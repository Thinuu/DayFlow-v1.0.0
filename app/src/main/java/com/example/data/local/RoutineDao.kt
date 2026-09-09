package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.RoutineCompletionEntity
import com.example.data.model.RoutineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routines WHERE isArchived = 0 ORDER BY timeMinutes ASC")
    fun getAllRoutines(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines WHERE id = :id")
    suspend fun getRoutineById(id: Long): RoutineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity): Long

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)

    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)

    @Query("SELECT * FROM routine_completions WHERE dateEpochDay = :dateEpochDay")
    fun getCompletionsForDate(dateEpochDay: Long): Flow<List<RoutineCompletionEntity>>

    @Query("SELECT * FROM routine_completions WHERE routineId = :routineId ORDER BY dateEpochDay DESC")
    fun getCompletionsForRoutine(routineId: Long): Flow<List<RoutineCompletionEntity>>

    @Query("SELECT * FROM routine_completions WHERE dateEpochDay >= :fromEpochDay AND dateEpochDay <= :toEpochDay")
    fun getCompletionsInRange(fromEpochDay: Long, toEpochDay: Long): Flow<List<RoutineCompletionEntity>>

    @Query("SELECT * FROM routine_completions")
    fun getAllCompletions(): Flow<List<RoutineCompletionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: RoutineCompletionEntity)

    @Query("DELETE FROM routine_completions WHERE routineId = :routineId AND dateEpochDay = :dateEpochDay")
    suspend fun deleteCompletion(routineId: Long, dateEpochDay: Long)

    @Query("DELETE FROM routine_completions WHERE routineId = :routineId")
    suspend fun deleteAllCompletionsForRoutine(routineId: Long)
}
