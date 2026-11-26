package com.eldershield.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.eldershield.data.entities.LearningProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: LearningProgress): Long

    @Query("SELECT * FROM learning_progress WHERE userProfileId = :userProfileId")
    fun getProgressListFlow(userProfileId: Long = 1): Flow<List<LearningProgress>>

    @Query("SELECT COUNT(*) FROM learning_progress WHERE isCompleted = 1")
    fun getCompletedLessonCountFlow(): Flow<Int>
}
