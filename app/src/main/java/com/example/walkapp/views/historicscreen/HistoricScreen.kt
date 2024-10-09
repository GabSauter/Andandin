package com.example.walkapp.views.historicscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.walkapp.viewmodels.HistoricViewModel
import com.google.firebase.auth.FirebaseUser
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HistoricScreen(user: FirebaseUser?) {
    val historicViewModel = koinViewModel<HistoricViewModel>()
    val walkHistoric by historicViewModel.walkHistory.collectAsState()

    var selectedFilter by remember { mutableIntStateOf(0) }
    val filteredWalks = remember(walkHistoric, selectedFilter) {
        when (selectedFilter) {
            0 -> walkHistoric
            1 -> walkHistoric?.filter { isToday(it.date) }
            else -> walkHistoric
        }
    }

    LaunchedEffect(walkHistoric) {
        if (walkHistoric == null && user != null) {
            historicViewModel.loadWalkHistory(user.uid)
        }
    }

    if (walkHistoric == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (filteredWalks.isNullOrEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nenhuma caminhada encontrada.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TopButtons(selectedIndex = selectedFilter, onSelectionChanged = { selectedFilter = it })
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(filteredWalks.size) { index ->
                WalkHistoryCard(filteredWalks[index])

                if (index == walkHistoric!!.size - 1) {
                    historicViewModel.loadWalkHistory(user?.uid ?: "")
                }
            }
        }
    }
}

@Composable
fun TopButtons(selectedIndex: Int, onSelectionChanged: (Int) -> Unit) {
    val options = listOf("Tudo", "Hoje")
    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = { onSelectionChanged(index) },
                selected = index == selectedIndex
            ) {
                Text(label)
            }
        }
    }
}

@Composable
fun WalkHistoryCard(item: WalkHistoryItem) {
    val distanceInKm = item.distance
    val elapsedTimeInHours = convertElapsedTimeToHours(item.time)
    val velocity = if (elapsedTimeInHours > 0) distanceInKm / elapsedTimeInHours else 0.0
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = "  ${item.date}  ",
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier
                .background(
                    shape = Shapes().medium,
                    color = MaterialTheme.colorScheme.tertiaryContainer
                )
                .padding(2.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Distância: %.2f km".format(item.distance),
        )
        Text(
            text = "Tempo: ${formatElapsedTime(item.time)}",
        )
        Text(
            text = "Velocidade: %.2f km/h".format(velocity),
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
    }
}

fun convertElapsedTimeToHours(elapsedTimeMs: Long): Double {
    return elapsedTimeMs / (1000.0 * 60.0 * 60.0)
}

fun formatElapsedTime(elapsedTimeMs: Long): String {
    val totalSeconds = elapsedTimeMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

fun isToday(dateString: String): Boolean {
    val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
    return dateString == today
}

data class WalkHistoryItem(
    val date: String,
    val distance: Double,
    val time: Long
)