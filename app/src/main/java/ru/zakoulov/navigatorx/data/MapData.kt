package ru.zakoulov.navigatorx.data

data class MapData(
    val markers: List<Marker>,
    val pathDots: Map<String, PathDot>,
    val pathConnections: Map<String, List<String>>
)
