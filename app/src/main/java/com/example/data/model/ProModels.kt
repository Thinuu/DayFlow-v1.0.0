package com.example.data.model

data class BlueprintRoutine(
    val title: String,
    val note: String,
    val category: RoutineCategory,
    val timeMinutes: Int,
    val targetDaysOfWeekMask: Int = 127, // All 7 days by default
    val timeOfDay: TimeOfDay,
    val iconKey: String,
    val colorHex: Long
)

data class BlueprintPack(
    val id: String,
    val title: String,
    val tag: String,
    val description: String,
    val iconKey: String,
    val colorHex: Long,
    val routines: List<BlueprintRoutine>,
    val targetBudget: Double? = null
)

data class SubscriptionItem(
    val id: String,
    val name: String,
    val amount: Double,
    val category: ExpenseCategory,
    val cycle: String = "Monthly",
    val renewDay: Int = 1,
    val iconKey: String = "credit_card"
)

data class AchievementBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconKey: String,
    val isUnlocked: Boolean,
    val progressPercent: Int
)

data class PricingPlan(
    val id: String,
    val name: String,
    val price: String,
    val subtext: String,
    val badge: String? = null,
    val isPopular: Boolean = false
)
