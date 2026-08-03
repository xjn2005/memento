package com.echo.app.presentation

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val zone: ZoneId get() = ZoneId.systemDefault()
private val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
private val monthFormatter = DateTimeFormatter.ofPattern("M月")

fun formatMemoryDate(epochMillis: Long): String = dateFormatter.format(Instant.ofEpochMilli(epochMillis).atZone(zone))
fun yearOf(epochMillis: Long): String = Instant.ofEpochMilli(epochMillis).atZone(zone).year.toString()
fun monthOf(epochMillis: Long): String = monthFormatter.format(Instant.ofEpochMilli(epochMillis).atZone(zone))
fun formatReviewHint(epochMillis: Long): String = "将在 ${formatMemoryDate(epochMillis)} 重新出现"
