package com.example.walkapp.views.performancescreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.walkapp.viewmodels.PerformanceUiState
import com.example.walkapp.viewmodels.PerformanceViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import org.koin.androidx.compose.koinViewModel
import java.text.DateFormatSymbols
import java.util.Locale

@Composable
fun PerformanceScreen(userId: String) {
    val performanceViewModel = koinViewModel<PerformanceViewModel>()
    val performanceData by performanceViewModel.performanceUiState.collectAsState()
    val error by performanceViewModel.error.collectAsState()
    val loading by performanceViewModel.loading.collectAsState()

    LaunchedEffect(Unit) {
        performanceViewModel.loadPerformanceData(userId)
    }

    if(loading){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            CircularProgressIndicator()
        }
    }else{
        if(error != null){
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Text(text = error!!)
            }
        }else{
            val last7Days = performanceViewModel.getLast7Days()
            val last12Months = performanceViewModel.getLast12Months()
            PerformanceContent(performanceData, last7Days, last12Months)
        }
    }
}

@Composable
fun PerformanceContent(performanceData: PerformanceUiState, last7Days: List<String>, last12Months: List<String>){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item{
            Spacer(modifier = Modifier.padding(8.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Distâncias percorridas",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Text(text = "Total: ${performanceData.distanceTotal}m")
            Text(text = "Hoje: ${performanceData.distanceToday}m")
            Text(text = "Esta semana: ${performanceData.distanceWeek}m")
        }

        item{
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Dias (m/dia)",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if(performanceData.distanceLast7Days.isNotEmpty()){
                BarChartWithStaticData(
                    y = performanceData.distanceLast7Days.map { it.distance },
                    x = { _, x, _ ->
                        last7Days[(x.toInt() % 7)]
                    }
                )
            }
        }

        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Meses (m/mês)",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            if(performanceData.distanceLast7Days.isNotEmpty()){
                BarChartWithStaticData(
                    y = performanceData.distanceLast12Months.map { it.distance },
                    x = { _, x, _ ->
                        last12Months[(x.toInt() % 12)]
                    }
                )
            }
        }
    }
}

@Composable
fun BarChartWithStaticData(y: List<Number>, x: CartesianValueFormatter) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            columnSeries {
                series(y)
            }
        }
    }
    ProvideVicoTheme(rememberM3VicoTheme()){
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(
//                    title = "Metros",
//                    titleComponent = TextComponent()
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = x,
                    itemPlacer = remember {
                        HorizontalAxis.ItemPlacer.aligned(spacing = 2, addExtremeLabelPadding = true)
                    },
                ),
            ),
            modelProducer = modelProducer,
        )
    }
}