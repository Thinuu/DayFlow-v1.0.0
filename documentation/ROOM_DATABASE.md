# Room Database

Reference for the DayFlow SQLite schema, migrations, DAOs, and data access patterns.

---

## Overview

| Property | Value |
|----------|-------|
| Database name | `dayflow_database` |
| Room version | **4** |
| Backup schema version | 1 (`AppConfig.BACKUP_SCHEMA_VERSION`) |
| Export schema | `true` → JSON files written to `app/schemas/` on each build |
| Database file | `AppDatabase.kt` |
| Migration strategy | Explicit `Migration` objects — **no** `fallbackToDestructiveMigration` |

---

## Version history

| Version | Change |
|---------|--------|
| 1 | Initial schema |
| 2–3 | Internal schema adjustments |
| 4 | Added performance indices (purely additive — no data loss). See `MIGRATION_3_4` in `AppDatabase.kt`. |

---

## Entity reference

### `accounts` — `AccountEntity`

| Column | Type | Notes |
|--------|------|-------|
| `id` | `Long` PK autoGen | |
| `name` | `String` | |
| `type` | `String` | `AccountType` enum name |
| `initialBalance` | `Double` | Opening balance |
| `colorHex` | `Long` | ARGB packed long |
| `iconKey` | `String` | Material icon name key |
| `isDefault` | `Boolean` | One account marked default for quick entry |
| `accountNumberMask` | `String` | Display mask e.g. "•••• 4829" |
| `isArchived` | `Boolean` | Soft delete — archived accounts hidden from lists |
| `createdAt` | `Long` | `System.currentTimeMillis()` |

**AccountType** values: `CASH`, `BANK`, `SAVINGS`, `DIGITAL_WALLET`, `CREDIT_CARD`, `INVESTMENT`, `OTHER`

---

### `expenses` — `ExpenseEntity`

| Column | Type | Notes |
|--------|------|-------|
| `id` | `Long` PK autoGen | |
| `title` | `String` | |
| `amount` | `Double` | Positive value; type determines direction |
| `type` | `String` | `TransactionType` enum name: EXPENSE / INCOME / TRANSFER |
| `category` | `String` | `ExpenseCategory` enum name |
| `paymentMethod` | `String` | `PaymentMethod` enum name |
| `accountId` | `Long` | References `accounts.id` (soft ref — no FK constraint) |
| `dateEpochDay` | `Long` | `LocalDate.toEpochDay()` |
| `timestampMillis` | `Long` | Wall-clock creation time |
| `note` | `String` | User note |
| `isRecurring` | `Boolean` | |
| `recurringFrequency` | `String?` | Nullable |

**Indices**: `accountId`, `dateEpochDay`, `category`

**ExpenseCategory** values: `FOOD`, `GROCERIES`, `TRANSPORT`, `BILLS`, `SHOPPING`, `ENTERTAINMENT`, `HEALTH`, `EDUCATION`, `SALARY`, `INVESTMENT`, `BUSINESS`, `OTHER`

**Note on foreign keys**: No Room `ForeignKey` constraint is declared on `accountId`. This is intentional — the app retains historical transactions even when an account is archived or deleted. The index provides query performance without cascade behaviour.

---

### `budgets` — `BudgetEntity`

| Column | Type | Notes |
|--------|------|-------|
| `monthKey` | `String` PK | Format `"2026-08"` |
| `monthlyLimit` | `Double` | |
| `dailyTarget` | `Double` | |

One row per month. Created by `DayFlowRepository.setBudget()`. Default values from `AppConfig.DEFAULT_MONTHLY_BUDGET` and `DEFAULT_DAILY_BUDGET`.

---

### `category_budgets` — `CategoryBudgetEntity`

| Column | Type | Notes |
|--------|------|-------|
| `id` | `Long` PK autoGen | |
| `monthKey` | `String` | Links to `budgets.monthKey` logically |
| `category` | `String` | `ExpenseCategory` enum name |
| `limitAmount` | `Double` | |

**Index**: compound `(monthKey, category)`

---

### `savings_goals` — `SavingsGoalEntity`

