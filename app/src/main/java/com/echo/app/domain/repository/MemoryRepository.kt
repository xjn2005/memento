package com.echo.app.domain.repository

import com.echo.app.domain.model.Memory
import com.echo.app.domain.model.RecallFeedback
import kotlinx.coroutines.flow.Flow

interface MemoryRepository {
    fun observeActive(): Flow<List<Memory>>
    fun observeArchived(): Flow<List<Memory>>
    fun observeDue(now: Long): Flow<List<Memory>>
    suspend fun findById(id: String): Memory?
    suspend fun create(content: String, tag: String?, now: Long): Memory
    suspend fun applyFeedback(id: String, feedback: RecallFeedback, now: Long): Memory?
    suspend fun reactivate(id: String, now: Long): Memory?
}
