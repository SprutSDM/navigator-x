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
import ru.zakoulov.navigatorx.data.SharedPreferencesManager
import ru.zakoulov.navigatorx.data.realm.RealmRepository
import ru.zakoulov.navigatorx.ui.map.markers.MarkerData

class MainViewModel(
    private val realmRepository: RealmRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {

    val storedSelectedBuilding = sharedPreferencesManager.loadSelectedBuilding()

    init {
        observeMapData()
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(
        State.Map(
            mapState = MapState.Viewing,
            markers = realmRepository.mapData.value.markers
                .filter {
                    it.floor == 1 && it.building == storedSelectedBuilding
                }
                .map { MarkerData(marker = it) },
            selectedBuilding = storedSelectedBuilding,
            floor = 1,
            departureMarker = null,
            destinationMarker = null,
            pathInfo = null,
            floorPaths = null
        )
    )
    val state: StateFlow<State> = _state

    private val _events: MutableSharedFlow<Event> = MutableSharedFlow(replay = 1, extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events

    init {
        focusOnMainEntrance()
    }

    private val mapPathResolver = MapPathResolver()

    private fun observeMapData() {
        viewModelScope.launch {
            realmRepository.mapData.collect { mapData ->
                _state.value = when (val currentState = state.value) {
                    is State.Loading -> {
                        State.Map(
                            mapState = MapState.Viewing,
                            markers = mapData.markers
                                .filter {
                                    it.floor == 1 && it.building == storedSelectedBuilding
                                }
                                .map { MarkerData(marker = it) },
                            selectedBuilding = storedSelectedBuilding,
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
                                .filter { it.floor == currentState.floor && it.building == currentState.selectedBuilding }
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
                    sharedPreferencesManager.saveSelectedBuilding(selectedBuilding)
                    _state.value = currentState.copy(
                        mapState = MapState.Viewing,
                        markers = realmRepository.mapData.value.markers
                            .filter { it.building == selectedBuilding && it.floor == 1 }
                            .map { MarkerData(marker = it) },
                        selectedBuilding = selectedBuilding,
                        floor = 1,
                        departureMarker = null,
                        destinationMarker = null,
                        floorPaths = null,
                        pathInfo = null
                    )
                    focusOnMainEntrance()
                }
            }
        }
    }

    private fun focusOnMainEntrance() {
        val currentState = state.value
        if (currentState is State.Map) {
            currentState.markers.find {
                it.marker.building == currentState.selectedBuilding &&
                        it.marker.floor == currentState.floor &&
                        it.marker is Marker.Entrance && it.marker.type == Marker.Entrance.Type.MAIN
            }?.let {
                _events.tryEmit(Event.FocusOn(it.marker))
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
        val trimmedMatchText = matchText.trim()
        fun isRoomMatches(roomMarker: Marker.Room): Boolean {
            return trimmedMatchText.isEmpty() || roomMarker.roomNumber.contains(trimmedMatchText, ignoreCase = true) ||
                    roomMarker.roomInfo.realUsage?.contains(trimmedMatchText, ignoreCase = true) == true
        }

        fun isEntranceMatches(entranceMarker: Marker.Entrance): Boolean {
            return trimmedMatchText.isEmpty() || entranceMarker.labelText.contains(trimmedMatchText, ignoreCase = true)
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

    private fun findPathAndUpdateState(
        currentState: State.Map,
        departureMarker: Marker?,
        destinationMarker: Marker?
    ) {
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
        val floor = departureMarker?.floor ?: currentState.floor
        _state.value = currentState.copy(
            mapState = MapState.Viewing,
            markers = realmRepository.mapData.value.markers
                .filter {
                    it.building == currentState.selectedBuilding && it.floor == floor
                }.map {
                    MarkerData(
                        marker = it,
                        isSelected = false,
                        additionalText = getBreakTypeString(pathInfo?.pathBreakTypes?.get(it.id)),
                        forceVisible = it.id == departureMarker?.id || it.id == destinationMarker?.id
                    )
                },
            departureMarker = departureMarker,
            destinationMarker = destinationMarker,
            pathInfo = pathInfo,
            floor = floor,
            floorPaths = pathInfo?.floorPaths?.get(floor)
        )
        departureMarker?.let {
            _events.tryEmit(Event.FocusOn(it))
        }
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
            _state.value = currentState.copy(
                mapState = MapState.Viewing,
                markers = currentState.markers.map {
                    it.copy(isSelected = false)
                }
            )
        }
    }

    fun onRoomPickerBSClosed() {
        val currentState = state.value
        if (currentState is State.Map && currentState.mapState !is MapState.Viewing) {
            _state.value = currentState.copy(
                mapState = MapState.Viewing,
                markers = currentState.markers.map {
                    it.copy(isSelected = false)
                }
            )
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

    /**
     * @return true if back press was consumed
     */
    fun onBackPressed(): Boolean {
        Log.d(TAG, "onBackPressed: ${state.value}")
        return when (val currentState = state.value) {
            is State.Loading -> false
            is State.Map -> when (currentState.mapState) {
                is MapState.RoomPicking, is MapState.MarkerSelected -> {
                    _state.value = currentState.copy(mapState = MapState.Viewing)
                    true
                }
                is MapState.Viewing -> false
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
