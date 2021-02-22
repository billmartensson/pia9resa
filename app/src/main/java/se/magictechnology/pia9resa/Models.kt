package se.magictechnology.pia9resa

data class ApiData(val StopLocation: List<StopLoc>)
data class StopLoc(val id : String, val name: String)


data class TravelInfo(val Trip : List<Tripinfo>)
data class Tripinfo(val LegList : LegListInfo)
data class LegListInfo(val Leg : List<LegInfo>)
data class LegInfo(val Origin : StopTravelInfo, val Destination : StopTravelInfo)
data class StopTravelInfo(val name : String, val time : String)
