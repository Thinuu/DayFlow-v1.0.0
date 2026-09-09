package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class BillFrequency(val displayName: String) {
    ONE_TIME("One-time Bill"),
    WEEKLY("Weekly"),
    BI_WEEKLY("Bi-Weekly"),
    MONTHLY("Monthly Subscription"),
    QUARTERLY("Quarterly (3 Months)"),
    YEARLY("Yearly / Annual")
}

/**
 * Recurring bill or subscription.
 *
 * [accountId] references [AccountEntity.id]. Index added for efficient per-account queries.
 */
@Entity(
    tableName = "bills",
    indices = [Index(value = ["accountId"])]
)
data class BillEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String = ExpenseCategory.BILLS.name,
    val frequency: String = BillFrequency.MONTHLY.name,
    val dueDayOfMonth: Int = 1, // 1..31
    val dueDateEpochDay: Long = 0L,
    val isPaidThisCycle: Boolean = false,
    val reminderEnabled: Boolean = true,
    val paymentMethod: String = PaymentMethod.CARD.name,
    val accountId: Long = 1,
    val iconKey: String = "receipt_long",
    val colorHex: Long = 0xFF8B5CF6,
    val note: String = "",
    val autoLogExpense: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
