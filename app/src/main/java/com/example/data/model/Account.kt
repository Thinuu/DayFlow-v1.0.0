package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AccountType(val displayName: String, val defaultIcon: String) {
    CASH("Cash / Physical Wallet", "payments"),
    BANK("Bank Checking Account", "account_balance"),
    SAVINGS("High-Yield Savings", "savings"),
    DIGITAL_WALLET("Digital Wallet / UPI", "account_balance_wallet"),
    CREDIT_CARD("Credit Card", "credit_card"),
    INVESTMENT("Investment Account", "trending_up"),
    OTHER("Other Account", "wallet")
}

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String = AccountType.BANK.name,
    val initialBalance: Double = 0.0,
    val colorHex: Long = 0xFF6366F1,
    val iconKey: String = "account_balance",
    val isDefault: Boolean = false,
    val accountNumberMask: String = "",
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class AccountWithBalance(
    val account: AccountEntity,
    val currentBalance: Double,
    val totalIncome: Double,
    val totalExpense: Double,
    val transactionCount: Int
)
