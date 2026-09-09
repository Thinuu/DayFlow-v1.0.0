package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val targetDateEpochDay: Long? = null,
    val iconKey: String = "savings",
    val colorHex: Long = 0xFF10B981,
    val note: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Individual deposit or withdrawal against a [SavingsGoalEntity].
 *
 * [goalId] references [SavingsGoalEntity.id]. No Room ForeignKey constraint is used so
 * that contributions are retained even if a goal is deleted (for audit/history purposes).
 * The index ensures efficient per-goal contribution queries.
 */
@Entity(
    tableName = "savings_contributions",
    indices = [
        Index(value = ["goalId"]),
        Index(value = ["dateEpochDay"])
    ]
)
data class SavingsContributionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalId: Long,
    val amount: Double,
    val dateEpochDay: Long,
    val note: String = "",
    val isWithdrawal: Boolean = false,
    val timestampMillis: Long = System.currentTimeMillis()
)

data class SavingsGoalWithProgress(
    val goal: SavingsGoalEntity,
    val progressPercentage: Float,
    val remainingAmount: Double,
    val daysRemaining: Long? = null,
    val contributions: List<SavingsContributionEntity> = emptyList()
)
