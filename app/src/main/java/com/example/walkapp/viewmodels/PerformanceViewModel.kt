package com.example.walkapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.models.DistanceDay
import com.example.walkapp.models.DistanceMonth
import com.example.walkapp.repositories.PerformanceRepository
import com.example.walkapp.services.WalkingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class PerformanceUiState(
    val distanceTotal: Int = 0,
    val sumOfDistanceToday: Int = 0,
    val sumOfDistanceLast7Days: Int = 0,
    val distancesOfLast7Days: List<DistanceDay> = emptyList(),
    val distancesOfLast12Months: List<DistanceMonth> = emptyList()
)

class PerformanceViewModel(private val performanceRepository: PerformanceRepository): ViewModel() {
    private val _performanceUiState = MutableStateFlow(PerformanceUiState())
    val performanceUiState: StateFlow<PerformanceUiState> = _performanceUiState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    val needToLoadPerformance = WalkingService.needToLoadPerformance

    init{
        setNeedToLoadPerformance(true)
    }

    fun loadPerformanceData(userId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val performanceData = performanceRepository.getPerformanceData(userId)
                Log.d("PerformanceViewModel", "loadPerformanceData: $performanceData")
                _performanceUiState.update {
                    it.copy(
                        distanceTotal = performanceData.distanceTotal,
                    )
                }
                val performance7LastDays = mergeDataWithLast7Days(performanceData.distanceLast7Days)
                val performance12LastMonths = mergeDataWithLast12Months(performanceData.distanceLast12Months)
                _performanceUiState.update {
                    it.copy(
                        sumOfDistanceToday = performance7LastDays.firstOrNull()?.distance ?: 0,
                        sumOfDistanceLast7Days = performance7LastDays.sumOf { distanceLast7Days -> distanceLast7Days.distance},
                        distancesOfLast7Days = performance7LastDays,
                        distancesOfLast12Months = performance12LastMonths
                    )
                }
                _error.value = null
                setNeedToLoadPerformance(false)
            } catch (e: Exception) {
                Log.e("PerformanceViewModel", "Error loading performance data", e)
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

    private fun getCurrentWeek(): List<String> {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val currentWeek = mutableListOf<String>()
        for (i in 0..6) {
            currentWeek.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return currentWeek
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

    private fun getCurrentYearMonths(): List<String> {
        val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, Calendar.JANUARY)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val monthsOfCurrentYear = mutableListOf<String>()
        for (i in 0..11) {
            monthsOfCurrentYear.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.MONTH, 1)
        }
        return monthsOfCurrentYear
    }

    private fun mergeDataWithLast7Days(originalData: List<DistanceDay>): List<DistanceDay> {
        val last7Days = getLast7Days()
        val originalDataMap = originalData.associateBy { it.day }

        return last7Days.map { day ->
            originalDataMap[day] ?: DistanceDay(0, day)
        }
    }

    private fun mergeDataWithCurrentWeek(originalData: List<DistanceDay>): List<DistanceDay> {
        val last7Days = getCurrentWeek()
        val originalDataMap = originalData.associateBy { it.day }

        return last7Days.map { day ->
            originalDataMap[day] ?: DistanceDay(0, day)
        }
    }

    private fun mergeDataWithLast12Months(originalData: List<DistanceMonth>): List<DistanceMonth> {
        val last12Months = getLast12Months()
        val originalDataMap = originalData.associateBy { it.month }

        return last12Months.map { month ->
            originalDataMap[month] ?: DistanceMonth(0, month)
        }
    }

    private fun mergeDataWithCurrentYearMonths(originalData: List<DistanceDay>): List<DistanceDay> {
        val last7Days = getCurrentYearMonths()
        val originalDataMap = originalData.associateBy { it.day }

        return last7Days.map { day ->
            originalDataMap[day] ?: DistanceDay(0, day)
        }
    }

    fun setNeedToLoadPerformance(value: Boolean) {
        WalkingService.setNeedToLoadPerformance(value)
    }
}
