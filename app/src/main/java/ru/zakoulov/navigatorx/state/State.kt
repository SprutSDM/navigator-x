package ru.zakoulov.navigatorx.state

import ru.zakoulov.navigatorx.data.Building

sealed class State {
    class Loading() : State()

    data class Map(
        val mapState: MapState
    ) : State()
}

sealed class MapState(
    open val buildings: List<Building>,
    open val selectedBuilding: Building,
) {
    data class Viewing(
        override val buildings: List<Building>,
        override val selectedBuilding: Building
    ) : MapState(buildings, selectedBuilding)

    data class RoomSelected(
        override val buildings: List<Building>,
        override val selectedBuilding: Building,
        val roomNumber: String
    ) : MapState(buildings, selectedBuilding)

    data class RoomPicking(
        override val buildings: List<Building>,
        override val selectedBuilding: Building
    ) : MapState(buildings, selectedBuilding)
}
