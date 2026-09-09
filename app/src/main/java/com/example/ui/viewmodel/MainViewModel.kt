package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.AppConfig
import com.example.data.local.AppDatabase
import com.example.data.model.AccountEntity
import com.example.data.model.AccountType
import com.example.data.model.AccountWithBalance
import com.example.data.model.AchievementBadge
import com.example.data.model.BillEntity
import com.example.data.model.BillFrequency
import com.example.data.model.BlueprintPack
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryBudgetEntity
import com.example.data.model.CategorySpend
import com.example.data.model.CustomCategoryEntity
import com.example.data.model.ExpenseCategory
import com.example.data.model.ExpenseEntity
import com.example.data.model.PaymentMethod
import com.example.data.model.RoutineCategory
import com.example.data.model.RoutineCompletionEntity
import com.example.data.model.RoutineEntity
import com.example.data.model.RoutineWithStatus
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.SavingsGoalWithProgress
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.data.model.TimeOfDay
import com.example.data.model.TransactionType
import com.example.data.repository.DayFlowRepository
import com.example.data.service.AIService
import com.example.data.service.FinancialInsight
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class AppTab(val title: String) {
    TODAY("Today"),
    FINANCE("Finance"),
    SAVINGS("Savings"),
    BILLS("Bills"),
    ROUTINES("Routines"),
    ANALYTICS("Analytics")
}

