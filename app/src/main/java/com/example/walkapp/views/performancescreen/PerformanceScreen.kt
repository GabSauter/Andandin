package com.example.walkapp.views.performancescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PerformanceScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        // Chart for steps walked each day
        Text(text = "Steps (Day)")
        BarChartWithStaticData(
            data = listOf(5000f, 7000f, 8000f, 12000f, 15000f), // Example day data
            color = Color.Green,
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Chart for steps walked each week
        Text(text = "Steps (Week)")
        BarChartWithStaticData(
            data = listOf(35000f, 40000f, 42000f, 48000f, 50000f, 52000f), // Example week data
            color = Color.Blue,
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Chart for steps walked each month
        Text(text = "Steps (Month)")
        BarChartWithStaticData(
            data = listOf(150000f, 160000f, 170000f, 180000f), // Example month data
            color = Color.Red,
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )
    }
}

@Composable
fun BarChartWithStaticData(data: List<Float>, color: Color, modifier: Modifier = Modifier) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(Unit) { modelProducer.runTransaction { columnSeries { series(data) } } }

    CartesianChartHost(
        rememberCartesianChart(
            rememberColumnCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom()
        ),
        modelProducer,
    )
}