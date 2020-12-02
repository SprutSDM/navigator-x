package ru.zakoulov.navigatorx.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.zakoulov.navigatorx.data.Building
import ru.zakoulov.navigatorx.data.Marker
import ru.zakoulov.navigatorx.data.realm.RealmRepository
import ru.zakoulov.navigatorx.ui.map.markers.MarkerData

class MainViewModel(
    private val realmRepository: RealmRepository
) : ViewModel() {

    init {
        observeMapData()
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(
        State.Map(
            mapState = MapState.Viewing,
            markers = realmRepository.mapData.value.markers
                .filter { it.floor == 1 }
                .map { MarkerData(marker = it) },
            selectedBuilding = Building.MAIN_CORPUS,
            floor = 1,
            departureMarker = null,
            destinationMarker = null,
            pathInfo = null,
            floorPaths = null
        )
    )
    val state: StateFlow<State> = _state

    private val _events: MutableSharedFlow<Event> = MutableSharedFlow(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events

    private val mapPathResolver = MapPathResolver()

    private fun observeMapData() {
        viewModelScope.launch {
            realmRepository.mapData.collect { mapData ->
                _state.value = when (val currentState = state.value) {
                    is State.Loading -> {
                        State.Map(
                            mapState = MapState.Viewing,
                            markers = mapData.markers
                                .filter { it.floor == 1 }
                                .map { MarkerData(marker = it) },
                            selectedBuilding = Building.MAIN_CORPUS,
                            floor = 1,
                            departureMarker = null,
                            destinationMarker = null,
                            pathInfo = null,
                            floorPaths = null
                        )
                    }
                    is State.Map -> {
                        currentState.copy(
                            mapState = MapState.Viewing,
                            markers = realmRepository.mapData.value.markers
                                .filter { it.floor == currentState.floor }
                                .map { MarkerData(marker = it) },
                        )
                    }
                }
            }
        }
    }

    fun onBuildingSelected(selectedBuilding: Building) {
        when (val currentState = state.value) {
            is State.Map -> {
                if (selectedBuilding != currentState.selectedBuilding) {
                    _state.value = currentState.copy(
                        mapState = MapState.Viewing,
                        markers = realmRepository.mapData.value.markers
                            .filter { it.building == selectedBuilding && it.floor == 1 }
                            .map { MarkerData(marker = it) },
                        selectedBuilding = selectedBuilding,
                        floor = 1,
                    )
                }
            }
        }
    }

    fun openDepartureRoomPicker() {
        val currentState = state.value
        if (currentState is State.Map) {
            _state.value = currentState.copy(
                mapState = MapState.RoomPicking(
                    pickType = MapState.RoomPicking.PickType.PICK_DEPARTURE,
                    filteredRooms = filterRooms("", currentState.selectedBuilding.id)
                )
            )
        }
    }

    fun openDestinationRoomPicker() {
        val currentState = state.value
        if (currentState is State.Map) {
            _state.value = currentState.copy(
                mapState = MapState.RoomPicking(
                    pickType = MapState.RoomPicking.PickType.PICK_DESTINATION,
                    filteredRooms = filterRooms("", currentState.selectedBuilding.id)
                )
            )
        }
    }

    fun onRoomPickerTextUpdated(matchText: String) {
        val currentState = state.value
        if (currentState is State.Map && currentState.mapState is MapState.RoomPicking) {
            _state.value = currentState.copy(
                mapState = currentState.mapState.copy(
                    filteredRooms = filterRooms(matchText, currentState.selectedBuilding.id)
                )
            )
        }
    }

    private fun filterRooms(matchText: String, buildingId: Int): List<Marker> {
        fun isRoomMatches(roomMarker: Marker.Room): Boolean {
            return matchText.isEmpty() || roomMarker.roomNumber.contains(matchText, ignoreCase = true)
        }

        fun isEntranceMatches(entranceMarker: Marker.Entrance): Boolean {
            return matchText.isEmpty() || entranceMarker.labelText.contains(matchText, ignoreCase = true)
        }

        return realmRepository.mapData.value.markers.filter {
            it.building.id == buildingId && (
                    it is Marker.Room && isRoomMatches(it) ||
                    it is Marker.Entrance && it.type == Marker.Entrance.Type.MAIN && isEntranceMatches(it))
        }
    }

    fun onRoomPickerSelected(marker: Marker) {
        val currentState = state.value
        if (currentState is State.Map && currentState.mapState is MapState.RoomPicking) {
            if (currentState.mapState.pickType == MapState.RoomPicking.PickType.PICK_DEPARTURE) {
                findPathAndUpdateState(
                    currentState = currentState,
                    destinationMarker = currentState.destinationMarker,
                    departureMarker = marker
                )
            } else {
                findPathAndUpdateState(
                    currentState = currentState,
                    destinationMarker = marker,
                    departureMarker = currentState.departureMarker
                )
            }
        }
    }

    fun onPickerCanceled() {
        val currentState = state.value
        if (currentState is State.Map && currentState.mapState is MapState.RoomPicking) {
            _state.value = if (currentState.mapState.pickType == MapState.RoomPicking.PickType.PICK_DESTINATION) {
                currentState.copy(
                    mapState = MapState.Viewing,
                    destinationMarker = null
                )
            } else {
                currentState.copy(
                    mapState = MapState.Viewing,
                    departureMarker = null
                )
            }
        }
    }

    fun onRoomSelectedAsDeparture() {
        val currentState = state.value
        if (currentState is State.Map && currentState.mapState is MapState.MarkerSelected) {
            findPathAndUpdateState(
                currentState = currentState,
                departureMarker = currentState.mapState.selectedMarker,
                destinationMarker = currentState.destinationMarker
            )
        }
    }

    fun onRoomSelectedAsDestination() {
        val currentState = state.value
        if (currentState is State.Map && currentState.mapState is MapState.MarkerSelected) {
            findPathAndUpdateState(
                currentState = currentState,
                departureMarker = currentState.departureMarker,
                destinationMarker = currentState.mapState.selectedMarker,
            )
        }
    }

    private fun findPathAndUpdateState(currentState: State.Map, departureMarker: Marker?, destinationMarker: Marker?) {
        val pathInfo = if (departureMarker != null && destinationMarker != null) {
            // If departure or destination marker was reselected, we don't have to find and animate the same path
            if (currentState.departureMarker?.id == departureMarker.id &&
                currentState.destinationMarker?.id == destinationMarker.id) {
                return
            }
            val mapData = realmRepository.mapData.value
            mapPathResolver.findPath(
                pathDots = mapData.pathDots,
                pathConnections = mapData.pathConnections,
                startDot = departureMarker.id,
                finishDot = destinationMarker.id
            ).also {
                if (it == null) {
                    _events.tryEmit(Event.NoPathFound)
                }
            }
        } else {
            null
        }
        _state.value = currentState.copy(
            mapState = MapState.Viewing,
            markers = currentState.markers.map {
                it.copy(
                    isSelected = false,
                    additionalText = getBreakTypeString(pathInfo?.pathBreakTypes?.get(it.marker.id)),
                    forceVisible = it.marker.id == departureMarker?.id || it.marker.id == destinationMarker?.id
                )
            },
            departureMarker = departureMarker,
            destinationMarker = destinationMarker,
            pathInfo = pathInfo,
            floorPaths = pathInfo?.floorPaths?.get(currentState.floor)
        )
    }

    private fun getBreakTypeString(breakType: Path.BreakType?): String? {
        return when (breakType) {
            Path.BreakType.FLOOR_DOWN -> "Вниз"
            Path.BreakType.FLOOR_UP -> "Вверх"
            else -> null
        }
    }

    fun onRoomInfoBSClosed() {
        val currentState = state.value
        if (currentState is State.Map && currentState.mapState !is MapState.Viewing) {
            _state.value = currentState.copy(mapState = MapState.Viewing)
        }
    }

    fun onRoomPickerBSClosed() {
        val currentState = state.value
        if (currentState is State.Map && currentState.mapState !is MapState.Viewing) {
            _state.value = currentState.copy(mapState = MapState.Viewing)
        }
    }

    fun onMarkerSelected(marker: Marker) {
        val currentState = state.value
        if (currentState is State.Map) {
            _state.value = currentState.copy(
                mapState = MapState.MarkerSelected(selectedMarker = marker),
                markers = currentState.markers.map {
                    when {
                        it.marker.id == marker.id -> it.copy(isSelected = true)
                        it.isSelected -> it.copy(isSelected = false)
                        else -> it
                    }
                },
            )
        }
    }

    fun onOutsideClick() {
        val currentState = state.value
        if (currentState is State.Map) {
            _state.value = currentState.copy(
                mapState = MapState.Viewing,
                markers = currentState.markers.map {
                    if (it.isSelected) {
                        it.copy(isSelected = false)
                    } else {
                        it
                    }
                }
            )
        }
    }

    fun onBackPressed() {
        Log.d(TAG, "onBackPressed: ${state.value}")
        when (val currentState = state.value) {
            is State.Loading -> _events.tryEmit(Event.NavigateBack)
            is State.Map -> when (currentState.mapState) {
                is MapState.RoomPicking, is MapState.MarkerSelected -> {
                    _state.value = currentState.copy(mapState = MapState.Viewing)
                }
                is MapState.Viewing -> _events.tryEmit(Event.NavigateBack)
            }
        }
    }

    fun onUpFloorSelected() {
        val currentState = state.value
        if (currentState is State.Map) {
            val currentFloor = currentState.floor
            if (currentFloor != currentState.selectedBuilding.floors) {
                _state.value = currentState.copy(
                    mapState = MapState.Viewing,
                    markers = realmRepository.mapData.value.markers
                        .filter {
                            it.building == currentState.selectedBuilding &&
                                    it.floor == currentState.floor + 1
                        }
                        .map {
                            MarkerData(
                                marker = it,
                                additionalText = getBreakTypeString(currentState.pathInfo?.pathBreakTypes?.get(it.id)),
                                forceVisible = it.id == currentState.departureMarker?.id ||
                                        it.id == currentState.destinationMarker?.id
                            )
                        },
                    floor = currentState.floor + 1,
                    floorPaths = currentState.pathInfo?.floorPaths?.get(currentState.floor + 1)
                )
            }
        }
    }

    fun onDownFloorSelected() {
        val currentState = state.value
        if (currentState is State.Map) {
            val currentFloor = currentState.floor
            if (currentFloor != 1) {
                _state.value = currentState.copy(
                    mapState = MapState.Viewing,
                    markers = realmRepository.mapData.value.markers
                        .filter {
                            it.building == currentState.selectedBuilding &&
                                    it.floor == currentState.floor - 1
                        }
                        .map {
                            MarkerData(
                                marker = it,
                                additionalText = getBreakTypeString(currentState.pathInfo?.pathBreakTypes?.get(it.id)),
                                forceVisible = it.id == currentState.departureMarker?.id ||
                                        it.id == currentState.destinationMarker?.id
                            )
                        },
                    floor = currentState.floor - 1,
                    floorPaths = currentState.pathInfo?.floorPaths?.get(currentState.floor - 1)
                )
            }
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}
