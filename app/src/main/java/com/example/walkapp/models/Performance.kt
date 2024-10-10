package com.example.walkapp.models

//TODO: Da para adicionar um que mostra o tempo total de caminhada hoje e na semana, por causa da OMS
class Performance(
    val distanceTotal: Int,
    val distanceLast7Days: List<DistanceDay>,
    val distanceLast12Months: List<DistanceMonth>
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "distanceTotal" to distanceTotal,
            "distanceLast7Days" to distanceLast7Days,
            "distanceLast12Months" to distanceLast12Months
        )
    }

    companion object{
        fun mapToPerformance(data: Map<String, Any>): Performance{
            return Performance(
                distanceTotal = (data["distanceTotal"] as Number).toInt(),
                distanceLast7Days = (data["distanceLast7Days"] as List<Map<String, Any>>).map { dayData ->
                    DistanceDay(
                        distance = (dayData["distance"] as Number).toInt(),
                        day = dayData["day"] as String
                    )
                },
                distanceLast12Months = (data["distanceLast12Months"] as List<Map<String, Any>>).map { monthData ->
                    DistanceMonth(
                        distance = (monthData["distance"] as Number).toInt(),
                        month = monthData["month"] as String
                    )
                }
            )
        }
    }
}

data class DistanceDay(val distance: Int, val day: String)
data class DistanceMonth(val distance: Int, val month: String)