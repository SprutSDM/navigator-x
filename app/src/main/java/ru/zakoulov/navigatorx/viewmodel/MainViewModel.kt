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
import ru.zakoulov.navigatorx.ui.map.MarkerData

class MainViewModel(
    private val realmRepository: RealmRepository
) : ViewModel() {

    init {
        observeMapData()
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(
        State.Map(
        mapState = MapState.Viewing(
            markers = realmRepository.mapData.value.markers
                .filter { it.floor == 1 }
                .map { MarkerData(marker = it) },
            selectedBuilding = Building.MAIN_CORPUS,
            floor = 1,
            pathInfo = null
        )
    ))
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
                            mapState = MapState.Viewing(
                                markers = realmRepository.mapData.value.markers
                                    .filter { it.floor == 1 }
                                    .map { MarkerData(marker = it) },
                                selectedBuilding = Building.MAIN_CORPUS,
                                floor = 1,
                                pathInfo = null
                            )
                        )
                    }
                    is State.Map -> {
                        currentState.copy(mapState = MapState.Viewing(
                            markers = realmRepository.mapData.value.markers
                                .filter { it.floor == currentState.mapState.floor }
                                .map { MarkerData(marker = it) },
                            selectedBuilding = currentState.mapState.selectedBuilding,
                            floor = currentState.mapState.floor,
                            destinationMarker = currentState.mapState.destinationMarker,
                            departureMarker = currentState.mapState.departureMarker,
                            pathInfo = currentState.mapState.pathInfo
                        ))
                    }
                }
            }
        }
    }

    fun onBuildingSelected(selectedBuilding: Building) {
        when (val currentState = state.value) {
            is State.Map -> {
                if (selectedBuilding != currentState.mapState.selectedBuilding) {
                    _state.value = currentState.copy(mapState = MapState.Viewing(
                        markers = realmRepository.mapData.value.markers
                            .filter { it.building == selectedBuilding && it.floor == 1 }
                            .map { MarkerData(marker = it) },
                        selectedBuilding = selectedBuilding,
                        floor = 1,
                    ))
                }
            }
        }
    }

    fun pickDepartureRoom() {
        val currentState = state.value
        if (currentState is State.Map) {
            transformToRoomPickState(currentState)
        }
    }

    fun pickDestinationRoom() {
        val currentState = state.value
        if (currentState is State.Map) {
            transformToRoomPickState(currentState)
        }
    }

    fun onRoomSelectedAsDeparture() {
        val currentState = state.value
        if (currentState is State.Map && currentState.mapState is MapState.MarkerSelected) {
            findPathAndUpdateState(
                currentState = currentState,
                departureMarker = currentState.mapState.selectedMarker,
                destinationMarker = currentState.mapState.destinationMarker
            )
        }
    }

    fun onRoomSelectedAsDestination() {
        val currentState = state.value
        if (currentState is State.Map && currentState.mapState is MapState.MarkerSelected) {
            findPathAndUpdateState(
                currentState = currentState,
                departureMarker = currentState.mapState.departureMarker,
                destinationMarker = currentState.mapState.selectedMarker,
            )
        }
    }

    private fun findPathAndUpdateState(currentState: State.Map, departureMarker: Marker?, destinationMarker: Marker?) {
        val pathInfo = if (departureMarker != null && destinationMarker != null) {
            // If departure or destination marker was reselected, we don't have to find and animate the same path
            if (currentState.mapState.departureMarker?.id == departureMarker.id &&
                currentState.mapState.destinationMarker?.id == destinationMarker.id) {
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
        _state.value = currentState.copy(mapState = MapState.Viewing(
            selectedBuilding = currentState.mapState.selectedBuilding,
            floor = currentState.mapState.floor,
            markers = currentState.mapState.markers.map {
                if (it.isSelected) it.copy(isSelected = false) else it
            },
            departureMarker = departureMarker,
            destinationMarker = destinationMarker,
            pathInfo = pathInfo,
            floorPaths = pathInfo?.floorPaths?.get(currentState.mapState.floor)
        ))
    }

    fun onRoomInfoBSClosed() {
        val currentState = state.value
        if (currentState is State.Map && currentState.mapState !is MapState.Viewing) {
            transformToViewingState(currentState)
        }
    }

    fun onRoomPickerBSClosed() {
        val currentState = state.value
        if (currentState is State.Map && currentState.mapState !is MapState.Viewing) {
            transformToViewingState(currentState)
        }
    }

    fun onMarkerSelected(marker: Marker) {
        val currentState = state.value
        if (currentState is State.Map) {
            _state.value = currentState.copy(mapState = MapState.MarkerSelected(
                markers = currentState.mapState.markers.map {
                    when {
                        it.marker.id == marker.id -> it.copy(isSelected = true)
                        it.isSelected -> it.copy(isSelected = false)
                        else -> it
                    }
                },
                selectedBuilding = currentState.mapState.selectedBuilding,
                floor = currentState.mapState.floor,
                selectedMarker = marker,
                departureMarker = currentState.mapState.departureMarker,
                destinationMarker = currentState.mapState.destinationMarker,
                pathInfo = currentState.mapState.pathInfo,
                floorPaths = currentState.mapState.pathInfo?.floorPaths?.get(currentState.mapState.floor)
            ))
        }
    }

    fun onOutsideClick() {
        val currentState = state.value
        if (currentState is State.Map) {
            transformToViewingState(currentState)
        }
    }

    fun onBackPressed() {
        Log.d(TAG, "onBackPressed: ${state.value}")
        when (val currentState = state.value) {
            is State.Loading -> _events.tryEmit(Event.NavigateBack)
            is State.Map -> when (currentState.mapState) {
                is MapState.RoomPicking -> transformToViewingState(currentState)
                is MapState.MarkerSelected -> transformToViewingState(currentState)
                is MapState.Viewing -> _events.tryEmit(Event.NavigateBack)
            }
        }
    }

    fun onUpFloorSelected() {
        val currentState = state.value
        if (currentState is State.Map) {
            val currentFloor = currentState.mapState.floor
            if (currentFloor != currentState.mapState.selectedBuilding.floors) {
                transformToViewingState(
                    currentState = currentState,
                    markers = realmRepository.mapData.value.markers
                        .filter {
                            it.building == currentState.mapState.selectedBuilding &&
                                    it.floor == currentState.mapState.floor + 1
                        }
                        .map { MarkerData(marker = it) },
                    floor = currentState.mapState.floor + 1
                )
            }
        }
    }

    fun onDownFloorSelected() {
        val currentState = state.value
        if (currentState is State.Map) {
            val currentFloor = currentState.mapState.floor
            if (currentFloor != 1) {
                transformToViewingState(
                    currentState = currentState,
                    markers = realmRepository.mapData.value.markers
                        .filter {
                            it.building == currentState.mapState.selectedBuilding &&
                                    it.floor == currentState.mapState.floor - 1
                        }
                        .map { MarkerData(marker = it) },
                    floor = currentState.mapState.floor - 1
                )
            }
        }
    }

    private fun transformToViewingState(
        currentState: State.Map,
        markers: List<MarkerData>? = null,
        selectedBuilding: Building? = null,
        floor: Int? = null,
        departureMarker: Marker? = null,
        destinationMarker: Marker? = null,
        pathInfo: FullPathInfo? = null
    ) {
        _state.value = currentState.copy(mapState = MapState.Viewing(
            markers = (markers ?: currentState.mapState.markers).map {
                if (it.isSelected) it.copy(isSelected = false) else it
            },
            selectedBuilding = selectedBuilding ?: currentState.mapState.selectedBuilding,
            floor = floor ?: currentState.mapState.floor,
            departureMarker = departureMarker ?: currentState.mapState.departureMarker,
            destinationMarker = destinationMarker ?: currentState.mapState.destinationMarker,
            pathInfo = pathInfo ?: currentState.mapState.pathInfo,
            floorPaths = (pathInfo ?: currentState.mapState.pathInfo)?.floorPaths?.get(floor ?: currentState.mapState.floor)
        ))
    }

    private fun transformToRoomPickState(
        currentState: State.Map
    ) {
        _state.value = currentState.copy(mapState = MapState.RoomPicking(
            markers = currentState.mapState.markers,
            selectedBuilding = currentState.mapState.selectedBuilding,
            floor = currentState.mapState.floor,
            departureMarker = currentState.mapState.departureMarker,
            destinationMarker = currentState.mapState.destinationMarker,
            pathInfo = currentState.mapState.pathInfo,
            floorPaths = currentState.mapState.pathInfo?.floorPaths?.get(currentState.mapState.floor)
        ))
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}
