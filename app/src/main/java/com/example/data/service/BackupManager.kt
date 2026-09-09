package com.example.data.service

import com.example.AppConfig
import com.example.data.local.AppDatabase
import com.example.data.model.AccountEntity
import com.example.data.model.BillEntity
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryBudgetEntity
import com.example.data.model.CustomCategoryEntity
import com.example.data.model.ExpenseEntity
import com.example.data.model.RoutineCompletionEntity
import com.example.data.model.RoutineEntity
import com.example.data.model.SavingsContributionEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupValidationResult(
    val isValid: Boolean,
    val schemaVersion: Int = 0,
    val accountsCount: Int = 0,
    val transactionsCount: Int = 0,
    val routinesCount: Int = 0,
    val tasksCount: Int = 0,
    val savingsCount: Int = 0,
    val billsCount: Int = 0,
    val errorMessage: String? = null
)

class BackupManager(private val database: AppDatabase) {

    /**
     * Generates a complete JSON backup string of all persistent application data.
     */
    suspend fun createJsonBackup(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("app", AppConfig.APP_NAME)
        root.put("versionName", AppConfig.APP_VERSION_NAME)
        root.put("schemaVersion", AppConfig.BACKUP_SCHEMA_VERSION)
        root.put("exportedAt", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date()))
        root.put("timestamp", System.currentTimeMillis())

        // Accounts
        val accounts = database.accountDao().getAllAccounts().first()
        val accountsArray = JSONArray()
        accounts.forEach { acc ->
            val obj = JSONObject().apply {
                put("id", acc.id)
                put("name", acc.name)
                put("type", acc.type)
                put("initialBalance", acc.initialBalance)
                put("colorHex", acc.colorHex)
                put("iconKey", acc.iconKey)
                put("isDefault", acc.isDefault)
                put("accountNumberMask", acc.accountNumberMask)
                put("isArchived", acc.isArchived)
            }
            accountsArray.put(obj)
        }
        root.put("accounts", accountsArray)

        // Transactions / Expenses
        val expenses = database.expenseDao().getAllTransactions().first()
        val expensesArray = JSONArray()
        expenses.forEach { exp ->
            val obj = JSONObject().apply {
                put("id", exp.id)
                put("title", exp.title)
                put("amount", exp.amount)
                put("type", exp.type)
                put("category", exp.category)
                put("paymentMethod", exp.paymentMethod)
                put("accountId", exp.accountId)
                put("dateEpochDay", exp.dateEpochDay)
                put("timestampMillis", exp.timestampMillis)
                put("note", exp.note)
                put("isRecurring", exp.isRecurring)
                put("recurringFrequency", exp.recurringFrequency ?: "")
            }
            expensesArray.put(obj)
        }
        root.put("transactions", expensesArray)

        // Routines
        val routines = database.routineDao().getAllRoutines().first()
        val routinesArray = JSONArray()
        routines.forEach { r ->
            val obj = JSONObject().apply {
                put("id", r.id)
                put("title", r.title)
                put("note", r.note)
                put("category", r.category)
                put("timeMinutes", r.timeMinutes)
                put("targetDaysOfWeekMask", r.targetDaysOfWeekMask)
                put("timeOfDay", r.timeOfDay)
                put("iconKey", r.iconKey)
                put("colorHex", r.colorHex)
                put("isArchived", r.isArchived)
                put("createdAt", r.createdAt)
            }
            routinesArray.put(obj)
        }
        root.put("routines", routinesArray)

        // Routine Completions
        val completions = database.routineDao().getAllCompletions().first()
        val completionsArray = JSONArray()
        completions.forEach { c ->
            val obj = JSONObject().apply {
                put("routineId", c.routineId)
                put("dateEpochDay", c.dateEpochDay)
                put("completedAtMillis", c.completedAtMillis)
            }
            completionsArray.put(obj)
        }
        root.put("routineCompletions", completionsArray)

