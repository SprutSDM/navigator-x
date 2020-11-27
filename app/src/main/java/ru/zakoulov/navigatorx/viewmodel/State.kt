package ru.zakoulov.navigatorx.viewmodel

import ru.zakoulov.navigatorx.data.Building
import ru.zakoulov.navigatorx.data.MapData
import ru.zakoulov.navigatorx.data.Marker
import ru.zakoulov.navigatorx.ui.map.MarkerData

sealed class State {
    class Loading() : State()

    data class Map(
        val mapState: MapState,
        val markers: List<MarkerData>,
        val selectedBuilding: Building,
        val floor: Int,
        val departureMarker: Marker?,
        val destinationMarker: Marker?,
        val pathInfo: FullPathInfo?,
        val floorPaths: FloorPaths?
    ) : State()
}

sealed class MapState {
    object Viewing : MapState()

    data class MarkerSelected(
        val selectedMarker: Marker
    ) : MapState()

    object RoomPicking : MapState()
}
