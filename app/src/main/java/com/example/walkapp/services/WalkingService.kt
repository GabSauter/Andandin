package com.example.walkapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.walkapp.MainActivity
import com.example.walkapp.R
import com.example.walkapp.helpers.LocationManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow

class WalkingService : Service() {

    companion object {
        const val CHANNEL_ID = "WalkingServiceChannel"
        const val NOTIFICATION_ID = 1
        const val ACTION_STOP = "STOP_WALKING_SERVICE"

        val isTracking = MutableStateFlow(false)
        val pathPoints = MutableStateFlow(emptyList<LatLng>())
    }

    private fun addPathPoint(location: LatLng) {
        val points = pathPoints.value.toMutableList()
        points.add(location)
        pathPoints.value = points
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()

            val appIntent = Intent(this, MainActivity::class.java)
            appIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(appIntent)
        } else {
            startForeground(NOTIFICATION_ID, createNotification())
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(): Notification {
        val stopIntent = Intent(this, WalkingService::class.java).apply {
            action = ACTION_STOP
        }
        val pendingStopIntent = PendingIntent.getService(
            this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingNotificationIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Walking App")
            .setContentText("Walking in progress...")
            .setSmallIcon(R.drawable.ic_google_logo)
            .setContentIntent(pendingNotificationIntent)
            .addAction(R.drawable.ic_google_logo, "Stop", pendingStopIntent)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Walking Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
}