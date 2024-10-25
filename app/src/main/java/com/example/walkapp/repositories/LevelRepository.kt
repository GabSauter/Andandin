package com.example.walkapp.repositories

import com.example.walkapp.models.Level
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow

class LevelRepository(private val performanceRepository: PerformanceRepository) {
    suspend fun getLevel(userId: String): Level {
        val performance = performanceRepository.getPerformanceData(userId)
        return calculateLevel(performance.distanceTotal)
    }

    fun calculateLevel(xp: Int): Level {
        val c = 10.0
        val d = 1.5

        val currentLevel = floor(c * (ln((xp + 1).toDouble()).pow(d))).toInt()
        val currentLevelDistance = exp((currentLevel / c).pow(1 / d)) - 1

        val nextLevel = currentLevel + 1
        val nextLevelDistance = exp((nextLevel / c).pow(1 / d)) - 1
        val progressPercentage = ((xp - currentLevelDistance) /
                (nextLevelDistance - currentLevelDistance)) * 100

        return Level(currentLevel, progressPercentage)
    }
}