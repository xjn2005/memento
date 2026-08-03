package com.echo.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.echo.app.domain.model.Memory
import com.echo.app.domain.model.MemoryStatus

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey val id: String,
    val content: String,
    val tag: String?,
    val createdAt: Long,
    val lastReviewedAt: Long?,
    val stabilityDays: Double,
    val nextReviewAt: Long,
    val status: String,
    val reviewCount: Int,
)

fun MemoryEntity.toDomain() = Memory(
    id = id,
    content = content,
    tag = tag,
    createdAt = createdAt,
    lastReviewedAt = lastReviewedAt,
    stabilityDays = stabilityDays,
    nextReviewAt = nextReviewAt,
    status = MemoryStatus.valueOf(status),
    reviewCount = reviewCount,
)

fun Memory.toEntity() = MemoryEntity(
    id = id,
    content = content,
    tag = tag,
    createdAt = createdAt,
    lastReviewedAt = lastReviewedAt,
    stabilityDays = stabilityDays,
    nextReviewAt = nextReviewAt,
    status = status.name,
    reviewCount = reviewCount,
)
