package com.echo.app.data.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.echo.app.EchoApplication

class RecallWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val memoryId = inputData.getString(MemoryIdKey) ?: return Result.failure()
        val memory = (applicationContext as EchoApplication).container.memoryRepository.findById(memoryId)
            ?: return Result.success()
        if (memory.nextReviewAt > System.currentTimeMillis()) return Result.success()
        (applicationContext as EchoApplication).container.notificationPublisher.show(memory)
        return Result.success()
    }

    companion object { const val MemoryIdKey = "memory_id" }
}
