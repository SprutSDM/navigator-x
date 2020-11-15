package ru.zakoulov.navigatorx.data

data class MapData(val markers: List<Marker>, val pathDots: List<PathDot>) {
    fun filterByFloor(floor: Int): MapData {
        return MapData(markers = markers.filter { it.floor == floor }, pathDots = pathDots)
    }
}
