package com.echo.app.domain.model

data class Memory(
    val id: String,
    val content: String,
    val tag: String?,
    val createdAt: Long,
    val lastReviewedAt: Long?,
    val stabilityDays: Double,
    val nextReviewAt: Long,
    val status: MemoryStatus,
    val reviewCount: Int,
)

enum class MemoryStatus { Active, Archived }

enum class RecallFeedback { Important, Changed, NoLongerNeeded }
