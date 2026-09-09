package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

/**
 * DayFlow Room database.
 *
 * VERSION HISTORY
 * ───────────────
 * 1  Initial schema
 * 2  (internal — schema adjustments)
 * 3  (internal — schema adjustments)
 * 4  Added performance indices on frequently queried columns:
 *      expenses(accountId), expenses(dateEpochDay), expenses(category)
 *      savings_contributions(goalId), savings_contributions(dateEpochDay)
 *      bills(accountId)
 *      routine_completions(routineId), routine_completions(dateEpochDay)
 *      category_budgets(monthKey, category)
 *    Migration is purely additive — no data is modified or deleted.
 *
 * ADDING A NEW VERSION
 * ─────────────────────
 * 1. Add the schema change to the relevant @Entity class.
 * 2. Increment the version number below.
 * 3. Add a MIGRATION_N_(N+1) object (see MIGRATION_3_4 as a template).
 * 4. Add it to the Room.databaseBuilder(..).addMigrations(...) call below.
 * 5. Never use fallbackToDestructiveMigration() in production — it destroys user data.
 *
 * exportSchema = true generates JSON schema files under app/schemas/ on each build.
 * Commit these files to version control so you can diff schema changes across versions.
 * Configure the output directory in app/build.gradle.kts:
 *   ksp { arg("room.schemaLocation", "$projectDir/schemas") }
 */
@Database(
    entities = [
        RoutineEntity::class,
        RoutineCompletionEntity::class,
        ExpenseEntity::class,
        BudgetEntity::class,
        CategoryBudgetEntity::class,
        AccountEntity::class,
        SavingsGoalEntity::class,
        SavingsContributionEntity::class,
        BillEntity::class,
        TaskEntity::class,
        CustomCategoryEntity::class
    ],
    version = 4,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetDao(): BudgetDao
    abstract fun categoryBudgetDao(): CategoryBudgetDao
    abstract fun accountDao(): AccountDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun billDao(): BillDao
    abstract fun taskDao(): TaskDao
    abstract fun customCategoryDao(): CustomCategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Migration from version 3 to 4.
         *
         * Adds performance indices only — purely additive, no data is lost.
         * SQLite CREATE INDEX IF NOT EXISTS is safe to run on all schema states.
         */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // expenses table indices
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_accountId` ON `expenses` (`accountId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_dateEpochDay` ON `expenses` (`dateEpochDay`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_category` ON `expenses` (`category`)")
                // savings_contributions table indices
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_savings_contributions_goalId` ON `savings_contributions` (`goalId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_savings_contributions_dateEpochDay` ON `savings_contributions` (`dateEpochDay`)")
                // bills table index
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_bills_accountId` ON `bills` (`accountId`)")
                // routine_completions table indices
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_routine_completions_routineId` ON `routine_completions` (`routineId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_routine_completions_dateEpochDay` ON `routine_completions` (`dateEpochDay`)")
                // category_budgets compound index
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_category_budgets_monthKey_category` ON `category_budgets` (`monthKey`, `category`)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dayflow_database"
                )
                    .addMigrations(MIGRATION_3_4)
                    // fallbackToDestructiveMigration() has been intentionally removed.
                    // All schema changes MUST have an explicit Migration object so that
                    // existing user data is preserved across app updates.
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
