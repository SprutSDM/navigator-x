package ru.zakoulov.navigatorx.viewmodel

import ru.zakoulov.navigatorx.data.Building
import ru.zakoulov.navigatorx.data.Marker
import ru.zakoulov.navigatorx.ui.map.markers.MarkerData

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
        val floorPaths: List<Path>?
    ) : State()
}

sealed class MapState {
    object Viewing : MapState()

    data class MarkerSelected(
        val selectedMarker: Marker
    ) : MapState()

    data class RoomPicking(
        val pickType: PickType,
        val filteredRooms: List<Marker>
    ) : MapState() {

        enum class PickType {
            PICK_DEPARTURE,
            PICK_DESTINATION
        }
    }
}
