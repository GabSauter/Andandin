package com.example.walkapp.views.historicscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walkapp.viewmodels.HistoricViewModel
import com.google.firebase.auth.FirebaseUser
import org.koin.androidx.compose.koinViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HistoricScreen(user: FirebaseUser?) {
    val historicViewModel = koinViewModel<HistoricViewModel>()
    val walkHistoric by historicViewModel.walkHistory.collectAsState()

    LaunchedEffect(walkHistoric) {
        if(walkHistoric == null && user != null){
            historicViewModel.loadWalkHistory(user.uid)
        }
    }

    if (walkHistoric == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    } else{
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(walkHistoric!!.size) { index ->
                WalkHistoryCard(walkHistoric!![index])

                if (index == walkHistoric!!.size - 1) {
                    historicViewModel.loadWalkHistory(user?.uid ?: "")
                }
            }
        }
    }
}

@Composable
fun WalkHistoryCard(item: WalkHistoryItem) {
    val distanceInKm = item.distance
    val elapsedTimeInHours = convertElapsedTimeToHours(item.time)
    val velocity = if (elapsedTimeInHours > 0) distanceInKm / elapsedTimeInHours else 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Dia: ${item.date}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Distância: : %.2f km".format(item.distance),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp
                )
            )
            Text(
                text = "Tempo: ${formatElapsedTime(item.time)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Velocidade: %.2f km/h".format(velocity),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

fun convertElapsedTimeToHours(elapsedTimeMs: Long): Double {
    return elapsedTimeMs / (1000.0 * 60.0 * 60.0)
}

fun formatTimestamp(timestamp: String): String {
    val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    return try {
        val date = inputFormat.parse(timestamp)
        outputFormat.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
        "Invalid date"
    }
}

fun formatElapsedTime(elapsedTimeMs: Long): String {
    val totalSeconds = elapsedTimeMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

data class WalkHistoryItem(
    val date: String,
    val distance: Double,
    val time: Long
)