package com.echo.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories WHERE status = 'Active' ORDER BY createdAt DESC")
    fun observeActive(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE status = 'Archived' ORDER BY createdAt DESC")
    fun observeArchived(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE status = 'Active' AND nextReviewAt <= :now ORDER BY nextReviewAt")
    fun observeDue(now: Long): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): MemoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(memory: MemoryEntity)
}
