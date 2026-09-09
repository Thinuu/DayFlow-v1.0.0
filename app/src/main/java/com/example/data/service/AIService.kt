package com.example.data.service

import com.example.data.model.AccountWithBalance
import com.example.data.model.BillEntity
import com.example.data.model.ExpenseEntity
import com.example.data.model.RoutineWithStatus
import com.example.data.model.SavingsGoalWithProgress
import com.example.data.model.TaskEntity
import java.time.LocalDate
import kotlinx.coroutines.runBlocking

data class FinancialInsight(
    val title: String,
    val description: String,
    val category: String, // "BUDGET", "SAVINGS", "HABIT", "SUBSCRIPTION"
    val urgencyLevel: String = "INFO", // "INFO", "WARNING", "CELEBRATION"
    val actionText: String? = null
)

data class InsightInput(
    val monthlyExpense: Double,
    val monthlyIncome: Double,
    val monthlyBudget: Double,
    val accounts: List<AccountWithBalance>,
    val routines: List<RoutineWithStatus>,
    val savingsGoals: List<SavingsGoalWithProgress>,
    val bills: List<BillEntity>,
    val tasks: List<TaskEntity>,
    val currencySymbol: String
)

/**
 * AI Provider interface for generating financial insights.
 *
 * Implement this interface to swap the AI backend without touching
 * any ViewModel or UI code:
 *
 * - Default offline implementation: [OfflineAIProvider] (included, no API key needed)
 * - Cloud AI (e.g. Gemini, OpenAI): implement this interface, wire via [AIService.setProvider]
 *
 * Contract:
 * - Must never throw; return [Result.failure] on error instead.
 * - Input data is always pre-validated by the ViewModel before this is called.
 * - The returned list may be empty but must not be null.
 */
interface AIProvider {
    suspend fun generateInsights(input: InsightInput): Result<List<FinancialInsight>>
}

/**
 * Default offline AI provider that generates rule-based insights.
 * Works 100% offline without external network dependency.
 */
class OfflineAIProvider : AIProvider {
    override suspend fun generateInsights(input: InsightInput): Result<List<FinancialInsight>> {
        return Result.success(generateOfflineInsights(input))
    }

    private fun generateOfflineInsights(input: InsightInput): List<FinancialInsight> {
        val insights = mutableListOf<FinancialInsight>()
        val monthlyExpense = input.monthlyExpense
        val monthlyIncome = input.monthlyIncome
        val monthlyBudget = input.monthlyBudget
        val accounts = input.accounts
        val routines = input.routines
        val savingsGoals = input.savingsGoals
        val bills = input.bills
        val tasks = input.tasks
        val currencySymbol = input.currencySymbol

        // 1. Budget Pace & Warnings
        if (monthlyBudget > 0) {
            val budgetUsage = (monthlyExpense / monthlyBudget) * 100
            val dayOfMonth = LocalDate.now().dayOfMonth
            val daysInMonth = LocalDate.now().lengthOfMonth()
            val expectedPace = (dayOfMonth.toDouble() / daysInMonth) * 100

            if (budgetUsage > 90) {
                insights.add(
                    FinancialInsight(
                        title = "Critical Budget Alert",
                        description = "You have utilized ${budgetUsage.toInt()}% of your monthly limit of $currencySymbol${monthlyBudget.toInt()}.",
                        category = "BUDGET",
                        urgencyLevel = "WARNING",
                        actionText = "Review Expenses"
                    )
                )
            } else if (budgetUsage > expectedPace + 15) {
                insights.add(
                    FinancialInsight(
                        title = "Spending Ahead of Schedule",
                        description = "Your spending is ${budgetUsage.toInt()}% while month progress is ${expectedPace.toInt()}%. Consider pacing discretionary purchases.",
                        category = "BUDGET",
                        urgencyLevel = "WARNING"
                    )
                )
            } else if (budgetUsage < 40 && dayOfMonth > 15) {
                insights.add(
                    FinancialInsight(
                        title = "Excellent Budget Discipline",
                        description = "You're pacing well under budget this month with ${(100 - budgetUsage).toInt()}% runway remaining.",
                        category = "BUDGET",
                        urgencyLevel = "CELEBRATION"
                    )
                )
            }
        }

        // 2. Savings Rate Insight
        if (monthlyIncome > 0) {
            val netSavings = monthlyIncome - monthlyExpense
            val savingsRate = (netSavings / monthlyIncome) * 100
            if (savingsRate >= 20) {
                insights.add(
                    FinancialInsight(
                        title = "Strong ${savingsRate.toInt()}% Savings Rate",
                        description = "You have retained $currencySymbol${String.format("%.0f", netSavings)} (${savingsRate.toInt()}%) of total monthly cashflow.",
                        category = "SAVINGS",
                        urgencyLevel = "CELEBRATION",
                        actionText = "Allocate to Goals"
                    )
                )
            }
        }

        // 3. Goal Milestones
        val nearGoal = savingsGoals.firstOrNull { it.progressPercentage >= 80f && !it.goal.isCompleted }
        if (nearGoal != null) {
            insights.add(
                FinancialInsight(
                    title = "Goal Near Completion: ${nearGoal.goal.name}",
                    description = "You are only $currencySymbol${String.format("%.0f", nearGoal.remainingAmount)} away from your target ($currencySymbol${nearGoal.goal.targetAmount.toInt()})!",
                    category = "SAVINGS",
                    urgencyLevel = "CELEBRATION",
                    actionText = "Contribute"
                )
            )
        }

        // 4. Bills & Subscriptions Due
        val unpaidBills = bills.filter { !it.isPaidThisCycle }
        if (unpaidBills.isNotEmpty()) {
            val totalDue = unpaidBills.sumOf { it.amount }
            insights.add(
                FinancialInsight(
                    title = "${unpaidBills.size} Upcoming Bills ($currencySymbol${String.format("%.0f", totalDue)})",
                    description = "Next due: ${unpaidBills.first().title} on day ${unpaidBills.first().dueDayOfMonth}.",
                    category = "SUBSCRIPTION",
                    urgencyLevel = "INFO",
                    actionText = "Pay Bill"
                )
            )
        }

        // 5. Habit Consistency
        val bestStreak = routines.maxByOrNull { it.currentStreak }
        if (bestStreak != null && bestStreak.currentStreak >= 3) {
            insights.add(
                FinancialInsight(
                    title = "Habit Momentum: ${bestStreak.routine.title}",
                    description = "Active streak of ${bestStreak.currentStreak} consecutive days! Keep the momentum going.",
                    category = "HABIT",
                    urgencyLevel = "CELEBRATION"
                )
            )
        }

        val pendingTasks = tasks.filter { !it.isCompleted }
        if (pendingTasks.isNotEmpty()) {
            insights.add(
                FinancialInsight(
                    title = "${pendingTasks.size} Pending Action Items",
                    description = "Top priority: ${pendingTasks.first().title}",
                    category = "HABIT",
                    urgencyLevel = "INFO"
                )
            )
        }

        if (insights.isEmpty()) {
            insights.add(
                FinancialInsight(
                    title = "Financial Health Stable",
                    description = "Log your daily transactions and check off habits to generate personalized AI insights.",
                    category = "BUDGET",
                    urgencyLevel = "INFO"
                )
            )
        }

        return insights
    }
}

