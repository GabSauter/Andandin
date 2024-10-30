package com.example.walkapp.views.historicscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HistoricScreen(
    authUserId: String,
    navController: NavHostController,
    walkHistoric: List<WalkHistoryItem>?,
    needToLoadHistoric: Boolean,
    loadWalkHistory: (String) -> Unit,
    setNeedToLoadHistoric: (Boolean) -> Unit,
    isEndReached: Boolean,
    loading: Boolean
) {
    var selectedFilter by remember { mutableIntStateOf(0) }
    val filteredWalks = remember(walkHistoric, selectedFilter) {
        when (selectedFilter) {
            0 -> walkHistoric
            1 -> walkHistoric?.filter { isToday(it.date.split(" ")[0]) }
            else -> walkHistoric
        }
    }

    LaunchedEffect(needToLoadHistoric) {
        if (walkHistoric == null || needToLoadHistoric) {
            loadWalkHistory(authUserId)
            setNeedToLoadHistoric(false)
        }
    }

    if (walkHistoric == null || needToLoadHistoric) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primaryContainer),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .weight(.4f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Spacer(modifier = Modifier.weight(.6f))

                Text(
                    text = "Histórico",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.weight(1f))
            }
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
                        TopButtons(
                            selectedIndex = selectedFilter,
                            onSelectionChanged = { selectedFilter = it })
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (filteredWalks.isNullOrEmpty()) {
                    item {
                        Text(
                            text = "Nenhuma caminhada encontrada.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    items(filteredWalks.size) { index ->
                        WalkHistoryCard(filteredWalks[index])

                        if (index == filteredWalks.size - 1 && !isEndReached) {
                            loadWalkHistory(authUserId)
                        }
                    }

                    item {
                        if (loading) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
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
    val distanceInKm = item.distance / 1000
    val elapsedTimeInHours = convertElapsedTimeToHours(item.time)
    val velocity = if (elapsedTimeInHours > 0) distanceInKm / elapsedTimeInHours else 0.0

    val day = item.date.split(" ")[0]
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = "  $day  ",
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
            text = "Distância: ${item.distance} m",
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
    val today =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
    return dateString == today
}

data class WalkHistoryItem(
    val date: String,
    val distance: Double,
    val time: Long
)