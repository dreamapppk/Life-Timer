package com.lifetimer

import android.app.*
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import java.util.*

class TimerService : Service() {

    private val channelId = "LifeTimerChannel"
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startTimer()
    }

    private fun startTimer() {
        handler.post(object : Runnable {
            override fun run() {
                val minutesLeft = getMinutesLeftToday()
                val notification = buildNotification(minutesLeft)
                startForeground(1, notification)
                handler.postDelayed(this, 60000)
            }
        })
    }

    private fun getMinutesLeftToday(): Int {
        val now = Calendar.getInstance()
        val totalMinutesNow = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        return 1440 - totalMinutesNow
    }

    private fun buildNotification(minutes: Int): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("LifeTimer Running")
            .setContentText("Minutes left today: $minutes")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "Life Timer Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}