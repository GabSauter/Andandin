package com.example.walkapp.models

class Badges(
    var badge1: Boolean?,
    var badge2: Boolean?,
    var badge3: Boolean?,
    var badge4: Boolean?,
    var badge5: Boolean?,
    var badge6: Boolean?,
    var badge7: Boolean?,
    var badge8: Boolean?,
    var badge9: Boolean?
) {
    fun toMap(): Map<String, Boolean?> {
        return mapOf(
            "badge1" to badge1,
            "badge2" to badge2,
            "badge3" to badge3,
            "badge4" to badge4,
            "badge5" to badge5,
            "badge6" to badge6,
            "badge7" to badge7,
            "badge8" to badge8,
            "badge9" to badge9
        )
    }

    companion object{
        fun mapToBadge(data: Map<String, Any>): Badges{
            return Badges(
                badge1 = data["badge1"] as Boolean?,
                badge2 = data["badge2"] as Boolean?,
                badge3 = data["badge3"] as Boolean?,
                badge4 = data["badge4"] as Boolean?,
                badge5 = data["badge5"] as Boolean?,
                badge6 = data["badge6"] as Boolean?,
                badge7 = data["badge7"] as Boolean?,
                badge8 = data["badge8"] as Boolean?,
                badge9 = data["badge9"] as Boolean?
            )
        }
    }
}