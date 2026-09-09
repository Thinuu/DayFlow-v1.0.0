package com.example

import com.example.data.model.AccountEntity
import com.example.data.model.ExpenseCategory
import com.example.data.model.ExpenseEntity
import com.example.data.model.Money
import com.example.data.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BudgetAndLedgerCalculationsTest {

    @Test
    fun testNetWorthCalculationAcrossMultipleAccounts() {
        val accounts = listOf(
            AccountEntity(id = 1, name = "Checking", type = "CHECKING", initialBalance = 2500.0),
            AccountEntity(id = 2, name = "High-Yield Savings", type = "SAVINGS", initialBalance = 15000.0),
            AccountEntity(id = 3, name = "Crypto / Investment", type = "INVESTMENT", initialBalance = 4200.0)
        )

        val expenses = listOf(
            ExpenseEntity(id = 1, title = "Groceries", amount = 150.0, type = TransactionType.EXPENSE.name, accountId = 1, dateEpochDay = 20000),
            ExpenseEntity(id = 2, title = "Freelance Consulting", amount = 1200.0, type = TransactionType.INCOME.name, accountId = 1, dateEpochDay = 20001),
            ExpenseEntity(id = 3, title = "Tech Stocks Dividend", amount = 50.0, type = TransactionType.INCOME.name, accountId = 3, dateEpochDay = 20002)
        )

        // Compute balances
        val account1Income = expenses.filter { it.accountId == 1L && it.type == TransactionType.INCOME.name }.sumOf { it.amount }
        val account1Expense = expenses.filter { it.accountId == 1L && it.type == TransactionType.EXPENSE.name }.sumOf { it.amount }
        val account1Balance = accounts[0].initialBalance + account1Income - account1Expense
        assertEquals(3550.0, account1Balance, 0.001)

        val account3Income = expenses.filter { it.accountId == 3L && it.type == TransactionType.INCOME.name }.sumOf { it.amount }
        val account3Balance = accounts[2].initialBalance + account3Income
        assertEquals(4250.0, account3Balance, 0.001)

        val totalNetWorth = account1Balance + accounts[1].initialBalance + account3Balance
        assertEquals(22800.0, totalNetWorth, 0.001)
    }

    @Test
    fun testBudgetRemainingAndOverBudget() {
        val monthlyBudgetLimit = 2000.00
        val expenses = listOf(
            ExpenseEntity(id = 1, title = "Rent", amount = 1200.0, type = TransactionType.EXPENSE.name, dateEpochDay = 20000),
            ExpenseEntity(id = 2, title = "Utilities", amount = 250.0, type = TransactionType.EXPENSE.name, dateEpochDay = 20000),
            ExpenseEntity(id = 3, title = "Dining Out", amount = 150.0, type = TransactionType.EXPENSE.name, dateEpochDay = 20000)
        )

        val totalSpent = expenses.sumOf { it.amount }
        assertEquals(1600.0, totalSpent, 0.001)

        val remaining = monthlyBudgetLimit - totalSpent
        assertEquals(400.0, remaining, 0.001)

        val percentUsed = (totalSpent / monthlyBudgetLimit) * 100
        assertEquals(80.0, percentUsed, 0.001)

        // Over-budget scenario
        val extraExpense = 600.00
        val newTotalSpent = totalSpent + extraExpense
        val overBudgetAmount = newTotalSpent - monthlyBudgetLimit
        assertEquals(200.0, overBudgetAmount, 0.001)
        assertTrue(newTotalSpent > monthlyBudgetLimit)
    }

    @Test
    fun testCategorySpendDistribution() {
        val expenses = listOf(
            ExpenseEntity(id = 1, title = "Supermarket", amount = 200.0, type = TransactionType.EXPENSE.name, category = ExpenseCategory.GROCERIES.name, dateEpochDay = 20000),
            ExpenseEntity(id = 2, title = "Coffee", amount = 50.0, type = TransactionType.EXPENSE.name, category = ExpenseCategory.FOOD.name, dateEpochDay = 20000),
            ExpenseEntity(id = 3, title = "Metro Pass", amount = 100.0, type = TransactionType.EXPENSE.name, category = ExpenseCategory.TRANSPORT.name, dateEpochDay = 20000)
        )

        val total = expenses.sumOf { it.amount }
        assertEquals(350.0, total, 0.001)

        val groceriesPercent = (200.0 / total) * 100.0
        val foodPercent = (50.0 / total) * 100.0
        val transportPercent = (100.0 / total) * 100.0

        assertEquals(57.14, groceriesPercent, 0.01)
        assertEquals(14.28, foodPercent, 0.01)
        assertEquals(28.57, transportPercent, 0.01)
    }
}
