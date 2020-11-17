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
    open val destinationMarker: Marker?
) {
    data class Viewing(
        override val buildings: List<Building>,
        override val mapData: MapData,
        override val selectedBuilding: Building,
        override val floor: Int,
        override val departureMarker: Marker? = null,
        override val destinationMarker: Marker? = null
    ) : MapState(
        buildings = buildings,
        mapData = mapData,
        selectedBuilding = selectedBuilding,
        floor = floor,
        departureMarker = departureMarker,
        destinationMarker = destinationMarker
    )

    data class MarkerSelected(
        override val buildings: List<Building>,
        override val mapData: MapData,
        override val selectedBuilding: Building,
        override val floor: Int,
        override val departureMarker: Marker? = null,
        override val destinationMarker: Marker? = null,
        val selectedMarker: Marker
    ) : MapState(
        buildings = buildings,
        mapData = mapData,
        selectedBuilding = selectedBuilding,
        floor = floor,
        departureMarker = departureMarker,
        destinationMarker = destinationMarker
    )

    data class RoomPicking(
        override val buildings: List<Building>,
        override val mapData: MapData,
        override val selectedBuilding: Building,
        override val floor: Int,
        override val departureMarker: Marker? = null,
        override val destinationMarker: Marker? = null,
    ) : MapState(
        buildings = buildings,
        mapData = mapData,
        selectedBuilding = selectedBuilding,
        floor = floor,
        departureMarker = departureMarker,
        destinationMarker = destinationMarker
    )
}
