package com.echo.app.data.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.echo.app.MainActivity
import com.echo.app.domain.model.Memory

class NotificationPublisher(private val context: Context) {
    fun createChannel() {
        val channel = NotificationChannel(ChannelId, "未来记忆", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "在记忆适合重看的时候轻声提醒你"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun show(memory: Memory) {
        if (android.os.Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val intent = Intent(context, MainActivity::class.java).putExtra(MainActivity.RecallMemoryIdKey, memory.id)
        val pendingIntent = PendingIntent.getActivity(
            context,
            memory.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, ChannelId)
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .setContentTitle("过去的自己")
            .setContentText("这句话可能正在慢慢被遗忘，是时候重新看看它了。")
            .setStyle(NotificationCompat.BigTextStyle().bigText(memory.content))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        context.getSystemService(NotificationManager::class.java).notify(memory.id.hashCode(), notification)
    }

    private companion object { const val ChannelId = "echo_recalls" }
}
