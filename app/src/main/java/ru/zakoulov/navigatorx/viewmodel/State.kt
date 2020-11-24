package ru.zakoulov.navigatorx.viewmodel

import ru.zakoulov.navigatorx.data.Building
import ru.zakoulov.navigatorx.data.MapData
import ru.zakoulov.navigatorx.data.Marker
import ru.zakoulov.navigatorx.ui.map.MarkerData

sealed class State {
    class Loading() : State()

    data class Map(
        val mapState: MapState
    ) : State()
}

sealed class MapState(
    open val buildings: List<Building>,
    open val markers: List<MarkerData>,
    open val selectedBuilding: Building,
    open val floor: Int,
    open val departureMarker: Marker?,
    open val destinationMarker: Marker?,
    open val pathInfo: FullPathInfo?,
    open val floorPaths: FloorPaths?
) {
    data class Viewing(
        override val buildings: List<Building>,
        override val markers: List<MarkerData>,
        override val selectedBuilding: Building,
        override val floor: Int,
        override val departureMarker: Marker? = null,
        override val destinationMarker: Marker? = null,
        override val pathInfo: FullPathInfo? = null,
        override val floorPaths: FloorPaths? = null
    ) : MapState(
        buildings = buildings,
        markers = markers,
        selectedBuilding = selectedBuilding,
        floor = floor,
        departureMarker = departureMarker,
        destinationMarker = destinationMarker,
        pathInfo = pathInfo,
        floorPaths = floorPaths
    )

    data class MarkerSelected(
        override val buildings: List<Building>,
        override val markers: List<MarkerData>,
        override val selectedBuilding: Building,
        override val floor: Int,
        override val departureMarker: Marker? = null,
        override val destinationMarker: Marker? = null,
        override val pathInfo: FullPathInfo? = null,
        override val floorPaths: FloorPaths? = null,
        val selectedMarker: Marker
    ) : MapState(
        buildings = buildings,
        markers = markers,
        selectedBuilding = selectedBuilding,
        floor = floor,
        departureMarker = departureMarker,
        destinationMarker = destinationMarker,
        pathInfo = pathInfo,
        floorPaths = floorPaths
    )

    data class RoomPicking(
        override val buildings: List<Building>,
        override val markers: List<MarkerData>,
        override val selectedBuilding: Building,
        override val floor: Int,
        override val departureMarker: Marker? = null,
        override val destinationMarker: Marker? = null,
        override val pathInfo: FullPathInfo? = null,
        override val floorPaths: FloorPaths? = null
    ) : MapState(
        buildings = buildings,
        markers = markers,
        selectedBuilding = selectedBuilding,
        floor = floor,
        departureMarker = departureMarker,
        destinationMarker = destinationMarker,
        pathInfo = pathInfo,
        floorPaths = floorPaths
    )
}
