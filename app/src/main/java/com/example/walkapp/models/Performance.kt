package com.example.walkapp.models

//TODO: Não precisa da distacia de hoje, pois ela já está no distanceLast7Days, da para adicionar um que mostra o tempo total de caminhada hoje e na semana, por causa da OMS
class Performance(
    val distanceTotal: Double,
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
                distanceTotal = (data["distanceTotal"] as Number).toDouble(),
                distanceLast7Days = (data["distanceLast7Days"] as List<Map<String, Any>>).map { dayData ->
                    DistanceDay(
                        distance = (dayData["distance"] as Number).toDouble(),
                        day = dayData["day"] as String
                    )
                },
                distanceLast12Months = (data["distanceLast12Months"] as List<Map<String, Any>>).map { monthData ->
                    DistanceMonth(
                        distance = (monthData["distance"] as Number).toDouble(),
                        month = monthData["month"] as String
                    )
                }
            )
        }
    }
}

data class DistanceDay(val distance: Double, val day: String)
data class DistanceMonth(val distance: Double, val month: String)