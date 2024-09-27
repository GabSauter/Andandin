package com.example.walkapp.views.performancescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import com.patrykandpatrick.vico.core.common.component.TextComponent
import java.text.DateFormatSymbols
import java.util.Locale

@Composable
fun PerformanceScreen(modifier: Modifier = Modifier) {
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
            Text(text = "Total: 50000m")
            Text(text = "Hoje: 12000m")
            Text(text = "Esta semana: 35000m")
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
            BarChartWithStaticData(
                y = listOf(5000f, 7000f, 8000f, 12000f, 15000f, 5000f, 7000f),
                x = weekBottomAxisValueFormatter
            )
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
            BarChartWithStaticData(
                y = listOf(5000f, 7000f, 8000f, 12000f, 15000f, 5000f, 7000f, 8000f, 12000f, 15000f, 2f, 1f),
                x = monthBottomAxisValueFormatter
            )
        }
    }
}

@Composable
fun BarChartWithStaticData(y: List<Float>, x: CartesianValueFormatter) {
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

private var monthNames = DateFormatSymbols.getInstance(Locale.getDefault()).shortMonths
private val monthBottomAxisValueFormatter = CartesianValueFormatter { _, x, _ ->
    monthNames[x.toInt() % 12]
}

private var weekNames = DateFormatSymbols.getInstance(Locale.getDefault()).shortWeekdays
private val weekBottomAxisValueFormatter = CartesianValueFormatter { _, x, _ ->
    weekNames[(x.toInt() % 7) + 1]
}