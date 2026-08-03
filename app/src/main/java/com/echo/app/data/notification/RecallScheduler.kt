package com.echo.app.data.notification

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.echo.app.domain.model.Memory
import java.util.concurrent.TimeUnit

class RecallScheduler(
    private val workManager: WorkManager,
    private val now: () -> Long = System::currentTimeMillis,
) {
    fun schedule(memory: Memory) {
        val delay = (memory.nextReviewAt - now()).coerceAtLeast(0)
        val request = OneTimeWorkRequestBuilder<RecallWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(RecallWorker.MemoryIdKey to memory.id))
            .build()
        workManager.enqueueUniqueWork(workName(memory.id), ExistingWorkPolicy.REPLACE, request)
    }

    fun cancel(memoryId: String) = workManager.cancelUniqueWork(workName(memoryId))

    private fun workName(memoryId: String) = "echo-recall-$memoryId"
}
