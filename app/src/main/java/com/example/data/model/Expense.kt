package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TransactionType(val displayName: String) {
    EXPENSE("Expense"),
    INCOME("Income"),
    TRANSFER("Transfer")
}

enum class ExpenseCategory(val displayName: String, val iconName: String, val colorHex: Long) {
    FOOD("Food & Dining", "restaurant", 0xFFF97316),
    GROCERIES("Groceries", "shopping_cart", 0xFF10B981),
    TRANSPORT("Transport & Fuel", "directions_car", 0xFF3B82F6),
    BILLS("Bills & Utilities", "receipt_long", 0xFF8B5CF6),
    SHOPPING("Shopping", "local_mall", 0xFFEC4899),
    ENTERTAINMENT("Entertainment", "movie", 0xFFA855F7),
    HEALTH("Health & Medical", "local_hospital", 0xFFEF4444),
    EDUCATION("Education", "school", 0xFF06B6D4),
    SALARY("Salary & Wage", "payments", 0xFF10B981),
    INVESTMENT("Investments", "trending_up", 0xFF14B8A6),
    BUSINESS("Business / Freelance", "business_center", 0xFFF59E0B),
    OTHER("Other", "category", 0xFF6B7280)
}

enum class PaymentMethod(val displayName: String) {
    CASH("Cash"),
    CARD("Credit/Debit Card"),
    BANK_TRANSFER("Bank / UPI"),
    DIGITAL_WALLET("Digital Wallet"),
    OTHER("Other")
}

/**
 * Expense / income / transfer transaction.
 *
 * [accountId] references [AccountEntity.id]. No Room ForeignKey constraint is used here
 * because the app intentionally allows soft-deleted / archived accounts to retain their
 * historical transactions. The index on [accountId] ensures efficient per-account queries.
 *
 * [dateEpochDay] is stored as [LocalDate.toEpochDay()] for efficient date range queries.
 */
@Entity(
    tableName = "expenses",
    indices = [
        Index(value = ["accountId"]),
        Index(value = ["dateEpochDay"]),
        Index(value = ["category"])
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String = TransactionType.EXPENSE.name,
    val category: String = ExpenseCategory.FOOD.name,
    val paymentMethod: String = PaymentMethod.CARD.name,
    val accountId: Long = 1,
    val dateEpochDay: Long, // LocalDate.toEpochDay()
    val timestampMillis: Long = System.currentTimeMillis(),
    val note: String = "",
    val isRecurring: Boolean = false,
    val recurringFrequency: String? = null
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val monthKey: String, // e.g. "2026-08"
    val monthlyLimit: Double = 1500.0,
    val dailyTarget: Double = 50.0
)

/**
 * Per-category spending limit for a given month.
 * [monthKey] + [category] together uniquely identify a row but are not a composite PK
 * to allow simple auto-generated IDs. The compound index ensures fast month+category lookups.
 */
@Entity(
    tableName = "category_budgets",
    indices = [Index(value = ["monthKey", "category"])]
)
data class CategoryBudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monthKey: String,
    val category: String,
    val limitAmount: Double
)

data class DailySummary(
    val dateEpochDay: Long,
    val totalRoutinesCount: Int,
    val completedRoutinesCount: Int,
    val totalExpense: Double,
    val totalIncome: Double
)

data class CategorySpend(
    val category: ExpenseCategory,
    val totalAmount: Double,
    val percentage: Float
)