        // Savings Goals
        val goals = database.savingsGoalDao().getAllGoals().first()
        val goalsArray = JSONArray()
        goals.forEach { g ->
            val obj = JSONObject().apply {
                put("id", g.id)
                put("name", g.name)
                put("targetAmount", g.targetAmount)
                put("currentAmount", g.currentAmount)
                put("targetDateEpochDay", g.targetDateEpochDay ?: 0L)
                put("iconKey", g.iconKey)
                put("colorHex", g.colorHex)
                put("note", g.note)
                put("isCompleted", g.isCompleted)
                put("createdAt", g.createdAt)
            }
            goalsArray.put(obj)
        }
        root.put("savingsGoals", goalsArray)

        // Savings Contributions
        val contributions = database.savingsGoalDao().getAllContributions().first()
        val contribArray = JSONArray()
        contributions.forEach { c ->
            val obj = JSONObject().apply {
                put("id", c.id)
                put("goalId", c.goalId)
                put("amount", c.amount)
                put("dateEpochDay", c.dateEpochDay)
                put("note", c.note)
                put("isWithdrawal", c.isWithdrawal)
                put("timestampMillis", c.timestampMillis)
            }
            contribArray.put(obj)
        }
        root.put("savingsContributions", contribArray)

        // Budgets (monthly limits)
        val budgets = database.budgetDao().getAllBudgets().first()
        val budgetsArray = JSONArray()
        budgets.forEach { b ->
            val obj = JSONObject().apply {
                put("monthKey", b.monthKey)
                put("monthlyLimit", b.monthlyLimit)
                put("dailyTarget", b.dailyTarget)
            }
            budgetsArray.put(obj)
        }
        root.put("budgets", budgetsArray)

        // Bills
        val bills = database.billDao().getAllBills().first()
        val billsArray = JSONArray()
        bills.forEach { b ->
            val obj = JSONObject().apply {
                put("id", b.id)
                put("title", b.title)
                put("amount", b.amount)
                put("category", b.category)
                put("frequency", b.frequency)
                put("dueDayOfMonth", b.dueDayOfMonth)
                put("isPaidThisCycle", b.isPaidThisCycle)
                put("reminderEnabled", b.reminderEnabled)
                put("paymentMethod", b.paymentMethod)
                put("accountId", b.accountId)
                put("iconKey", b.iconKey)
                put("colorHex", b.colorHex)
            }
            billsArray.put(obj)
        }
        root.put("bills", billsArray)

        // Tasks
        val tasks = database.taskDao().getAllTasks().first()
        val tasksArray = JSONArray()
        tasks.forEach { t ->
            val obj = JSONObject().apply {
                put("id", t.id)
                put("title", t.title)
                put("note", t.note)
                put("priority", t.priority)
                put("dueDateEpochDay", t.dueDateEpochDay)
                put("dueTimeMinutes", t.dueTimeMinutes ?: -1)
                put("isCompleted", t.isCompleted)
                put("completedAtMillis", t.completedAtMillis ?: 0L)
                put("category", t.category)
                put("createdAt", t.createdAt)
            }
            tasksArray.put(obj)
        }
        root.put("tasks", tasksArray)

        // Custom Categories
        val customCats = database.customCategoryDao().getAllCustomCategories().first()
        val customCatsArray = JSONArray()
        customCats.forEach { cc ->
            val obj = JSONObject().apply {
                put("id", cc.id)
                put("name", cc.name)
                put("type", cc.type)
                put("iconKey", cc.iconKey)
                put("colorHex", cc.colorHex)
                put("isSystemDefault", cc.isSystemDefault)
                put("createdAt", cc.createdAt)
            }
            customCatsArray.put(obj)
        }
        root.put("customCategories", customCatsArray)

