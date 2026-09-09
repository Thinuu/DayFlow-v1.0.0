package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskPriority(val displayName: String, val colorHex: Long) {
    LOW("Low", 0xFF6B7280),
    MEDIUM("Medium", 0xFF3B82F6),
    HIGH("High", 0xFFF59E0B),
    URGENT("Urgent", 0xFFEF4444)
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val note: String = "",
    val priority: String = TaskPriority.MEDIUM.name,
    val dueDateEpochDay: Long, // LocalDate.toEpochDay()
    val dueTimeMinutes: Int? = null, // e.g. 14*60 = 2:00 PM
    val isCompleted: Boolean = false,
    val completedAtMillis: Long? = null,
    val reminderEnabled: Boolean = false,
    val category: String = "General",
    val createdAt: Long = System.currentTimeMillis()
)