/**
 * Central AI service that manages the active AI provider.
 *
 * ============================================================
 * HOW TO SWAP IN A CUSTOM AI PROVIDER
 * ============================================================
 * 1. Create a class that implements [AIProvider]:
 *
 *      class MyCloudAIProvider : AIProvider {
 *          override suspend fun generateInsights(
 *              input: InsightInput
 *          ): Result<List<FinancialInsight>> {
 *              // call your API here
 *          }
 *      }
 *
 * 2. Register it before the ViewModel is created, e.g. in your
 *    Application.onCreate() or a DI module:
 *
 *      AIService.setProvider(MyCloudAIProvider())
 *
 * 3. The default [OfflineAIProvider] is replaced for the lifetime
 *    of the process. No other code changes are needed.
 *
 * The default provider is [OfflineAIProvider] which works 100%
 * offline with no API keys.
 * ============================================================
 */
object AIService {
    @Volatile
    private var provider: AIProvider = OfflineAIProvider()

    /**
     * Replace the active AI provider. Thread-safe (volatile field).
     * Call this from Application.onCreate() before the ViewModel starts.
     */
    fun setProvider(newProvider: AIProvider) {
        provider = newProvider
    }

    /** Returns the currently active [AIProvider] implementation. */
    fun getProvider(): AIProvider = provider

    /**
     * Synchronous wrapper around [AIProvider.generateInsights].
     *
     * This is intentionally blocking. It is called exclusively from
     * within a [kotlinx.coroutines.flow.combine] lambda, which already
     * runs on the coroutine dispatcher — no UI thread is blocked.
     *
     * If you replace the provider with a network-based implementation,
     * prefer [generateInsightsAsync] and restructure the ViewModel flow
     * to use coroutine-based collection instead.
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    fun generateOfflineInsights(
        monthlyExpense: Double,
        monthlyIncome: Double,
        monthlyBudget: Double,
        accounts: List<AccountWithBalance>,
        routines: List<RoutineWithStatus>,
        savingsGoals: List<SavingsGoalWithProgress>,
        bills: List<BillEntity>,
        tasks: List<TaskEntity>,
        currencySymbol: String
    ): List<FinancialInsight> {
        val input = InsightInput(
            monthlyExpense = monthlyExpense,
            monthlyIncome = monthlyIncome,
            monthlyBudget = monthlyBudget,
            accounts = accounts,
            routines = routines,
            savingsGoals = savingsGoals,
            bills = bills,
            tasks = tasks,
            currencySymbol = currencySymbol
        )
        // runBlocking is safe here: called from within a coroutine's combine lambda.
        // The underlying OfflineAIProvider is CPU-only and returns immediately.
        return runBlocking {
            provider.generateInsights(input).getOrDefault(emptyList())
        }
    }

    /**
     * Async version — preferred for any network-based [AIProvider] implementation.
     * Wire this into your ViewModel using a Flow or a launched coroutine.
     */
    suspend fun generateInsightsAsync(input: InsightInput): Result<List<FinancialInsight>> {
        return provider.generateInsights(input)
    }
}