        root.toString(2)
    }

    /**
     * Validates a JSON backup without modifying the database.
     * Also enforces that the backup's [schemaVersion] matches the current
     * [AppConfig.BACKUP_SCHEMA_VERSION] so stale backups are rejected early.
     */
    fun validateBackupJson(jsonString: String): BackupValidationResult {
        return try {
            val root = JSONObject(jsonString)
            val schemaVersion = root.optInt("schemaVersion", 1)

            // Reject backups written by a different schema version to prevent
            // silent data corruption on restore.
            if (schemaVersion != AppConfig.BACKUP_SCHEMA_VERSION) {
                return BackupValidationResult(
                    isValid = false,
                    schemaVersion = schemaVersion,
                    errorMessage = "Backup schema version $schemaVersion does not match " +
                        "current version ${AppConfig.BACKUP_SCHEMA_VERSION}. " +
                        "Please export a new backup from this version of DayFlow."
                )
            }

            BackupValidationResult(
                isValid = true,
                schemaVersion = schemaVersion,
                accountsCount      = root.optJSONArray("accounts")?.length() ?: 0,
                transactionsCount  = root.optJSONArray("transactions")?.length() ?: 0,
                routinesCount      = root.optJSONArray("routines")?.length() ?: 0,
                tasksCount         = root.optJSONArray("tasks")?.length() ?: 0,
                savingsCount       = root.optJSONArray("savingsGoals")?.length() ?: 0,
                billsCount         = root.optJSONArray("bills")?.length() ?: 0
            )
        } catch (e: Exception) {
            BackupValidationResult(
                isValid = false,
                errorMessage = e.localizedMessage ?: "Invalid JSON backup format"
            )
        }
    }

    /**
     * Restores database content from a validated JSON backup.
     *
     * Strategy:
     * 1. Validate the JSON and schema version.
     * 2. Clear all existing tables with [RoomDatabase.clearAllTables] so the
     *    restored data is the single source of truth (no merge / overwrite confusion).
     * 3. Re-insert every entity in dependency order (accounts before transactions,
     *    goals before contributions, routines before completions).
     *
     * All operations run on [Dispatchers.IO].
     */
    suspend fun restoreFromJson(jsonString: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val validation = validateBackupJson(jsonString)
            if (!validation.isValid) {
                return@withContext Result.failure(
                    IllegalArgumentException(validation.errorMessage ?: "Corrupt backup file")
                )
            }

            val root = JSONObject(jsonString)

            // ── Clear all existing data before restore ──────────────────────
            // clearAllTables() is provided by Room and truncates every table in
            // the database in a single transaction, avoiding partial-restore states.
            database.clearAllTables()

            // ── Accounts ────────────────────────────────────────────────────
            root.optJSONArray("accounts")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    database.accountDao().insertAccount(
                        AccountEntity(
                            id                = o.optLong("id", 0),
                            name              = o.getString("name"),
                            type              = o.optString("type", "BANK"),
                            initialBalance    = o.optDouble("initialBalance", 0.0),
                            colorHex          = o.optLong("colorHex", 0xFF6366F1),
                            iconKey           = o.optString("iconKey", "account_balance"),
                            isDefault         = o.optBoolean("isDefault", false),
                            accountNumberMask = o.optString("accountNumberMask", ""),
                            isArchived        = o.optBoolean("isArchived", false)
                        )
                    )
                }
            }

            // ── Transactions ────────────────────────────────────────────────
            root.optJSONArray("transactions")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    database.expenseDao().insertTransaction(
                        ExpenseEntity(
                            id                 = o.optLong("id", 0),
                            title              = o.getString("title"),
                            amount             = o.getDouble("amount"),
                            type               = o.optString("type", "EXPENSE"),
                            category           = o.optString("category", "OTHER"),
                            paymentMethod      = o.optString("paymentMethod", "CARD"),
                            accountId          = o.optLong("accountId", 1),
                            dateEpochDay       = o.getLong("dateEpochDay"),
                            timestampMillis    = o.optLong("timestampMillis", System.currentTimeMillis()),
                            note               = o.optString("note", ""),
                            isRecurring        = o.optBoolean("isRecurring", false),
                            recurringFrequency = o.optString("recurringFrequency", "")
                        )
                    )
                }
            }

            // ── Budgets ─────────────────────────────────────────────────────
            root.optJSONArray("budgets")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    database.budgetDao().setBudget(
                        BudgetEntity(
                            monthKey     = o.getString("monthKey"),
                            monthlyLimit = o.optDouble("monthlyLimit", 2500.0),
                            dailyTarget  = o.optDouble("dailyTarget", 80.0)
                        )
                    )
                }
            }

            // ── Routines ────────────────────────────────────────────────────
            root.optJSONArray("routines")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    database.routineDao().insertRoutine(
                        RoutineEntity(
                            id                   = o.optLong("id", 0),
                            title                = o.getString("title"),
                            note                 = o.optString("note", ""),
                            category             = o.optString("category", "PRODUCTIVITY"),
                            timeMinutes          = o.optInt("timeMinutes", 480),
                            targetDaysOfWeekMask = o.optInt("targetDaysOfWeekMask", 127),
                            timeOfDay            = o.optString("timeOfDay", "MORNING"),
                            iconKey              = o.optString("iconKey", "check_circle"),
                            colorHex             = o.optLong("colorHex", 0xFF4F46E5),
                            isArchived           = o.optBoolean("isArchived", false),
                            createdAt            = o.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
            }

            // ── Routine completions ──────────────────────────────────────────
            root.optJSONArray("routineCompletions")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    database.routineDao().insertCompletion(
                        RoutineCompletionEntity(
                            routineId        = o.getLong("routineId"),
                            dateEpochDay     = o.getLong("dateEpochDay"),
                            completedAtMillis = o.optLong("completedAtMillis", System.currentTimeMillis())
                        )
                    )
                }
            }

            // ── Savings goals ────────────────────────────────────────────────
            root.optJSONArray("savingsGoals")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val targetDate = o.optLong("targetDateEpochDay", 0L)
                    database.savingsGoalDao().insertGoal(
                        SavingsGoalEntity(
                            id                = o.optLong("id", 0),
                            name              = o.getString("name"),
                            targetAmount      = o.getDouble("targetAmount"),
                            currentAmount     = o.optDouble("currentAmount", 0.0),
                            targetDateEpochDay = if (targetDate > 0) targetDate else null,
                            iconKey           = o.optString("iconKey", "savings"),
                            colorHex          = o.optLong("colorHex", 0xFF6366F1),
                            note              = o.optString("note", ""),
                            isCompleted       = o.optBoolean("isCompleted", false),
                            createdAt         = o.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
            }

            // ── Savings contributions (previously missing from restore) ───────
            root.optJSONArray("savingsContributions")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    database.savingsGoalDao().insertContribution(
                        SavingsContributionEntity(
                            id              = o.optLong("id", 0),
                            goalId          = o.getLong("goalId"),
                            amount          = o.getDouble("amount"),
                            dateEpochDay    = o.getLong("dateEpochDay"),
                            note            = o.optString("note", ""),
                            isWithdrawal    = o.optBoolean("isWithdrawal", false),
                            timestampMillis = o.optLong("timestampMillis", System.currentTimeMillis())
                        )
                    )
                }
            }

            // ── Tasks ────────────────────────────────────────────────────────
            root.optJSONArray("tasks")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val dueTime    = o.optInt("dueTimeMinutes", -1)
                    val completedAt = o.optLong("completedAtMillis", 0L)
                    database.taskDao().insertTask(
                        TaskEntity(
                            id               = o.optLong("id", 0),
                            title            = o.getString("title"),
                            note             = o.optString("note", ""),
                            priority         = o.optString("priority", "MEDIUM"),
                            dueDateEpochDay  = o.getLong("dueDateEpochDay"),
                            dueTimeMinutes   = if (dueTime >= 0) dueTime else null,
                            isCompleted      = o.optBoolean("isCompleted", false),
                            completedAtMillis = if (completedAt > 0) completedAt else null,
                            category         = o.optString("category", "General"),
                            createdAt        = o.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
            }

            // ── Bills ────────────────────────────────────────────────────────
            root.optJSONArray("bills")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    database.billDao().insertBill(
                        BillEntity(
                            id              = o.optLong("id", 0),
                            title           = o.getString("title"),
                            amount          = o.getDouble("amount"),
                            category        = o.optString("category", "BILLS"),
                            frequency       = o.optString("frequency", "MONTHLY"),
                            dueDayOfMonth   = o.optInt("dueDayOfMonth", 1),
                            isPaidThisCycle = o.optBoolean("isPaidThisCycle", false),
                            reminderEnabled = o.optBoolean("reminderEnabled", true),
                            paymentMethod   = o.optString("paymentMethod", "CARD"),
                            accountId       = o.optLong("accountId", 1),
                            iconKey         = o.optString("iconKey", "receipt_long"),
                            colorHex        = o.optLong("colorHex", 0xFF8B5CF6)
                        )
                    )
                }
            }

            // ── Custom categories (previously missing from restore) ───────────
            root.optJSONArray("customCategories")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    database.customCategoryDao().insertCategory(
                        CustomCategoryEntity(
                            id              = o.optLong("id", 0),
                            name            = o.getString("name"),
                            type            = o.optString("type", "EXPENSE"),
                            iconKey         = o.optString("iconKey", "category"),
                            colorHex        = o.optLong("colorHex", 0xFF8B5CF6),
                            isSystemDefault = o.optBoolean("isSystemDefault", false),
                            createdAt       = o.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generates a CSV of all transactions for spreadsheet import.
     *
     * Date is derived with [java.time.LocalDate.ofEpochDay] which is timezone-agnostic,
     * matching exactly how dates are stored and displayed inside the app.
     *
     * Text cells are double-quoted and sanitised:
     * - Internal double-quotes are escaped by doubling (RFC 4180).
     * - Leading formula-injection characters (=, +, -, @) are stripped to prevent
     *   spreadsheet applications from interpreting cell values as formulas.
     */
    suspend fun generateTransactionsCsv(): String = withContext(Dispatchers.IO) {
        val expenses = database.expenseDao().getAllTransactions().first()
        val accounts = database.accountDao().getAllAccounts().first().associateBy { it.id }

        val sb = StringBuilder()
        sb.append("ID,Date,Title,Type,Category,Amount,PaymentMethod,Account,Notes\n")

        expenses.forEach { exp ->
            // LocalDate.ofEpochDay is timezone-agnostic — avoids the off-by-one-day
            // bug that occurs when using Date(epochDay * 86400000) + SimpleDateFormat
            // with a non-UTC default timezone.
            val dateStr = java.time.LocalDate.ofEpochDay(exp.dateEpochDay).toString()
            val accountName = csvEscapeBackup(accounts[exp.accountId]?.name ?: "Default")
            val title  = csvEscapeBackup(exp.title)
            val note   = csvEscapeBackup(exp.note)
            sb.append(
                "${exp.id},$dateStr,\"$title\",${exp.type},${exp.category}," +
                "${String.format(java.util.Locale.US, "%.2f", exp.amount)}," +
                "${exp.paymentMethod},\"$accountName\",\"$note\"\n"
            )
        }
        sb.toString()
    }

    /**
     * CSV text-cell sanitiser for [generateTransactionsCsv].
     * Strips leading formula-injection chars; escapes internal double-quotes.
     */
    private fun csvEscapeBackup(value: String): String =
        value.trimStart('=', '+', '-', '@', '\t', '\r')
             .replace("\"", "\"\"")
             .replace("\n", " ")
}
