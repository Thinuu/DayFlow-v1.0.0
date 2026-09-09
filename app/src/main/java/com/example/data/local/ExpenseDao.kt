package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY dateEpochDay DESC, timestampMillis DESC")
    fun getAllTransactions(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE dateEpochDay = :dateEpochDay ORDER BY timestampMillis DESC")
    fun getTransactionsForDate(dateEpochDay: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE dateEpochDay >= :fromEpochDay AND dateEpochDay <= :toEpochDay ORDER BY dateEpochDay DESC, timestampMillis DESC")
    fun getTransactionsInRange(fromEpochDay: Long, toEpochDay: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getTransactionById(id: Long): ExpenseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(expense: ExpenseEntity): Long

    @Update
    suspend fun updateTransaction(expense: ExpenseEntity)

    @Delete
    suspend fun deleteTransaction(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)
}