| Column | Type | Notes |
|--------|------|-------|
| `id` | `Long` PK autoGen | |
| `name` | `String` | |
| `targetAmount` | `Double` | |
| `currentAmount` | `Double` | Snapshot; actual balance derived from contributions |
| `targetDateEpochDay` | `Long?` | Optional deadline |
| `iconKey` | `String` | |
| `colorHex` | `Long` | |
| `note` | `String` | |
| `isCompleted` | `Boolean` | |
| `createdAt` | `Long` | |

---

### `savings_contributions` — `SavingsContributionEntity`

| Column | Type | Notes |
|--------|------|-------|
| `id` | `Long` PK autoGen | |
| `goalId` | `Long` | References `savings_goals.id` (soft ref) |
| `amount` | `Double` | Always positive; `isWithdrawal` determines direction |
| `dateEpochDay` | `Long` | |
| `note` | `String` | |
| `isWithdrawal` | `Boolean` | True = money removed from goal |
| `timestampMillis` | `Long` | |

**Indices**: `goalId`, `dateEpochDay`

**Balance calculation**: `DayFlowRepository.savingsGoalsWithProgress` sums contributions per goal in a `combine` flow. Withdrawals are subtracted.

---

### `bills` — `BillEntity`

| Column | Type | Notes |
|--------|------|-------|
| `id` | `Long` PK autoGen | |
| `title` | `String` | |
| `amount` | `Double` | |
| `category` | `String` | `ExpenseCategory` enum name |
| `frequency` | `String` | `BillFrequency` enum name |
| `dueDayOfMonth` | `Int` | 1–31 |
| `dueDateEpochDay` | `Long` | Absolute next due date (0 = use dueDayOfMonth only) |
| `isPaidThisCycle` | `Boolean` | Reset monthly by `BillDao.resetAllBillsPaidStatus()` |
| `reminderEnabled` | `Boolean` | |
| `paymentMethod` | `String` | |
| `accountId` | `Long` | References `accounts.id` (soft ref) |
| `iconKey` | `String` | |
| `colorHex` | `Long` | |
| `note` | `String` | |
| `autoLogExpense` | `Boolean` | Auto-create expense when marked paid |
| `createdAt` | `Long` | |

**Index**: `accountId`

**BillFrequency** values: `ONE_TIME`, `WEEKLY`, `BI_WEEKLY`, `MONTHLY`, `QUARTERLY`, `YEARLY`

---

### `routines` — `RoutineEntity`

| Column | Type | Notes |
|--------|------|-------|
| `id` | `Long` PK autoGen | |
| `title` | `String` | |
| `note` | `String` | |
| `category` | `String` | `RoutineCategory` enum name |
| `timeMinutes` | `Int` | Minutes since midnight (e.g. 8:30 AM = 510) |
| `targetDaysOfWeekMask` | `Int` | 7-bit mask: bit 0 = Monday … bit 6 = Sunday; 127 = every day |
| `timeOfDay` | `String` | `TimeOfDay` enum name |
| `iconKey` | `String` | |
| `colorHex` | `Long` | |
| `isArchived` | `Boolean` | Soft delete |
| `createdAt` | `Long` | |

**RoutineCategory** values: `WELLNESS`, `FITNESS`, `PRODUCTIVITY`, `MINDFULNESS`, `LEARNING`, `PERSONAL`, `CHORES`

**TimeOfDay** values: `MORNING` (5–12h), `AFTERNOON` (12–17h), `EVENING` (17–21h), `NIGHT` (21–5h)

---

### `routine_completions` — `RoutineCompletionEntity`

| Column | Type | Notes |
|--------|------|-------|
| `routineId` | `Long` PK (composite) | References `routines.id` |
| `dateEpochDay` | `Long` PK (composite) | One completion per routine per day, enforced at DB level |
| `completedAtMillis` | `Long` | Wall-clock time of completion |

**Indices**: `routineId`, `dateEpochDay`

**Streak calculation**: performed in-memory inside `DayFlowRepository.getRoutinesWithStatusForDate()`. Walks backwards from today while `dateEpochDay` is present in the completion set.

---

### `tasks` — `TaskEntity`

