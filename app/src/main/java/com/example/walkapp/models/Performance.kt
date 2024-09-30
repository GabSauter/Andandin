package com.example.walkapp.models

class Performance(
    val distanceTotal: Double,
    val distanceToday: Double,
    val distanceWeek: Double,
    val distanceLast7Days: List<DistanceDay>,
    val distanceLast12Months: List<DistanceMonth>
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "distanceTotal" to distanceTotal,
            "distanceToday" to distanceToday,
            "distanceWeek" to distanceWeek,
            "distanceLast7Days" to distanceLast7Days,
            "distanceLast12Months" to distanceLast12Months
        )
    }

    companion object{
        fun mapToPerformance(data: Map<String, Any>): Performance{
            return Performance(
                distanceTotal = (data["distanceTotal"] as Number).toDouble(),
                distanceToday = (data["distanceToday"] as Number).toDouble(),
                distanceWeek = (data["distanceWeek"] as Number).toDouble(),
                distanceLast7Days = (data["distanceLast7Days"] as List<Map<String, Any>>).map { dayData ->
                    DistanceDay(
                        distance = dayData["distance"] as Number,
                        day = dayData["day"] as String
                    )
                },
                distanceLast12Months = (data["distanceLast12Months"] as List<Map<String, Any>>).map { monthData ->
                    DistanceMonth(
                        distance = monthData["distance"] as Number,
                        month = monthData["month"] as String
                    )
                }
            )
        }
    }
}

data class DistanceDay(val distance: Number, val day: String)
data class DistanceMonth(val distance: Number, val month: String)