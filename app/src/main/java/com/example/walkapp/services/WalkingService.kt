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
import com.example.walkapp.helpers.LocationManager
import com.example.walkapp.repositories.UserRepository
import com.example.walkapp.repositories.WalkRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.Locale

class WalkingService : Service() {

    private val walkRepository: WalkRepository by inject()

    private lateinit var locationManager: LocationManager
    private var startTime = 0L
    private var timerJob: Job? = null
    private var trackingJob: Job? = null
    private var userId: String? = null

    companion object {
        const val CHANNEL_ID = "WalkingServiceChannel"
        const val NOTIFICATION_ID = 1
        const val ACTION_STOP = "STOP_WALKING_SERVICE"

        val isTracking = MutableStateFlow(false)
        val pathPoints = MutableStateFlow(emptyList<LatLng>())
        val totalDistance = MutableStateFlow(0)
        val elapsedTime = MutableStateFlow(0L)
        val loading = MutableStateFlow(false)
        private val _needToLoadXp = MutableStateFlow(false)
        val needToLoadXp: StateFlow<Boolean> = _needToLoadXp
        private val _needToLoadHistoric = MutableStateFlow(false)
        val needToLoadHistoric: StateFlow<Boolean> = _needToLoadHistoric
        private val _needToLoadPerformance = MutableStateFlow(false)
        val needToLoadPerformance: StateFlow<Boolean> = _needToLoadPerformance

        fun setNeedToLoadXp(value: Boolean) {
            _needToLoadXp.value = value
        }

        fun setNeedToLoadHistoric(value: Boolean) {
            _needToLoadHistoric.value = value
        }

        fun setNeedToLoadPerformance(value: Boolean) {
            _needToLoadPerformance.value = value
        }

        fun addPathPoint(location: LatLng) {
            val points = pathPoints.value.toMutableList()
            points.add(location)
            pathPoints.value = points

            if (points.size > 1) {
                val previousPoint = points[points.size - 2]
                val newDistance = calculateDistance(previousPoint, location)
                totalDistance.value += newDistance.toInt()
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

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startTracking()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        userId = intent?.getStringExtra("userId") ?: userId
        if (intent?.action == ACTION_STOP) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            userId?.let {
                saveWalkingData(it, totalDistance.value, elapsedTime.value)
            }
            stopTimer()
            isTracking.value = false
            pathPoints.value = emptyList()
            totalDistance.value = 0
            stopSelf()
            return START_NOT_STICKY
        } else {
            startForeground(NOTIFICATION_ID, createNotification(0, 0L))
            isTracking.value = true
            observeTrackingData()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)

        trackingJob?.cancel()
    }

    private fun saveWalkingData(
        userId: String,
        distance: Int,
        elapsedTime: Long,
        //onComplete: () -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                loading.value = true
//                if(distance > 10 && elapsedTime > 10000) {
                walkRepository.completeWalk(
                    userId = userId,
                    distance = distance,
                    elapsedTime = elapsedTime,
                )
                _needToLoadXp.value = true
                _needToLoadHistoric.value = true
                _needToLoadPerformance.value = true
//                }else{
                //colocar alguma coisa dizendo que não salvou, pois precisa caminhar pelo menos 10m e 10s
//                }
            } catch (e: Exception) {
                //colocar alguma coisa dizendo que não salvou, pois houve um erro
                Log.e("WalkingService", "Failed to save walking data", e)
            } finally {
                loading.value = false
                //onComplete()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(distance: Int, time: Long): Notification {
        val stopIntent = Intent(this, WalkingService::class.java).apply {
            action = ACTION_STOP
            putExtra("userId", userId)
        }
        val pendingStopIntent = PendingIntent.getService(
            this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingNotificationIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val formattedTime = formatElapsedTime(time)
        val formattedDistance = distance.toString()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Andandin")
            .setContentText("Distancia: $formattedDistance meters\nTempo: $formattedTime")
            .setSmallIcon(R.drawable.ic_walk)
            .setContentIntent(pendingNotificationIntent)
            .addAction(R.drawable.ic_walk, "Parar caminhada", pendingStopIntent)
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
        locationManager = LocationManager
        locationManager.initialize(this)
        startLocationUpdates()

        startTimer()
    }

    private fun startLocationUpdates() {
        locationManager.startLocationUpdates()

        trackingJob = CoroutineScope(Dispatchers.Main).launch {
            locationManager.locationState.collect { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    addPathPoint(latLng)
                }
            }
        }
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
                val notification = createNotification(distance, time)
                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
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