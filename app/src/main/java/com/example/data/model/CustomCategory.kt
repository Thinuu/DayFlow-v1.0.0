package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_categories")
data class CustomCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String = TransactionType.EXPENSE.name, // "EXPENSE", "INCOME", or "ROUTINE"
    val iconKey: String = "category",
    val colorHex: Long = 0xFF8B5CF6,
    val isSystemDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
