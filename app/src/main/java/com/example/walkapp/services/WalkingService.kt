package com.example.walkapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.walkapp.MainActivity
import com.example.walkapp.R
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Locale

class WalkingService : Service() {

    companion object {
        const val CHANNEL_ID = "WalkingServiceChannel"
        const val NOTIFICATION_ID = 1
        const val ACTION_STOP = "STOP_WALKING_SERVICE"

        val isTracking = MutableStateFlow(false)
        val pathPoints = MutableStateFlow(emptyList<LatLng>())
        val totalDistance = MutableStateFlow(0.0)
        val elapsedTime = MutableStateFlow(0L)

        fun addPathPoint(location: LatLng) {
            val points = pathPoints.value.toMutableList()
            points.add(location)
            pathPoints.value = points

            if (points.size > 1) {
                val previousPoint = points[points.size - 2]
                val newDistance = calculateDistance(previousPoint, location)
                totalDistance.value += newDistance
            }
        }

        private fun calculateDistance(start: LatLng, end: LatLng): Double {
            val startLocation = Location("").apply {
                latitude = start.latitude
                longitude = start.longitude
            }

            val endLocation = Location("").apply {
                latitude = end.latitude
                longitude = end.longitude
            }

            return startLocation.distanceTo(endLocation).toDouble()
        }
    }

    private var startTime = 0L
    private var timerJob: Job? = null
    private var trackingJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startTracking()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        } else {
            startForeground(NOTIFICATION_ID, createNotification(0.0, 0L))
            isTracking.value = true
            observeTrackingData()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isTracking.value = false
        pathPoints.value = emptyList()
        totalDistance.value = 0.0
        stopTimer()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)

        trackingJob?.cancel()

        Log.d("WalkingService", "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(distance: Double, time: Long): Notification {
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

        val formattedTime = formatElapsedTime(time)
        val formattedDistance = String.format(Locale.getDefault(), "%.2f", distance)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Andandin")
            .setContentText("Distancia: $formattedDistance meters\nTempo: $formattedTime")
            .setSmallIcon(R.drawable.ic_google_logo)
            .setContentIntent(pendingNotificationIntent)
            .addAction(R.drawable.ic_google_logo, "Parar", pendingStopIntent)
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

    private fun startTracking() {
        startTimer()
    }

    private fun startTimer() {
        startTime = SystemClock.elapsedRealtime()
        timerJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                elapsedTime.value = SystemClock.elapsedRealtime() - startTime
                delay(1000L)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        elapsedTime.value = 0L
    }

    private fun observeTrackingData() {
        trackingJob = CoroutineScope(Dispatchers.Main).launch {
            combine(totalDistance, elapsedTime) { distance, time ->
                distance to time
            }.collect { (distance, time) ->
                Log.d("WalkingService", "Observing tracking data")
                val notification = createNotification(distance, time)
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
        }
    }

    private fun formatElapsedTime(timeInMillis: Long): String {
        val seconds = (timeInMillis / 1000) % 60
        val minutes = (timeInMillis / (1000 * 60)) % 60
        val hours = (timeInMillis / (1000 * 60 * 60))

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }
}