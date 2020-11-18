package ru.zakoulov.navigatorx.viewmodel

import ru.zakoulov.navigatorx.data.Building
import ru.zakoulov.navigatorx.data.MapData
import ru.zakoulov.navigatorx.data.Marker

sealed class State {
    class Loading() : State()

    data class Map(
        val mapState: MapState
    ) : State()
}

sealed class MapState(
    open val buildings: List<Building>,
    open val mapData: MapData,
    open val selectedBuilding: Building,
    open val floor: Int,
    open val departureMarker: Marker?,
    open val destinationMarker: Marker?,
    open val pathInfo: FullPathInfo?
) {
    data class Viewing(
        override val buildings: List<Building>,
        override val mapData: MapData,
        override val selectedBuilding: Building,
        override val floor: Int,
        override val departureMarker: Marker? = null,
        override val destinationMarker: Marker? = null,
        override val pathInfo: FullPathInfo? = null
    ) : MapState(
        buildings = buildings,
        mapData = mapData,
        selectedBuilding = selectedBuilding,
        floor = floor,
        departureMarker = departureMarker,
        destinationMarker = destinationMarker,
        pathInfo = pathInfo
    )

    data class MarkerSelected(
        override val buildings: List<Building>,
        override val mapData: MapData,
        override val selectedBuilding: Building,
        override val floor: Int,
        override val departureMarker: Marker? = null,
        override val destinationMarker: Marker? = null,
        override val pathInfo: FullPathInfo? = null,
        val selectedMarker: Marker
    ) : MapState(
        buildings = buildings,
        mapData = mapData,
        selectedBuilding = selectedBuilding,
        floor = floor,
        departureMarker = departureMarker,
        destinationMarker = destinationMarker,
        pathInfo = pathInfo
    )

    data class RoomPicking(
        override val buildings: List<Building>,
        override val mapData: MapData,
        override val selectedBuilding: Building,
        override val floor: Int,
        override val departureMarker: Marker? = null,
        override val destinationMarker: Marker? = null,
        override val pathInfo: FullPathInfo? = null
    ) : MapState(
        buildings = buildings,
        mapData = mapData,
        selectedBuilding = selectedBuilding,
        floor = floor,
        departureMarker = departureMarker,
        destinationMarker = destinationMarker,
        pathInfo = pathInfo
    )
}
