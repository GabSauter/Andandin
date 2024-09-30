package com.example.walkapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.models.DistanceDay
import com.example.walkapp.models.DistanceMonth
import com.example.walkapp.repositories.PerformanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

data class PerformanceUiState(
    val distanceTotal: Double = .0,
    val distanceToday: Double = .0,
    val distanceWeek: Double = .0,
    val distanceLast7Days: List<DistanceDay> = emptyList(),
    val distanceLast12Months: List<DistanceMonth> = emptyList()
)

class PerformanceViewModel(private val performanceRepository: PerformanceRepository): ViewModel() {
    private val _performanceUiState = MutableStateFlow(PerformanceUiState())
    val performanceUiState: StateFlow<PerformanceUiState> = _performanceUiState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadPerformanceData(userId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                Log.d("PerformanceViewModel", "Chama a função getPerformanceData")
                val performanceData = performanceRepository.getPerformanceData(userId)
                Log.d("PerformanceViewModel", "loadPerformanceData: $performanceData")
                if (performanceData != null) {
                    _performanceUiState.update {
                        it.copy(
                            distanceTotal = performanceData.distanceTotal,
                            distanceToday = performanceData.distanceToday,
                            distanceWeek = performanceData.distanceWeek
                        )
                    }
                    val performance7LastDays = mergeDataWithLast7Days(performanceData.distanceLast7Days)
                    val performance12LastMonths =mergeDataWithLast12Months(performanceData.distanceLast12Months)
                    _performanceUiState.update {
                        it.copy(
                            distanceLast7Days = performance7LastDays,
                            distanceLast12Months = performance12LastMonths
                        )
                    }
                }
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Houve um erro ao tentar carregar os dados de performance."
            }finally {
                _loading.value = false
            }
        }
    }

    fun getLast7Days(): List<String> {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val last7Days = mutableListOf<String>()
        for (i in 0..6) {
            last7Days.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        return last7Days
    }

    fun getLast12Months(): List<String> {
        val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val last12Months = mutableListOf<String>()
        for (i in 0..11) {
            last12Months.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.MONTH, -1)
        }
        return last12Months
    }

    private fun mergeDataWithLast7Days(originalData: List<DistanceDay>): List<DistanceDay> {
        val last7Days = getLast7Days()  // Get the last 7 days
        val originalDataMap = originalData.associateBy { it.day }  // Convert original data to a map for quick lookup

        return last7Days.map { day ->
            originalDataMap[day] ?: DistanceDay(0, day)  // If day exists, use it; otherwise, create a new entry with distance 0
        }
    }

    private fun mergeDataWithLast12Months(originalData: List<DistanceMonth>): List<DistanceMonth> {
        val last12Months = getLast12Months()  // Get the last 12 months
        val originalDataMap = originalData.associateBy { it.month }  // Convert original data to a map for quick lookup

        return last12Months.map { month ->
            originalDataMap[month] ?: DistanceMonth(0, month)  // If month exists, use it; otherwise, create a new entry with distance 0
        }
    }
}
