package ru.zakoulov.navigatorx.data

data class MapData(
    val markers: List<Marker>,
    val pathDots: List<PathDot>,
    val pathConnections: Map<String, List<String>>
) {
    fun filterByFloor(floor: Int): MapData {
        return MapData(
            markers = markers.filter { it.floor == floor },
            pathDots = pathDots,
            pathConnections = pathConnections
        )
    }
}
