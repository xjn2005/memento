package com.echo.app

import android.app.Application
import androidx.room.Room
import androidx.work.WorkManager
import com.echo.app.data.local.EchoDatabase
import com.echo.app.data.notification.NotificationPublisher
import com.echo.app.data.notification.RecallScheduler
import com.echo.app.data.repository.RoomMemoryRepository
import com.echo.app.domain.algorithm.ForgettingCurve
import com.echo.app.domain.repository.MemoryRepository

class EchoApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        val publisher = NotificationPublisher(this)
        publisher.createChannel()
        val database = Room.databaseBuilder(this, EchoDatabase::class.java, "echo.db").build()
        container = AppContainer(
            memoryRepository = RoomMemoryRepository(
                dao = database.memoryDao(),
                curve = ForgettingCurve(),
                scheduler = RecallScheduler(WorkManager.getInstance(this)),
            ),
            notificationPublisher = publisher,
        )
    }
}

data class AppContainer(
    val memoryRepository: MemoryRepository,
    val notificationPublisher: NotificationPublisher,
)
