package ru.zakoulov.navigatorx.viewmodel

import ru.zakoulov.navigatorx.data.Building
import ru.zakoulov.navigatorx.data.MapData

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
) {
    data class Viewing(
        override val buildings: List<Building>,
        override val mapData: MapData,
        override val selectedBuilding: Building
    ) : MapState(buildings, mapData, selectedBuilding)

    data class RoomSelected(
        override val buildings: List<Building>,
        override val mapData: MapData,
        override val selectedBuilding: Building,
        val roomNumber: String
    ) : MapState(buildings, mapData, selectedBuilding)

    data class RoomPicking(
        override val buildings: List<Building>,
        override val mapData: MapData,
        override val selectedBuilding: Building
    ) : MapState(buildings, mapData, selectedBuilding)
}