data class DayFlowUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val currentTab: AppTab = AppTab.TODAY,
    val routinesForDate: List<RoutineWithStatus> = emptyList(),
    val allRoutines: List<RoutineEntity> = emptyList(),
    val expensesForDate: List<ExpenseEntity> = emptyList(),
    val allExpenses: List<ExpenseEntity> = emptyList(),
    val accounts: List<AccountWithBalance> = emptyList(),
    val savingsGoals: List<SavingsGoalWithProgress> = emptyList(),
    val bills: List<BillEntity> = emptyList(),
    val tasks: List<TaskEntity> = emptyList(),
    val categoryBudgets: List<CategoryBudgetEntity> = emptyList(),
    val aiInsights: List<FinancialInsight> = emptyList(),
    val currentBudget: BudgetEntity? = null,
    val totalAccountBalance: Double = 0.0,
    val monthlyTotalExpense: Double = 0.0,
    val monthlyTotalIncome: Double = 0.0,
    val todayTotalExpense: Double = 0.0,
    val todayTotalIncome: Double = 0.0,
    val routineCompletionRate: Float = 0f,
    val bestStreak: Int = 0,
    val totalCompletionsAllTime: Int = 0,
    val categorySpends: List<CategorySpend> = emptyList(),
    val currencySymbol: String = AppConfig.DEFAULT_CURRENCY_SYMBOL,
    val currencyCode: String = AppConfig.DEFAULT_CURRENCY_CODE,
    // Theme: DARK | LIGHT | SYSTEM — persisted to DataStore
    val themeMode: com.example.data.local.preferences.AppThemeMode =
        com.example.data.local.preferences.AppThemeMode.DARK,
    val isProUser: Boolean = false,
    val isPinEnabled: Boolean = false,
    val userPin: String = "",
    val isNotificationsEnabled: Boolean = AppConfig.FEATURE_NOTIFICATIONS_ENABLED,
    val isAiEnabled: Boolean = AppConfig.FEATURE_AI_INSIGHTS_ENABLED,
    val dayFlowScore: Int = 85,
    val achievements: List<AchievementBadge> = emptyList(),
    val isOnboardingComplete: Boolean = true
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DayFlowRepository
    private val preferencesRepository = com.example.data.local.preferences.UserPreferencesRepository(application)
    private val backupManager: com.example.data.service.BackupManager

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DayFlowRepository(
            routineDao = database.routineDao(),
            expenseDao = database.expenseDao(),
            budgetDao = database.budgetDao(),
            categoryBudgetDao = database.categoryBudgetDao(),
            accountDao = database.accountDao(),
            savingsGoalDao = database.savingsGoalDao(),
            billDao = database.billDao(),
            taskDao = database.taskDao(),
            customCategoryDao = database.customCategoryDao()
        )
        backupManager = com.example.data.service.BackupManager(database)
    }

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _currentTab = MutableStateFlow(AppTab.TODAY)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Dialog & UI Visibility states
    val showAddRoutineDialog = MutableStateFlow(false)
    val routineToEdit = MutableStateFlow<RoutineEntity?>(null)

    val showAddExpenseDialog = MutableStateFlow(false)
    val expenseToEdit = MutableStateFlow<ExpenseEntity?>(null)

    val showAddAccountDialog = MutableStateFlow(false)
    val accountToEdit = MutableStateFlow<AccountEntity?>(null)

    val showAddSavingsGoalDialog = MutableStateFlow(false)
    val savingsGoalToEdit = MutableStateFlow<SavingsGoalEntity?>(null)

    val showAddContributionDialog = MutableStateFlow(false)
    val savingsGoalForContribution = MutableStateFlow<SavingsGoalEntity?>(null)

    val showAddBillDialog = MutableStateFlow(false)
    val billToEdit = MutableStateFlow<BillEntity?>(null)

    val showAddTaskDialog = MutableStateFlow(false)
    val taskToEdit = MutableStateFlow<TaskEntity?>(null)

    val showCategoryBudgetsDialog = MutableStateFlow(false)
    val showAiInsightsDialog = MutableStateFlow(false)
    val showSearchFilterSheet = MutableStateFlow(false)
    val showOnboardingDialog = MutableStateFlow(false)

    val showBudgetDialog = MutableStateFlow(false)
    val showSettingsSheet = MutableStateFlow(false)

    // Dialogs
    val showBlueprintsDialog = MutableStateFlow(false)
    val showSubscriptionsDialog = MutableStateFlow(false)
    val showSecurityDialog = MutableStateFlow(false)
    val showBackupRestoreDialog = MutableStateFlow(false)
    val showLegalDialog = MutableStateFlow<String?>(null)

    // App Lock state
    val isAppLocked = MutableStateFlow(false)

    // User Preferences & Pro status
    val isProUser = MutableStateFlow(false)
    val isPinEnabled = MutableStateFlow(false)
    val userPin = MutableStateFlow("")
    val currencySymbol = MutableStateFlow(AppConfig.DEFAULT_CURRENCY_SYMBOL)
    val currencyCode = MutableStateFlow(AppConfig.DEFAULT_CURRENCY_CODE)
    val themeMode = MutableStateFlow(com.example.data.local.preferences.AppThemeMode.DARK)
    val isNotificationsEnabled = MutableStateFlow(AppConfig.FEATURE_NOTIFICATIONS_ENABLED)
    val isAiEnabled = MutableStateFlow(AppConfig.FEATURE_AI_INSIGHTS_ENABLED)
    val isOnboardingComplete = MutableStateFlow(true)

    // Undo Cache for deleted entities
    private var lastDeletedRoutine: RoutineEntity? = null
    private var lastDeletedExpense: ExpenseEntity? = null

    val snackbarMessage = MutableStateFlow<String?>(null)
    val hasUndoAction = MutableStateFlow(false)

    init {
        // Seed demo data in debug builds only.
        // In release builds (production) the database starts empty so real users
        // are never shown invented financial records on first launch.
        // Buyers can load showcase data at any time via Settings → Backup & Restore
        // → "Load Demo Data", which calls restoreDemoShowcaseData() explicitly.
        if (com.example.BuildConfig.DEBUG) {
            viewModelScope.launch {
                repository.seedInitialDataIfEmpty(LocalDate.now())
            }
        }
        // Hydrate all in-memory StateFlows from persisted DataStore on every launch.
        // This ensures currency, PIN state, theme, notification settings and onboarding
        // status survive process death and are never reset to defaults on restart.
        viewModelScope.launch {
            preferencesRepository.userPreferencesFlow.collect { prefs ->
                currencySymbol.value = prefs.currencySymbol
                currencyCode.value = prefs.currencyCode
                isProUser.value = prefs.isProUser
                isPinEnabled.value = prefs.isPinLockEnabled
                userPin.value = prefs.pinHash
                themeMode.value = prefs.themeMode
                isNotificationsEnabled.value = prefs.isNotificationsEnabled
                isAiEnabled.value = prefs.isAiEnabled
                isOnboardingComplete.value = prefs.isOnboardingCompleted
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _routinesForDateFlow = _selectedDate.flatMapLatest { date ->
        repository.getRoutinesWithStatusForDate(date)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _tasksForDateFlow = _selectedDate.flatMapLatest { date ->
        repository.getTasksForDate(date)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _currentMonthBudgetFlow = _selectedDate.flatMapLatest { date ->
        val monthKey = date.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        repository.getBudgetForMonth(monthKey)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DayFlowUiState> = combine(
        _selectedDate,
        _currentTab,
        _routinesForDateFlow,
        _tasksForDateFlow,
        repository.allExpenses,
        repository.accountsWithBalances,
        repository.savingsGoalsWithProgress,
        repository.allBills,
        repository.allRoutines,
        currencySymbol,
        isProUser,
        isPinEnabled,
        userPin,
        _currentMonthBudgetFlow
    ) { params ->
        val date = params[0] as LocalDate
        val tab = params[1] as AppTab
        val routinesWithStatus = params[2] as List<RoutineWithStatus>
        val tasks = params[3] as List<TaskEntity>
        val allExpenses = params[4] as List<ExpenseEntity>
        val accounts = params[5] as List<AccountWithBalance>
        val savingsGoals = params[6] as List<SavingsGoalWithProgress>
        val bills = params[7] as List<BillEntity>
        val allRoutines = params[8] as List<RoutineEntity>
        val cur = params[9] as String
        val pro = params[10] as Boolean
        val pinEn = params[11] as Boolean
        val pin = params[12] as String
        @Suppress("UNCHECKED_CAST")
        val budget = params[13] as BudgetEntity?

        // Currency code and other prefs are kept in sync by the preferences collector in init {}
        val curCode = currencyCode.value
        val onbDone = isOnboardingComplete.value
        val currentThemeMode = themeMode.value
        val notificationsOn = isNotificationsEnabled.value
        val aiOn = isAiEnabled.value

        val dateEpoch = date.toEpochDay()
        val expensesOnDate = allExpenses.filter { it.dateEpochDay == dateEpoch }

        // Filter for the selected month
        val firstDayOfMonthEpoch = date.withDayOfMonth(1).toEpochDay()
        val lastDayOfMonthEpoch = date.withDayOfMonth(date.lengthOfMonth()).toEpochDay()
        val monthExpenses = allExpenses.filter { it.dateEpochDay in firstDayOfMonthEpoch..lastDayOfMonthEpoch }

        val monthlyExpenseSum = monthExpenses.filter { it.type == TransactionType.EXPENSE.name }.sumOf { it.amount }
        val monthlyIncomeSum = monthExpenses.filter { it.type == TransactionType.INCOME.name }.sumOf { it.amount }

        val todayExpenseSum = expensesOnDate.filter { it.type == TransactionType.EXPENSE.name }.sumOf { it.amount }
        val todayIncomeSum = expensesOnDate.filter { it.type == TransactionType.INCOME.name }.sumOf { it.amount }

        val totalAccountBalance = accounts.sumOf { it.currentBalance }

        val completedCount = routinesWithStatus.count { it.isCompletedToday }
        val totalRoutinesCount = routinesWithStatus.size
        val completionRate = if (totalRoutinesCount > 0) completedCount.toFloat() / totalRoutinesCount else 0f

        val bestStreak = routinesWithStatus.maxOfOrNull { it.currentStreak } ?: 0

        // Calculate category spends for current month
        val categorySpends = ExpenseCategory.entries.mapNotNull { category ->
            val catTotal = monthExpenses
                .filter { it.type == TransactionType.EXPENSE.name && it.category == category.name }
                .sumOf { it.amount }
            if (catTotal > 0) {
                CategorySpend(
                    category = category,
                    totalAmount = catTotal,
                    percentage = if (monthlyExpenseSum > 0) (catTotal / monthlyExpenseSum).toFloat() else 0f
                )
            } else null
        }.sortedByDescending { it.totalAmount }

        // Calculate AI Insights using the actual persisted monthly budget
        val monthlyBudgetLimit = budget?.monthlyLimit ?: AppConfig.DEFAULT_MONTHLY_BUDGET
        val insights = AIService.generateOfflineInsights(
            monthlyExpense = monthlyExpenseSum,
            monthlyIncome = monthlyIncomeSum,
            monthlyBudget = monthlyBudgetLimit,
            accounts = accounts,
            routines = routinesWithStatus,
            savingsGoals = savingsGoals,
            bills = bills,
            tasks = tasks,
            currencySymbol = cur
        )

        // DayFlow Score (0-100)
        var score = 72
        if (completionRate > 0.8f) score += 12 else if (completionRate > 0.5f) score += 6
        if (bestStreak >= 5) score += 8 else if (bestStreak >= 3) score += 4
        if (monthlyExpenseSum <= monthlyBudgetLimit) score += 5
        val finalScore = score.coerceIn(0, 100)

        val achievements = listOf(
            AchievementBadge(
                id = "first_step",
                title = "Genesis Step",
                description = "Complete your first daily routine",
                iconKey = "flag",
                isUnlocked = routinesWithStatus.any { it.isCompletedToday },
                progressPercent = if (routinesWithStatus.any { it.isCompletedToday }) 100 else 0
            ),
            AchievementBadge(
                id = "streak_3",
                title = "Habit Momentum",
                description = "Build a 3-day consecutive streak",
                iconKey = "local_fire_department",
                isUnlocked = bestStreak >= 3,
                progressPercent = (bestStreak.coerceAtMost(3) * 33)
            ),
            AchievementBadge(
                id = "streak_7",
                title = "Iron Discipline",
                description = "Achieve a flawless 7-day routine streak",
                iconKey = "workspace_premium",
                isUnlocked = bestStreak >= 7,
                progressPercent = (bestStreak.coerceAtMost(7) * 14)
            ),
            AchievementBadge(
                id = "budget_sentinel",
                title = "Budget Sentinel",
                description = "Log at least 5 ledger transactions",
                iconKey = "account_balance_wallet",
                isUnlocked = allExpenses.size >= 5,
                progressPercent = (allExpenses.size.coerceAtMost(5) * 20)
            )
        )

        DayFlowUiState(
            selectedDate = date,
            currentTab = tab,
            routinesForDate = routinesWithStatus,
            allRoutines = allRoutines,
            expensesForDate = expensesOnDate,
            allExpenses = allExpenses,
            accounts = accounts,
            savingsGoals = savingsGoals,
            bills = bills,
            tasks = tasks,
            aiInsights = insights,
            currentBudget = budget ?: BudgetEntity(
                monthKey = date.format(DateTimeFormatter.ofPattern("yyyy-MM")),
                monthlyLimit = AppConfig.DEFAULT_MONTHLY_BUDGET,
                dailyTarget = AppConfig.DEFAULT_DAILY_BUDGET
            ),
            totalAccountBalance = totalAccountBalance,
            monthlyTotalExpense = monthlyExpenseSum,
            monthlyTotalIncome = monthlyIncomeSum,
            todayTotalExpense = todayExpenseSum,
            todayTotalIncome = todayIncomeSum,
            routineCompletionRate = completionRate,
            bestStreak = bestStreak,
            totalCompletionsAllTime = routinesWithStatus.sumOf { it.totalCompletions },
            categorySpends = categorySpends,
            currencySymbol = cur,
            currencyCode = curCode,
            themeMode = currentThemeMode,
            isProUser = pro,
            isPinEnabled = pinEn,
            userPin = pin,
            isNotificationsEnabled = notificationsOn,
            isAiEnabled = aiOn,
            dayFlowScore = finalScore,
            achievements = achievements,
            isOnboardingComplete = onbDone
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DayFlowUiState()
    )

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    /**
     * Stores PIN security settings. [pinHash] is a SHA-256 hex hash produced by
     * [com.example.ui.components.hashPin] — the plain PIN never reaches the ViewModel.
     */
    fun setPinSecurity(enabled: Boolean, pinHash: String) {
        isPinEnabled.value = enabled
        userPin.value = pinHash
        viewModelScope.launch {
            preferencesRepository.setPinLock(enabled = enabled, hash = pinHash)
        }
        showSnackbarMessage(if (enabled) "Passcode lock enabled" else "Passcode lock disabled")
    }

    fun lockApp() {
        // userPin holds the SHA-256 hash (64 hex chars) when a PIN is configured
        if (isPinEnabled.value && userPin.value.length == 64) {
            isAppLocked.value = true
        }
    }

    fun unlockApp() {
        isAppLocked.value = false
    }

    fun completeOnboarding(currency: String, budget: Double) {
        val resolvedCode = AppConfig.SUPPORTED_CURRENCIES.firstOrNull { it.symbol == currency }?.code
            ?: AppConfig.DEFAULT_CURRENCY_CODE
        currencySymbol.value = currency
        currencyCode.value = resolvedCode
        viewModelScope.launch {
            preferencesRepository.setCurrency(symbol = currency, code = resolvedCode)
            preferencesRepository.setOnboardingCompleted(true)
        }
        isOnboardingComplete.value = true
        showOnboardingDialog.value = false
        saveBudget(budget, budget / 30.0)
        showSnackbarMessage("Welcome to DayFlow! Setup completed.")
    }

    fun installBlueprintPack(pack: BlueprintPack) {
        viewModelScope.launch {
            pack.routines.forEach { r ->
                val entity = RoutineEntity(
                    title = r.title,
                    note = r.note,
                    category = r.category.name,
                    timeMinutes = r.timeMinutes,
                    targetDaysOfWeekMask = r.targetDaysOfWeekMask,
                    timeOfDay = r.timeOfDay.name,
                    iconKey = r.iconKey,
                    colorHex = r.colorHex
                )
                repository.insertRoutine(entity)
            }
            pack.targetBudget?.let { budgetAmt ->
                saveBudget(budgetAmt, (budgetAmt / 30.0))
            }
            showSnackbarMessage("⚡ Installed ${pack.title} (${pack.routines.size} routines added)")
        }
    }

    // Routines
    fun openAddRoutine() {
        routineToEdit.value = null
        showAddRoutineDialog.value = true
    }

    fun openEditRoutine(routine: RoutineEntity) {
        routineToEdit.value = routine
        showAddRoutineDialog.value = true
    }

    fun saveRoutine(
        id: Long = 0,
        title: String,
        note: String,
        category: RoutineCategory,
        timeMinutes: Int,
        targetDaysOfWeekMask: Int,
        timeOfDay: TimeOfDay,
        iconKey: String,
        colorHex: Long
    ) {
        viewModelScope.launch {
            val entity = RoutineEntity(
                id = id,
                title = title.trim(),
                note = note.trim(),
                category = category.name,
                timeMinutes = timeMinutes,
                targetDaysOfWeekMask = targetDaysOfWeekMask,
                timeOfDay = timeOfDay.name,
                iconKey = iconKey,
                colorHex = colorHex
            )
            repository.insertRoutine(entity)
            showAddRoutineDialog.value = false
            routineToEdit.value = null
            showSnackbarMessage(if (id == 0L) "Routine created" else "Routine updated")
        }
    }

    fun deleteRoutine(routine: RoutineEntity) {
        viewModelScope.launch {
            lastDeletedRoutine = routine
            lastDeletedExpense = null
            repository.deleteRoutine(routine)
            showAddRoutineDialog.value = false
            routineToEdit.value = null
            showSnackbarMessage("Routine deleted", hasUndo = true)
        }
    }

    fun toggleRoutine(routineId: Long, isCurrentlyCompleted: Boolean) {
        viewModelScope.launch {
            repository.markRoutineStatus(
                routineId = routineId,
                date = _selectedDate.value,
                completed = !isCurrentlyCompleted
            )
        }
    }

    fun markAllRoutinesDoneForDate(done: Boolean) {
        viewModelScope.launch {
            val routines = uiState.value.routinesForDate
            routines.forEach { r ->
                if (r.isCompletedToday != done) {
                    repository.markRoutineStatus(
                        routineId = r.routine.id,
                        date = _selectedDate.value,
                        completed = done
                    )
                }
            }
            showSnackbarMessage(if (done) "All routines marked complete" else "Routines reset")
        }
    }

    // Expenses & Income
    fun openAddExpense() {
        expenseToEdit.value = null
        showAddExpenseDialog.value = true
    }

    fun openEditExpense(expense: ExpenseEntity) {
        expenseToEdit.value = expense
        showAddExpenseDialog.value = true
    }

    fun saveExpense(
        id: Long = 0,
        title: String,
        amount: Double,
        type: TransactionType,
        category: ExpenseCategory,
        paymentMethod: PaymentMethod,
        date: LocalDate,
        note: String
    ) {
        viewModelScope.launch {
            val defaultAccId = uiState.value.accounts.firstOrNull { it.account.isDefault }?.account?.id ?: 1L
            val entity = ExpenseEntity(
                id = id,
                title = title.trim(),
                amount = amount,
                type = type.name,
                category = category.name,
                paymentMethod = paymentMethod.name,
                accountId = defaultAccId,
                dateEpochDay = date.toEpochDay(),
                note = note.trim()
            )
            repository.insertExpense(entity)
            showAddExpenseDialog.value = false
            expenseToEdit.value = null
            showSnackbarMessage(if (id == 0L) "Transaction logged" else "Transaction updated")
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            lastDeletedExpense = expense
            lastDeletedRoutine = null
            repository.deleteExpense(expense)
            showAddExpenseDialog.value = false
            expenseToEdit.value = null
            showSnackbarMessage("Transaction deleted", hasUndo = true)
        }
    }

    // Accounts
    fun openAddAccount() {
        accountToEdit.value = null
        showAddAccountDialog.value = true
    }

    fun openEditAccount(account: AccountEntity) {
        accountToEdit.value = account
        showAddAccountDialog.value = true
    }

    fun saveAccount(
        id: Long = 0,
        name: String,
        type: AccountType,
        initialBalance: Double,
        colorHex: Long,
        iconKey: String,
        isDefault: Boolean,
        accountNumberMask: String
    ) {
        viewModelScope.launch {
            val entity = AccountEntity(
                id = id,
                name = name.trim(),
                type = type.name,
                initialBalance = initialBalance,
                colorHex = colorHex,
                iconKey = iconKey,
                isDefault = isDefault,
                accountNumberMask = accountNumberMask.trim()
            )
            repository.insertAccount(entity)
            if (isDefault && id > 0) {
                repository.setDefaultAccount(id)
            }
            showAddAccountDialog.value = false
            accountToEdit.value = null
            showSnackbarMessage(if (id == 0L) "Account created" else "Account updated")
        }
    }

    fun deleteAccount(account: AccountEntity) {
        viewModelScope.launch {
            repository.deleteAccount(account)
            showAddAccountDialog.value = false
            accountToEdit.value = null
            showSnackbarMessage("Account removed")
        }
    }

    // Savings Goals
    fun openAddSavingsGoal() {
        savingsGoalToEdit.value = null
        showAddSavingsGoalDialog.value = true
    }

    fun openEditSavingsGoal(goal: SavingsGoalEntity) {
        savingsGoalToEdit.value = goal
        showAddSavingsGoalDialog.value = true
    }

    fun openAddContribution(goal: SavingsGoalEntity) {
        savingsGoalForContribution.value = goal
        showAddContributionDialog.value = true
    }

    fun saveSavingsGoal(
        id: Long = 0,
        name: String,
        targetAmount: Double,
        currentAmount: Double,
        monthsTarget: Int,
        colorHex: Long,
        note: String
    ) {
        viewModelScope.launch {
            val targetDate = LocalDate.now().plusMonths(monthsTarget.toLong()).toEpochDay()
            val entity = SavingsGoalEntity(
                id = id,
                name = name.trim(),
                targetAmount = targetAmount,
                currentAmount = currentAmount,
                targetDateEpochDay = targetDate,
                colorHex = colorHex,
                note = note.trim()
            )
            repository.insertSavingsGoal(entity)
            showAddSavingsGoalDialog.value = false
            savingsGoalToEdit.value = null
            showSnackbarMessage(if (id == 0L) "Goal created" else "Goal updated")
        }
    }

    fun deleteSavingsGoal(goal: SavingsGoalEntity) {
        viewModelScope.launch {
            repository.deleteSavingsGoal(goal)
            showAddSavingsGoalDialog.value = false
            savingsGoalToEdit.value = null
            showSnackbarMessage("Goal removed")
        }
    }

    fun addSavingsContribution(goalId: Long, amount: Double, note: String, isWithdrawal: Boolean) {
        viewModelScope.launch {
            repository.addSavingsContribution(goalId, amount, note, isWithdrawal)
            showAddContributionDialog.value = false
            savingsGoalForContribution.value = null
            showSnackbarMessage(if (isWithdrawal) "Withdrawal recorded" else "Savings deposit added")
        }
    }

    // Bills & Subscriptions
    fun openAddBill() {
        billToEdit.value = null
        showAddBillDialog.value = true
    }

    fun openEditBill(bill: BillEntity) {
        billToEdit.value = bill
        showAddBillDialog.value = true
    }

    fun saveBill(
        id: Long = 0,
        title: String,
        amount: Double,
        category: ExpenseCategory,
        frequency: BillFrequency,
        dueDay: Int,
        reminder: Boolean,
        colorHex: Long
    ) {
        viewModelScope.launch {
            val entity = BillEntity(
                id = id,
                title = title.trim(),
                amount = amount,
                category = category.name,
                frequency = frequency.name,
                dueDayOfMonth = dueDay,
                reminderEnabled = reminder,
                colorHex = colorHex
            )
            repository.insertBill(entity)
            showAddBillDialog.value = false
            billToEdit.value = null
            showSnackbarMessage(if (id == 0L) "Bill added" else "Bill updated")
        }
    }

    fun deleteBill(bill: BillEntity) {
        viewModelScope.launch {
            repository.deleteBill(bill)
            showAddBillDialog.value = false
            billToEdit.value = null
            showSnackbarMessage("Bill removed")
        }
    }

    fun toggleBillPaid(billId: Long, currentPaid: Boolean) {
        viewModelScope.launch {
            repository.setBillPaidStatus(billId, !currentPaid)
            showSnackbarMessage(if (!currentPaid) "Marked as Paid" else "Marked as Unpaid")
        }
    }

    // Tasks
    fun openAddTask() {
        taskToEdit.value = null
        showAddTaskDialog.value = true
    }

    fun openEditTask(task: TaskEntity) {
        taskToEdit.value = task
        showAddTaskDialog.value = true
    }

    fun saveTask(
        id: Long = 0,
        title: String,
        note: String,
        priority: TaskPriority,
        category: String,
        reminder: Boolean,
        date: LocalDate
    ) {
        viewModelScope.launch {
            val entity = TaskEntity(
                id = id,
                title = title.trim(),
                note = note.trim(),
                priority = priority.name,
                category = category,
                reminderEnabled = reminder,
                dueDateEpochDay = date.toEpochDay()
            )
            repository.insertTask(entity)
            showAddTaskDialog.value = false
            taskToEdit.value = null
            showSnackbarMessage(if (id == 0L) "Task added" else "Task updated")
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
            showAddTaskDialog.value = false
            taskToEdit.value = null
            showSnackbarMessage("Task deleted")
        }
    }

    fun toggleTaskCompleted(taskId: Long, currentCompleted: Boolean) {
        viewModelScope.launch {
            repository.setTaskCompleted(taskId, !currentCompleted)
        }
    }

    // Budgets
    fun saveBudget(monthlyLimit: Double, dailyTarget: Double) {
        viewModelScope.launch {
            val currentMonth = _selectedDate.value.format(DateTimeFormatter.ofPattern("yyyy-MM"))
            repository.setBudget(
                BudgetEntity(
                    monthKey = currentMonth,
                    monthlyLimit = monthlyLimit,
                    dailyTarget = dailyTarget
                )
            )
            showBudgetDialog.value = false
            showSnackbarMessage("Budget updated: ${currencySymbol.value}${monthlyLimit.toInt()}/mo")
        }
    }

    fun setThemeMode(mode: com.example.data.local.preferences.AppThemeMode) {
        themeMode.value = mode
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        isNotificationsEnabled.value = enabled
        viewModelScope.launch {
            preferencesRepository.setNotificationsEnabled(enabled)
        }
    }

    fun setCurrency(symbol: String, code: String = "") {
        currencySymbol.value = symbol
        // Resolve the currency code from the symbol if not supplied directly
        val resolvedCode = if (code.isNotBlank()) code else
            AppConfig.SUPPORTED_CURRENCIES.firstOrNull { it.symbol == symbol }?.code
                ?: AppConfig.DEFAULT_CURRENCY_CODE
        currencyCode.value = resolvedCode
        viewModelScope.launch {
            preferencesRepository.setCurrency(symbol = symbol, code = resolvedCode)
        }
        showSnackbarMessage("Currency changed to $symbol")
    }

    fun openSettings() {
        showSettingsSheet.value = true
    }

    fun closeSettings() {
        showSettingsSheet.value = false
    }

    // Account Transfer
    fun executeAccountTransfer(fromAccountId: Long, toAccountId: Long, amount: Double, note: String = "") {
        viewModelScope.launch {
            if (fromAccountId == toAccountId) {
                showSnackbarMessage("Source and destination accounts must be different")
                return@launch
            }
            if (amount <= 0) {
                showSnackbarMessage("Transfer amount must be greater than zero")
                return@launch
            }
            val todayEpoch = _selectedDate.value.toEpochDay()
            // Outgoing transfer from source
            repository.insertExpense(
                ExpenseEntity(
                    title = "Transfer to Account #$toAccountId",
                    amount = amount,
                    type = TransactionType.EXPENSE.name,
                    category = ExpenseCategory.OTHER.name,
                    paymentMethod = PaymentMethod.BANK_TRANSFER.name,
                    accountId = fromAccountId,
                    dateEpochDay = todayEpoch,
                    note = if (note.isNotBlank()) "Transfer: $note" else "Account transfer"
                )
            )
            // Incoming transfer to destination
            repository.insertExpense(
                ExpenseEntity(
                    title = "Transfer from Account #$fromAccountId",
                    amount = amount,
                    type = TransactionType.INCOME.name,
                    category = ExpenseCategory.OTHER.name,
                    paymentMethod = PaymentMethod.BANK_TRANSFER.name,
                    accountId = toAccountId,
                    dateEpochDay = todayEpoch,
                    note = if (note.isNotBlank()) "Transfer: $note" else "Account transfer"
                )
            )
            showSnackbarMessage("Transferred ${currencySymbol.value}${String.format("%.2f", amount)} successfully")
        }
    }

    // Custom Categories
    fun addCustomCategory(name: String, type: String, iconKey: String = "category", colorHex: Long = 0xFF8B5CF6) {
        viewModelScope.launch {
            repository.insertCustomCategory(
                CustomCategoryEntity(
                    name = name.trim(),
                    type = type,
                    iconKey = iconKey,
                    colorHex = colorHex
                )
            )
            showSnackbarMessage("Category '$name' added")
        }
    }

    fun deleteCustomCategory(category: CustomCategoryEntity) {
        viewModelScope.launch {
            repository.deleteCustomCategory(category)
            showSnackbarMessage("Category '${category.name}' removed")
        }
    }

    fun undoLastDelete() {
        viewModelScope.launch {
            lastDeletedRoutine?.let { routine ->
                repository.insertRoutine(routine.copy(id = 0))
                lastDeletedRoutine = null
                hasUndoAction.value = false
                showSnackbarMessage("Routine restored")
            }
            lastDeletedExpense?.let { expense ->
                repository.insertExpense(expense.copy(id = 0))
                lastDeletedExpense = null
                hasUndoAction.value = false
                showSnackbarMessage("Transaction restored")
            }
        }
    }

    fun showSnackbarMessage(msg: String, hasUndo: Boolean = false) {
        snackbarMessage.value = msg
        hasUndoAction.value = hasUndo
    }

    fun clearSnackbar() {
        snackbarMessage.value = null
        hasUndoAction.value = false
    }

    fun exportTransactionsCsv(context: Context) {
        viewModelScope.launch {
            val expenses = uiState.value.allExpenses
            val fileName = "dayflow_transactions_${LocalDate.now()}.csv"
            val file = File(context.cacheDir, fileName)
            val writer = FileWriter(file)
            writer.append("ID,Date,Title,Amount,Type,Category,PaymentMethod,Note\n")
            expenses.forEach { exp ->
                // LocalDate.ofEpochDay is timezone-agnostic — correct for epoch-day values
                val dateStr = LocalDate.ofEpochDay(exp.dateEpochDay).toString()
                writer.append(
                    "${exp.id}," +
                    "$dateStr," +
                    "\"${csvEscape(exp.title)}\"," +
                    "${exp.amount}," +
                    "${exp.type}," +
                    "${exp.category}," +
                    "${exp.paymentMethod}," +
                    "\"${csvEscape(exp.note)}\"\n"
                )
            }
            writer.flush()
            writer.close()
            shareFile(context, file, "text/csv", "Export Transactions")
        }
    }

    fun exportRoutinesCsv(context: Context) {
        viewModelScope.launch {
            val routines = uiState.value.allRoutines
            val fileName = "dayflow_routines_${LocalDate.now()}.csv"
            val file = File(context.cacheDir, fileName)
            val writer = FileWriter(file)
            writer.append("ID,Title,Category,TimeMinutes,TimeOfDay,DaysMask,Note\n")
            routines.forEach { r ->
                writer.append(
                    "${r.id}," +
                    "\"${csvEscape(r.title)}\"," +
                    "${r.category}," +
                    "${r.timeMinutes}," +
                    "${r.timeOfDay}," +
                    "${r.targetDaysOfWeekMask}," +
                    "\"${csvEscape(r.note)}\"\n"
                )
            }
            writer.flush()
            writer.close()
            shareFile(context, file, "text/csv", "Export Routines")
        }
    }

    /**
     * Sanitises a string for safe inclusion inside a double-quoted CSV cell.
     *
     * Rules applied:
     * 1. Escape embedded double-quotes by doubling them (RFC 4180).
     * 2. Strip leading formula-injection characters (=, +, -, @, TAB, CR) that
     *    spreadsheet applications interpret as formula prefixes when a cell value
     *    starts with them. We strip rather than prefix so the exported text remains
     *    human-readable and the column schema stays intact.
     */
    private fun csvEscape(value: String): String {
        // Remove leading formula-injection chars
        val stripped = value.trimStart('=', '+', '-', '@', '\t', '\r')
        // Escape internal double-quotes per RFC 4180
        return stripped.replace("\"", "\"\"").replace("\n", " ")
    }

    fun exportFullJsonBackup(context: Context) {
        viewModelScope.launch {
            try {
                val jsonString = backupManager.createJsonBackup()
                val fileName = "dayflow_backup_${LocalDate.now()}.json"
                val file = File(context.cacheDir, fileName)
                file.writeText(jsonString)
                shareFile(context, file, "application/json", "Export DayFlow JSON Backup")
                showSnackbarMessage("Backup generated successfully")
            } catch (e: Exception) {
                showSnackbarMessage("Failed to export backup: ${e.localizedMessage}")
            }
        }
    }

    fun restoreFromJsonBackup(jsonString: String) {
        viewModelScope.launch {
            val result = backupManager.restoreFromJson(jsonString)
            if (result.isSuccess) {
                showSnackbarMessage("Data restored successfully from backup")
            } else {
                showSnackbarMessage("Restore failed: ${result.exceptionOrNull()?.localizedMessage}")
            }
        }
    }

    /**
     * Loads the showcase demo dataset on demand.
     *
     * Called explicitly from Settings → Backup & Restore → "Load Demo Data".
     * This is the only path that seeds demo data in production builds — it is
     * never called automatically on first launch (that path is debug-only).
     *
     * Uses the same [DayFlowRepository.seedInitialDataIfEmpty] guard, so calling
     * this after data already exists is a no-op.
     */
    fun restoreDemoShowcaseData() {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty(LocalDate.now())
            showSnackbarMessage("Showcase demo records populated successfully")
        }
    }

    private fun shareFile(context: Context, file: File, mimeType: String, chooserTitle: String) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, chooserTitle))
        } catch (e: Exception) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, file.readText())
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, chooserTitle))
        }
    }
}
