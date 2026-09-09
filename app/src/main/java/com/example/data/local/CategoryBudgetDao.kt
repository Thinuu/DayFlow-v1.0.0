package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CategoryBudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryBudgetDao {
    @Query("SELECT * FROM category_budgets WHERE monthKey = :monthKey")
    fun getCategoryBudgetsForMonth(monthKey: String): Flow<List<CategoryBudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryBudget(categoryBudget: CategoryBudgetEntity): Long

    @Update
    suspend fun updateCategoryBudget(categoryBudget: CategoryBudgetEntity)

    @Delete
    suspend fun deleteCategoryBudget(categoryBudget: CategoryBudgetEntity)

    @Query("DELETE FROM category_budgets WHERE monthKey = :monthKey")
    suspend fun clearBudgetsForMonth(monthKey: String)
}
