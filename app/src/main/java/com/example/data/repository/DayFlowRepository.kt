package com.example.data.repository

import com.example.AppConfig
import com.example.data.local.AccountDao
import com.example.data.local.BillDao
import com.example.data.local.BudgetDao
import com.example.data.local.CategoryBudgetDao
import com.example.data.local.CustomCategoryDao
import com.example.data.local.ExpenseDao
import com.example.data.local.RoutineDao
import com.example.data.local.SavingsGoalDao
import com.example.data.local.TaskDao
import com.example.data.model.AccountEntity
import com.example.data.model.AccountType
import com.example.data.model.AccountWithBalance
import com.example.data.model.BillEntity
import com.example.data.model.BillFrequency
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryBudgetEntity
import com.example.data.model.CategorySpend
import com.example.data.model.CustomCategoryEntity
import com.example.data.model.DailySummary
import com.example.data.model.ExpenseCategory
import com.example.data.model.ExpenseEntity
import com.example.data.model.PaymentMethod
import com.example.data.model.RoutineCategory
import com.example.data.model.RoutineCompletionEntity
import com.example.data.model.RoutineEntity
import com.example.data.model.RoutineWithStatus
import com.example.data.model.SavingsContributionEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.SavingsGoalWithProgress
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.data.model.TimeOfDay
import com.example.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DayFlowRepository(
    private val routineDao: RoutineDao,
    private val expenseDao: ExpenseDao,
    private val budgetDao: BudgetDao,
    private val categoryBudgetDao: CategoryBudgetDao,
    private val accountDao: AccountDao,
    private val savingsGoalDao: SavingsGoalDao,
    private val billDao: BillDao,
    private val taskDao: TaskDao,
    private val customCategoryDao: CustomCategoryDao
) {
    val allRoutines: Flow<List<RoutineEntity>> = routineDao.getAllRoutines()
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllTransactions()
    val allCompletions: Flow<List<RoutineCompletionEntity>> = routineDao.getAllCompletions()
    val allAccounts: Flow<List<AccountEntity>> = accountDao.getAllAccounts()
    val allSavingsGoals: Flow<List<SavingsGoalEntity>> = savingsGoalDao.getAllGoals()
    val allSavingsContributions: Flow<List<SavingsContributionEntity>> = savingsGoalDao.getAllContributions()
    val allBills: Flow<List<BillEntity>> = billDao.getAllBills()
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val allCustomCategories: Flow<List<CustomCategoryEntity>> = customCategoryDao.getAllCustomCategories()

    // Accounts with calculated balances
    val accountsWithBalances: Flow<List<AccountWithBalance>> = combine(
        accountDao.getAllAccounts(),
        expenseDao.getAllTransactions()
    ) { accounts, transactions ->
        accounts.map { account ->
            val accountTx = transactions.filter { it.accountId == account.id }
            val totalIncome = accountTx.filter { it.type == TransactionType.INCOME.name }.sumOf { it.amount }
            val totalExpense = accountTx.filter { it.type == TransactionType.EXPENSE.name }.sumOf { it.amount }
            val currentBalance = account.initialBalance + totalIncome - totalExpense

            AccountWithBalance(
                account = account,
                currentBalance = currentBalance,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                transactionCount = accountTx.size
            )
        }
    }

    // Savings goals with progress
    val savingsGoalsWithProgress: Flow<List<SavingsGoalWithProgress>> = combine(
        savingsGoalDao.getAllGoals(),
        savingsGoalDao.getAllContributions()
    ) { goals, contributions ->
        val contribMap = contributions.groupBy { it.goalId }
        val todayEpoch = LocalDate.now().toEpochDay()

        goals.map { goal ->
            val goalContribs = contribMap[goal.id] ?: emptyList()
            val totalContributed = goalContribs.sumOf { if (it.isWithdrawal) -it.amount else it.amount }
            val currentAmount = (goal.currentAmount + totalContributed).coerceAtLeast(0.0)
            val progress = if (goal.targetAmount > 0) {
                ((currentAmount / goal.targetAmount).toFloat()).coerceIn(0f, 1f)
            } else 0f
            val remaining = (goal.targetAmount - currentAmount).coerceAtLeast(0.0)
            val daysRemaining = goal.targetDateEpochDay?.let { (it - todayEpoch).coerceAtLeast(0L) }

            SavingsGoalWithProgress(
                goal = goal.copy(currentAmount = currentAmount),
                progressPercentage = progress,
                remainingAmount = remaining,
                daysRemaining = daysRemaining,
                contributions = goalContribs
            )
        }
    }

    fun getRoutinesWithStatusForDate(date: LocalDate): Flow<List<RoutineWithStatus>> {
        val epochDay = date.toEpochDay()
        val dayOfWeekIndex = date.dayOfWeek.value - 1
        val dayMask = 1 shl dayOfWeekIndex

        return combine(
            routineDao.getAllRoutines(),
            routineDao.getAllCompletions()
        ) { routines, completions ->
            val completionsMap = completions.groupBy { it.routineId }
            val activeForDay = routines.filter { (it.targetDaysOfWeekMask and dayMask) != 0 }

            activeForDay.map { routine ->
                val routineCompletions = completionsMap[routine.id]?.map { it.dateEpochDay }?.toSet() ?: emptySet()
                val isCompleted = routineCompletions.contains(epochDay)

                var streak = 0
                var checkDate = if (isCompleted) date else date.minusDays(1)
                while (routineCompletions.contains(checkDate.toEpochDay())) {
                    streak++
                    checkDate = checkDate.minusDays(1)
                }

                RoutineWithStatus(
                    routine = routine,
                    isCompletedToday = isCompleted,
                    currentStreak = streak,
                    totalCompletions = routineCompletions.size
                )
            }
        }
    }

    suspend fun toggleRoutineCompletion(routineId: Long, date: LocalDate) {
        val epochDay = date.toEpochDay()
        val completion = RoutineCompletionEntity(
            routineId = routineId,
            dateEpochDay = epochDay,
            completedAtMillis = System.currentTimeMillis()
        )
        try {
            routineDao.insertCompletion(completion)
        } catch (e: Exception) {
            routineDao.deleteCompletion(routineId, epochDay)
        }
    }

    suspend fun markRoutineStatus(routineId: Long, date: LocalDate, completed: Boolean) {
        val epochDay = date.toEpochDay()
        if (completed) {
            routineDao.insertCompletion(
                RoutineCompletionEntity(
                    routineId = routineId,
                    dateEpochDay = epochDay,
                    completedAtMillis = System.currentTimeMillis()
                )
            )
        } else {
            routineDao.deleteCompletion(routineId, epochDay)
        }
    }

    // Routines CRUD
    suspend fun insertRoutine(routine: RoutineEntity): Long = routineDao.insertRoutine(routine)
    suspend fun updateRoutine(routine: RoutineEntity) = routineDao.updateRoutine(routine)
    suspend fun deleteRoutine(routine: RoutineEntity) {
        routineDao.deleteRoutine(routine)
        routineDao.deleteAllCompletionsForRoutine(routine.id)
    }

    // Expenses CRUD
    suspend fun insertExpense(expense: ExpenseEntity): Long = expenseDao.insertTransaction(expense)
    suspend fun updateExpense(expense: ExpenseEntity) = expenseDao.updateTransaction(expense)
    suspend fun deleteExpense(expense: ExpenseEntity) = expenseDao.deleteTransaction(expense)

    // Budgets CRUD
    fun getBudgetForMonth(monthKey: String): Flow<BudgetEntity?> = budgetDao.getBudgetForMonth(monthKey)
    suspend fun setBudget(budget: BudgetEntity) = budgetDao.setBudget(budget)
    fun getCategoryBudgetsForMonth(monthKey: String): Flow<List<CategoryBudgetEntity>> = categoryBudgetDao.getCategoryBudgetsForMonth(monthKey)
    suspend fun insertCategoryBudget(categoryBudget: CategoryBudgetEntity) = categoryBudgetDao.insertCategoryBudget(categoryBudget)

    // Accounts CRUD
    suspend fun insertAccount(account: AccountEntity): Long = accountDao.insertAccount(account)
    suspend fun updateAccount(account: AccountEntity) = accountDao.updateAccount(account)
    suspend fun deleteAccount(account: AccountEntity) = accountDao.deleteAccount(account)
    suspend fun setDefaultAccount(id: Long) {
        accountDao.clearDefaultAccount()
        accountDao.setDefaultAccount(id)
    }

    // Savings Goals CRUD
    suspend fun insertSavingsGoal(goal: SavingsGoalEntity): Long = savingsGoalDao.insertGoal(goal)
    suspend fun updateSavingsGoal(goal: SavingsGoalEntity) = savingsGoalDao.updateGoal(goal)
    suspend fun deleteSavingsGoal(goal: SavingsGoalEntity) {
        savingsGoalDao.deleteGoal(goal)
        savingsGoalDao.deleteAllContributionsForGoal(goal.id)
    }
    suspend fun addSavingsContribution(goalId: Long, amount: Double, note: String, isWithdrawal: Boolean = false) {
        savingsGoalDao.insertContribution(
            SavingsContributionEntity(
                goalId = goalId,
                amount = amount,
                dateEpochDay = LocalDate.now().toEpochDay(),
                note = note,
                isWithdrawal = isWithdrawal
            )
        )
    }

    // Bills & Subscriptions CRUD
    suspend fun insertBill(bill: BillEntity): Long = billDao.insertBill(bill)
    suspend fun updateBill(bill: BillEntity) = billDao.updateBill(bill)
    suspend fun deleteBill(bill: BillEntity) = billDao.deleteBill(bill)
    suspend fun setBillPaidStatus(id: Long, isPaid: Boolean) = billDao.setBillPaidStatus(id, isPaid)

    // Tasks CRUD
    fun getTasksForDate(date: LocalDate): Flow<List<TaskEntity>> = taskDao.getTasksForDate(date.toEpochDay())
    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
    suspend fun setTaskCompleted(id: Long, completed: Boolean) = taskDao.setTaskCompleted(id, completed, if (completed) System.currentTimeMillis() else null)

    // Custom Categories CRUD
    suspend fun insertCustomCategory(category: CustomCategoryEntity): Long = customCategoryDao.insertCategory(category)
    suspend fun updateCustomCategory(category: CustomCategoryEntity) = customCategoryDao.updateCategory(category)
    suspend fun deleteCustomCategory(category: CustomCategoryEntity) = customCategoryDao.deleteCategory(category)
    suspend fun deleteCustomCategoryById(id: Long) = customCategoryDao.deleteCategoryById(id)

    /**
     * Seeds demo/showcase data on a fresh install.
     *
     * Guard: only runs if no routine with id=1 exists. This means the block executes
     * exactly once on a clean database and never again on subsequent launches.
     * Existing user data is NEVER modified by this function.
     *
     * Seed data overview:
     *   1. Accounts          — 4 demo accounts with realistic opening balances
     *   2. Savings Goals     — 3 goals at various progress stages
     *   3. Bills             — 3 recurring subscriptions
     *   4. Tasks             — 3 prioritized tasks for today / upcoming
     *   5. Routines          — 6 daily habits with 3-day completion history for streak display
     *   6. Budget + Categories — monthly budget and per-category limits
     *   7. Transactions      — realistic income + expense transactions
     *   8. Custom Categories — sourced from AppConfig.DEFAULT_CUSTOM_CATEGORIES
     *                          (edit AppConfig.kt to change the default set)
     */
    suspend fun seedInitialDataIfEmpty(currentDate: LocalDate) {
        val routinesCount = routineDao.getRoutineById(1)
        if (routinesCount == null) {
            // 1. Seed Accounts
            val accMain = accountDao.insertAccount(
                AccountEntity(
                    id = 1,
                    name = "Main Checking Bank",
                    type = AccountType.BANK.name,
                    initialBalance = 2450.00,
                    colorHex = 0xFF6366F1,
                    iconKey = "account_balance",
                    isDefault = true,
                    accountNumberMask = "•••• 4829"
                )
            )
            val accCash = accountDao.insertAccount(
                AccountEntity(
                    id = 2,
                    name = "Physical Cash Wallet",
                    type = AccountType.CASH.name,
                    initialBalance = 160.00,
                    colorHex = 0xFF10B981,
                    iconKey = "payments",
                    isDefault = false,
                    accountNumberMask = "Cash"
                )
            )
            val accSavings = accountDao.insertAccount(
                AccountEntity(
                    id = 3,
                    name = "Emergency Savings Pot",
                    type = AccountType.SAVINGS.name,
                    initialBalance = 5200.00,
                    colorHex = 0xFFF59E0B,
                    iconKey = "savings",
                    isDefault = false,
                    accountNumberMask = "•••• 9104"
                )
            )
            val accDigital = accountDao.insertAccount(
                AccountEntity(
                    id = 4,
                    name = "Digital Wallet / UPI",
                    type = AccountType.DIGITAL_WALLET.name,
                    initialBalance = 280.50,
                    colorHex = 0xFFEC4899,
                    iconKey = "account_balance_wallet",
                    isDefault = false,
                    accountNumberMask = "PayApp"
                )
            )

            // 2. Seed Savings Goals
            savingsGoalDao.insertGoal(
                SavingsGoalEntity(
                    id = 1,
                    name = "MacBook Pro M3 Max",
                    targetAmount = 2500.0,
                    currentAmount = 1850.0,
                    targetDateEpochDay = currentDate.plusMonths(3).toEpochDay(),
                    iconKey = "laptop_mac",
                    colorHex = 0xFF6366F1,
                    note = "Workstation upgrade for high-performance mobile dev"
                )
            )
            savingsGoalDao.insertGoal(
                SavingsGoalEntity(
                    id = 2,
                    name = "Tokyo Vacation Fund",
                    targetAmount = 4000.0,
                    currentAmount = 2600.0,
                    targetDateEpochDay = currentDate.plusMonths(6).toEpochDay(),
                    iconKey = "flight_takeoff",
                    colorHex = 0xFFEC4899,
                    note = "Flights, ryokan stays, and culinary experiences"
                )
            )
            savingsGoalDao.insertGoal(
                SavingsGoalEntity(
                    id = 3,
                    name = "Emergency Buffer (6 Mo)",
                    targetAmount = 10000.0,
                    currentAmount = 5200.0,
                    targetDateEpochDay = currentDate.plusYears(1).toEpochDay(),
                    iconKey = "shield",
                    colorHex = 0xFF10B981,
                    note = "Peace-of-mind liquidity reserve"
                )
            )

            // 3. Seed Recurring Bills & Subscriptions
            billDao.insertBill(
                BillEntity(
                    id = 1,
                    title = "High-speed Fiber Internet",
                    amount = 55.00,
                    category = ExpenseCategory.BILLS.name,
                    frequency = BillFrequency.MONTHLY.name,
                    dueDayOfMonth = 5,
                    isPaidThisCycle = true,
                    reminderEnabled = true,
                    paymentMethod = PaymentMethod.BANK_TRANSFER.name,
                    accountId = accMain,
                    iconKey = "wifi",
                    colorHex = 0xFF3B82F6
                )
            )
            billDao.insertBill(
                BillEntity(
                    id = 2,
                    title = "Cloud & AI Pro Plan",
                    amount = 20.00,
                    category = ExpenseCategory.EDUCATION.name,
                    frequency = BillFrequency.MONTHLY.name,
                    dueDayOfMonth = 14,
                    isPaidThisCycle = false,
                    reminderEnabled = true,
                    paymentMethod = PaymentMethod.CARD.name,
                    accountId = accMain,
                    iconKey = "cloud",
                    colorHex = 0xFF8B5CF6
                )
            )
            billDao.insertBill(
                BillEntity(
                    id = 3,
                    title = "Gym & Wellness Club",
                    amount = 45.00,
                    category = ExpenseCategory.HEALTH.name,
                    frequency = BillFrequency.MONTHLY.name,
                    dueDayOfMonth = 18,
                    isPaidThisCycle = false,
                    reminderEnabled = true,
                    paymentMethod = PaymentMethod.CARD.name,
                    accountId = accMain,
                    iconKey = "fitness_center",
                    colorHex = 0xFF10B981
                )
            )

            // 4. Seed Tasks
            val todayEpoch = currentDate.toEpochDay()
            taskDao.insertTask(
                TaskEntity(
                    title = "Review Monthly Savings Allocations",
                    note = "Transfer $250 surplus to emergency fund",
                    priority = TaskPriority.HIGH.name,
                    dueDateEpochDay = todayEpoch,
                    dueTimeMinutes = 11 * 60,
                    isCompleted = false,
                    category = "Finance"
                )
            )
            taskDao.insertTask(
                TaskEntity(
                    title = "Grocery Restock: High-Protein & Greens",
                    note = "Eggs, chicken breast, oats, spinach, berries",
                    priority = TaskPriority.MEDIUM.name,
                    dueDateEpochDay = todayEpoch,
                    dueTimeMinutes = 17 * 60,
                    isCompleted = false,
                    category = "Household"
                )
            )
            taskDao.insertTask(
                TaskEntity(
                    title = "Submit Quarterly Tax Documentation",
                    note = "Download statements and compile business expenses",
                    priority = TaskPriority.URGENT.name,
                    dueDateEpochDay = todayEpoch + 2,
                    dueTimeMinutes = 15 * 60,
                    isCompleted = false,
                    category = "Work"
                )
            )

            // 5. Seed Routines
            val r1 = routineDao.insertRoutine(
                RoutineEntity(
                    title = "Morning Hydration & Sunlight",
                    note = "Drink 500ml water and get 10 mins outdoor light",
                    category = RoutineCategory.WELLNESS.name,
                    timeMinutes = 7 * 60,
                    targetDaysOfWeekMask = 127,
                    timeOfDay = TimeOfDay.MORNING.name,
                    iconKey = "local_drink",
                    colorHex = 0xFF0284C7
                )
            )
            val r2 = routineDao.insertRoutine(
                RoutineEntity(
                    title = "30-Min HIIT or Run",
                    note = "Cardio warmup followed by core workout",
                    category = RoutineCategory.FITNESS.name,
                    timeMinutes = 7 * 60 + 30,
                    targetDaysOfWeekMask = 127,
                    timeOfDay = TimeOfDay.MORNING.name,
                    iconKey = "fitness_center",
                    colorHex = 0xFF10B981
                )
            )
            val r3 = routineDao.insertRoutine(
                RoutineEntity(
                    title = "Deep Focus Work Block",
                    note = "High-priority tasks with phone in DND mode",
                    category = RoutineCategory.PRODUCTIVITY.name,
                    timeMinutes = 9 * 60 + 30,
                    targetDaysOfWeekMask = 31,
                    timeOfDay = TimeOfDay.MORNING.name,
                    iconKey = "work",
                    colorHex = 0xFF6366F1
                )
            )
            val r4 = routineDao.insertRoutine(
                RoutineEntity(
                    title = "Afternoon Recharge & Walk",
                    note = "Step outside for fresh air, 2000 steps",
                    category = RoutineCategory.MINDFULNESS.name,
                    timeMinutes = 14 * 60 + 30,
                    targetDaysOfWeekMask = 127,
                    timeOfDay = TimeOfDay.AFTERNOON.name,
                    iconKey = "directions_walk",
                    colorHex = 0xFFF59E0B
                )
            )
            val r5 = routineDao.insertRoutine(
                RoutineEntity(
                    title = "Read 20 Pages of Book",
                    note = "Fiction / Non-fiction reading before screen wind-down",
                    category = RoutineCategory.LEARNING.name,
                    timeMinutes = 20 * 60 + 30,
                    targetDaysOfWeekMask = 127,
                    timeOfDay = TimeOfDay.EVENING.name,
                    iconKey = "menu_book",
                    colorHex = 0xFF8B5CF6
                )
            )
            val r6 = routineDao.insertRoutine(
                RoutineEntity(
                    title = "Daily Journal & Tomorrow Plan",
                    note = "Review today's wins, log expenses, set 3 goals for tomorrow",
                    category = RoutineCategory.PERSONAL.name,
                    timeMinutes = 21 * 60 + 45,
                    targetDaysOfWeekMask = 127,
                    timeOfDay = TimeOfDay.NIGHT.name,
                    iconKey = "edit_note",
                    colorHex = 0xFFEC4899
                )
            )

            // Seed completions for past 3 days for habit streaks
            for (offset in 1..3) {
                val pastEpoch = todayEpoch - offset
                routineDao.insertCompletion(RoutineCompletionEntity(r1, pastEpoch))
                routineDao.insertCompletion(RoutineCompletionEntity(r2, pastEpoch))
                routineDao.insertCompletion(RoutineCompletionEntity(r5, pastEpoch))
                if (offset <= 2) {
                    routineDao.insertCompletion(RoutineCompletionEntity(r3, pastEpoch))
                    routineDao.insertCompletion(RoutineCompletionEntity(r6, pastEpoch))
                }
            }
            routineDao.insertCompletion(RoutineCompletionEntity(r1, todayEpoch))
            routineDao.insertCompletion(RoutineCompletionEntity(r2, todayEpoch))

            // Seed Budget
            val monthKey = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM"))
            budgetDao.setBudget(BudgetEntity(monthKey = monthKey, monthlyLimit = 1800.0, dailyTarget = 60.0))

            // Seed Category Budgets
            categoryBudgetDao.insertCategoryBudget(CategoryBudgetEntity(monthKey = monthKey, category = ExpenseCategory.FOOD.name, limitAmount = 450.0))
            categoryBudgetDao.insertCategoryBudget(CategoryBudgetEntity(monthKey = monthKey, category = ExpenseCategory.GROCERIES.name, limitAmount = 400.0))
            categoryBudgetDao.insertCategoryBudget(CategoryBudgetEntity(monthKey = monthKey, category = ExpenseCategory.TRANSPORT.name, limitAmount = 200.0))
            categoryBudgetDao.insertCategoryBudget(CategoryBudgetEntity(monthKey = monthKey, category = ExpenseCategory.BILLS.name, limitAmount = 250.0))
            categoryBudgetDao.insertCategoryBudget(CategoryBudgetEntity(monthKey = monthKey, category = ExpenseCategory.ENTERTAINMENT.name, limitAmount = 150.0))

            // Seed Initial Expenses & Income
            expenseDao.insertTransaction(
                ExpenseEntity(
                    title = "Monthly Salary Deposit",
                    amount = 3500.0,
                    type = TransactionType.INCOME.name,
                    category = ExpenseCategory.SALARY.name,
                    paymentMethod = PaymentMethod.BANK_TRANSFER.name,
                    accountId = accMain,
                    dateEpochDay = todayEpoch - 5,
                    note = "Direct payroll deposit"
                )
            )
            expenseDao.insertTransaction(
                ExpenseEntity(
                    title = "Freelance UI Consulting",
                    amount = 650.0,
                    type = TransactionType.INCOME.name,
                    category = ExpenseCategory.BUSINESS.name,
                    paymentMethod = PaymentMethod.BANK_TRANSFER.name,
                    accountId = accMain,
                    dateEpochDay = todayEpoch - 1,
                    note = "Milestone 2 deliverable payment"
                )
            )
            expenseDao.insertTransaction(
                ExpenseEntity(
                    title = "Weekly Grocery Haul",
                    amount = 84.50,
                    type = TransactionType.EXPENSE.name,
                    category = ExpenseCategory.GROCERIES.name,
                    paymentMethod = PaymentMethod.CARD.name,
                    accountId = accMain,
                    dateEpochDay = todayEpoch - 2,
                    note = "Fresh vegetables, fruits, protein, dairy"
                )
            )
            expenseDao.insertTransaction(
                ExpenseEntity(
                    title = "High-speed Fiber Internet",
                    amount = 55.00,
                    type = TransactionType.EXPENSE.name,
                    category = ExpenseCategory.BILLS.name,
                    paymentMethod = PaymentMethod.BANK_TRANSFER.name,
                    accountId = accMain,
                    dateEpochDay = todayEpoch - 4,
                    note = "Monthly home internet plan"
                )
            )
            expenseDao.insertTransaction(
                ExpenseEntity(
                    title = "Coffee & Healthy Breakfast",
                    amount = 12.50,
                    type = TransactionType.EXPENSE.name,
                    category = ExpenseCategory.FOOD.name,
                    paymentMethod = PaymentMethod.DIGITAL_WALLET.name,
                    accountId = accDigital,
                    dateEpochDay = todayEpoch,
                    note = "Matcha latte + avocado toast"
                )
            )
            expenseDao.insertTransaction(
                ExpenseEntity(
                    title = "Metro Transit Pass Recharge",
                    amount = 30.00,
                    type = TransactionType.EXPENSE.name,
                    category = ExpenseCategory.TRANSPORT.name,
                    paymentMethod = PaymentMethod.CARD.name,
                    accountId = accMain,
                    dateEpochDay = todayEpoch - 1,
                    note = "Commuter rail & bus reload"
                )
            )
            expenseDao.insertTransaction(
                ExpenseEntity(
                    title = "Productivity Books & Learning",
                    amount = 18.20,
                    type = TransactionType.EXPENSE.name,
                    category = ExpenseCategory.EDUCATION.name,
                    paymentMethod = PaymentMethod.CARD.name,
                    accountId = accMain,
                    dateEpochDay = todayEpoch - 3,
                    note = "Paperback edition"
                )
            )

            // 6. Seed Default Custom Categories
            // Source of truth: AppConfig.DEFAULT_CUSTOM_CATEGORIES
            // To add, remove, or rename a default category, edit AppConfig.kt only.
            // This block is only executed once on a fresh install — existing user categories
            // are never modified on upgrade.
            AppConfig.DEFAULT_CUSTOM_CATEGORIES.forEach { cat ->
                customCategoryDao.insertCategory(
                    CustomCategoryEntity(
                        name = cat.name,
                        type = cat.type,
                        iconKey = cat.iconKey,
                        colorHex = cat.colorHex,
                        isSystemDefault = true
                    )
                )
            }
        }
    }
}
