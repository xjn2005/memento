package com.echo.app.data.repository

import com.echo.app.data.local.MemoryDao
import com.echo.app.data.local.toDomain
import com.echo.app.data.local.toEntity
import com.echo.app.data.notification.RecallScheduler
import com.echo.app.domain.algorithm.ForgettingCurve
import com.echo.app.domain.model.Memory
import com.echo.app.domain.model.MemoryStatus
import com.echo.app.domain.model.RecallFeedback
import com.echo.app.domain.repository.MemoryRepository
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomMemoryRepository(
    private val dao: MemoryDao,
    private val curve: ForgettingCurve,
    private val scheduler: RecallScheduler,
) : MemoryRepository {
    override fun observeActive(): Flow<List<Memory>> = dao.observeActive().map { entries -> entries.map { it.toDomain() } }
    override fun observeArchived(): Flow<List<Memory>> = dao.observeArchived().map { entries -> entries.map { it.toDomain() } }
    override fun observeDue(now: Long): Flow<List<Memory>> = dao.observeDue(now).map { entries -> entries.map { it.toDomain() } }
    override suspend fun findById(id: String): Memory? = dao.findById(id)?.toDomain()

    override suspend fun create(content: String, tag: String?, now: Long): Memory {
        require(content.isNotBlank()) { "Memory content cannot be blank." }
        val stability = InitialStabilityDays
        val memory = Memory(
            id = UUID.randomUUID().toString(), content = content.trim(), tag = tag?.trim()?.takeIf(String::isNotBlank),
            createdAt = now, lastReviewedAt = null, stabilityDays = stability,
            nextReviewAt = now + curve.nextIntervalDays(stability) * DayMillis,
            status = MemoryStatus.Active, reviewCount = 0,
        )
        dao.upsert(memory.toEntity())
        scheduler.schedule(memory)
        return memory
    }

    override suspend fun applyFeedback(id: String, feedback: RecallFeedback, now: Long): Memory? {
        val current = findById(id) ?: return null
        val updated = if (feedback == RecallFeedback.NoLongerNeeded) {
            current.copy(status = MemoryStatus.Archived, lastReviewedAt = now)
        } else {
            val stability = curve.adjustStability(current.stabilityDays, feedback)
            current.copy(
                lastReviewedAt = now, stabilityDays = stability,
                nextReviewAt = now + curve.nextIntervalDays(stability) * DayMillis,
                reviewCount = current.reviewCount + 1,
            )
        }
        dao.upsert(updated.toEntity())
        if (updated.status == MemoryStatus.Archived) scheduler.cancel(id) else scheduler.schedule(updated)
        return updated
    }

    override suspend fun reactivate(id: String, now: Long): Memory? {
        val archived = findById(id)?.takeIf { it.status == MemoryStatus.Archived } ?: return null
        val stability = InitialStabilityDays
        val active = archived.copy(
            status = MemoryStatus.Active, stabilityDays = stability,
            nextReviewAt = now + curve.nextIntervalDays(stability) * DayMillis,
        )
        dao.upsert(active.toEntity())
        scheduler.schedule(active)
        return active
    }

    private companion object {
        const val InitialStabilityDays = 3.0
        const val DayMillis = 24L * 60L * 60L * 1000L
    }
}