| Column | Type | Notes |
|--------|------|-------|
| `id` | `Long` PK autoGen | |
| `title` | `String` | |
| `note` | `String` | |
| `priority` | `String` | `TaskPriority` enum name |
| `dueDateEpochDay` | `Long` | |
| `dueTimeMinutes` | `Int?` | Optional time component |
| `isCompleted` | `Boolean` | |
| `completedAtMillis` | `Long?` | Set when completed |
| `reminderEnabled` | `Boolean` | |
| `category` | `String` | Free-text category label |
| `createdAt` | `Long` | |

**TaskPriority** values: `LOW`, `MEDIUM`, `HIGH`, `URGENT`

---

### `custom_categories` — `CustomCategoryEntity`

| Column | Type | Notes |
|--------|------|-------|
| `id` | `Long` PK autoGen | |
| `name` | `String` | Display name |
| `type` | `String` | `"INCOME"`, `"EXPENSE"`, or `"ROUTINE"` |
| `iconKey` | `String` | |
| `colorHex` | `Long` | |
| `isSystemDefault` | `Boolean` | `true` for categories seeded from `AppConfig.DEFAULT_CUSTOM_CATEGORIES` |
| `createdAt` | `Long` | |

Default seed categories are defined in `AppConfig.DEFAULT_CUSTOM_CATEGORIES` and inserted once by `DayFlowRepository.seedInitialDataIfEmpty()`. User-created categories use `isSystemDefault = false`.

---

## DAO reference

| DAO | Interface file | Key queries |
|-----|---------------|-------------|
| `RoutineDao` | `RoutineDao.kt` | `getAllRoutines()`, `getAllCompletions()`, `insertCompletion()`, `deleteCompletion()` |
| `ExpenseDao` | `ExpenseDao.kt` | `getAllTransactions()`, `getTransactionsForDate()`, `getTransactionsInRange()` |
| `BudgetDao` | `BudgetDao.kt` | `getBudgetForMonth(monthKey)`, `setBudget()` |
| `CategoryBudgetDao` | `CategoryBudgetDao.kt` | `getCategoryBudgetsForMonth(monthKey)` |
| `AccountDao` | `AccountDao.kt` | `getAllAccounts()`, `clearDefaultAccount()`, `setDefaultAccount()` |
| `SavingsGoalDao` | `SavingsGoalDao.kt` | `getAllGoals()`, `getAllContributions()`, `insertContribution()` |
| `BillDao` | `BillDao.kt` | `getAllBills()`, `setBillPaidStatus()`, `resetAllBillsPaidStatus()` |
| `TaskDao` | `TaskDao.kt` | `getTasksForDate(epochDay)`, `setTaskCompleted()` |
| `CustomCategoryDao` | `CustomCategoryDao.kt` | `getAllCustomCategories()`, `getCategoriesByType()` |

All DAOs return `Flow<List<T>>` for reactive queries and `suspend` functions for writes.

---

## Adding a new schema version

1. Make the required change to the `@Entity` class (new column, new index, etc.).
2. Increment `version` in the `@Database` annotation in `AppDatabase.kt`.
3. Add a `MIGRATION_N_(N+1)` object following the `MIGRATION_3_4` template.
4. Add it to `.addMigrations(...)` in `getDatabase()`.
5. Rebuild — the new schema JSON is written to `app/schemas/`.
6. Commit the schema JSON file.

**Never use `fallbackToDestructiveMigration()` for a production app** — it wipes all user data when the schema version changes without a matching migration.

---

## Backup and restore

`BackupManager` serialises the full database to a schema-versioned JSON file (`dayflow_backup_<date>.json`). It also generates a CSV of transactions for spreadsheet use.

- `BackupManager.createJsonBackup()` — async, returns JSON string
- `BackupManager.validateBackupJson()` — parses and counts records without writing
- `BackupManager.restoreFromJson()` — inserts records using `OnConflictStrategy.REPLACE`

The backup format uses `schemaVersion = AppConfig.BACKUP_SCHEMA_VERSION` (currently 1). If you change the backup format, bump that constant and update the restore logic.

---

## Money and precision

`Money.kt` provides an integer-cent value object (`Money(cents: Long)`) with safe arithmetic operators, correct rounding, and a `format(currencySymbol)` method. Use it for any calculation where `Double` rounding would be unacceptable (e.g. totals, percentage splits). The Room entities store amounts as `Double` for simplicity; convert with `Money.fromDouble(amount)` when precision matters.
