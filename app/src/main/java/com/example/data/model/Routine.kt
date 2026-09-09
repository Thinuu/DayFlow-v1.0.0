package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class TimeOfDay(val displayName: String, val startHour: Int, val endHour: Int) {
    MORNING("Morning", 5, 12),
    AFTERNOON("Afternoon", 12, 17),
    EVENING("Evening", 17, 21),
    NIGHT("Night", 21, 5)
}

enum class RoutineCategory(val displayName: String, val iconKey: String) {
    WELLNESS("Wellness & Health", "favorite"),
    FITNESS("Fitness & Exercise", "fitness_center"),
    PRODUCTIVITY("Productivity & Work", "work"),
    MINDFULNESS("Mindfulness & Rest", "self_improvement"),
    LEARNING("Reading & Learning", "menu_book"),
    PERSONAL("Personal Care", "spa"),
    CHORES("Chores & Household", "cleaning_services")
}

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val note: String = "",
    val category: String = RoutineCategory.PRODUCTIVITY.name,
    val timeMinutes: Int = 480, // e.g. 8:00 AM = 8 * 60
    val targetDaysOfWeekMask: Int = 127, // 7-bit mask (1 = Mon, 2 = Tue, 4 = Wed, etc.) 127 = every day
    val timeOfDay: String = TimeOfDay.MORNING.name,
    val iconKey: String = "check_circle",
    val colorHex: Long = 0xFF4F46E5,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Records a single completion of a routine on a specific date.
 *
 * Primary key is the composite (routineId, dateEpochDay) which enforces one
 * completion per routine per day at the DB level.
 *
 * The index on [routineId] allows efficient queries for all completions of a
 * specific routine (e.g. streak calculation) without a full table scan.
 * The index on [dateEpochDay] allows efficient queries for all completions on
 * a specific day (e.g. HomeScreen daily summary).
 */
@Entity(
    tableName = "routine_completions",
    primaryKeys = ["routineId", "dateEpochDay"],
    indices = [
        Index(value = ["routineId"]),
        Index(value = ["dateEpochDay"])
    ]
)
data class RoutineCompletionEntity(
    val routineId: Long,
    val dateEpochDay: Long, // LocalDate.toEpochDay()
    val completedAtMillis: Long = System.currentTimeMillis()
)

data class RoutineWithStatus(
    val routine: RoutineEntity,
    val isCompletedToday: Boolean,
    val currentStreak: Int = 0,
    val totalCompletions: Int = 0
)
