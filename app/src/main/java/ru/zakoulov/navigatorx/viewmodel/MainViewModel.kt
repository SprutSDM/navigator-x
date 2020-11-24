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
import ru.zakoulov.navigatorx.data.MapData
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
            buildings = buildings,
            markers = realmRepository.mapData.value.markers
                .filter { it.floor == 1 }
                .map { MarkerData(marker = it) },
            selectedBuilding = buildings[0],
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
                                buildings = buildings,
                                markers = realmRepository.mapData.value.markers
                                    .filter { it.floor == 1 }
                                    .map { MarkerData(marker = it) },
                                selectedBuilding = buildings[0],
                                floor = 1,
                                pathInfo = null
                            )
                        )
                    }
                    is State.Map -> {
                        currentState.copy(mapState = MapState.Viewing(
                            buildings = currentState.mapState.buildings,
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

    fun selectBuilding(building: Building) {
        when (val currentState = state.value) {
            is State.Map -> {
                if (building != currentState.mapState.selectedBuilding) {
                    _state.value = currentState.copy(mapState = MapState.Viewing(
                        buildings = currentState.mapState.buildings,
                        markers = realmRepository.mapData.value.markers
                            .filter { it.floor == 1 }
                            .map { MarkerData(marker = it) },
                        selectedBuilding = building,
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
            val pathInfo = currentState.mapState.destinationMarker?.let { destinationMarker ->
                val mapData = realmRepository.mapData.value
                mapPathResolver.findPath(
                    pathDots = mapData.pathDots,
                    pathConnections = mapData.pathConnections,
                    startDot = currentState.mapState.selectedMarker.id,
                    finishDot = destinationMarker.id
                )
            }
            transformToViewingState(currentState, departureMarker = currentState.mapState.selectedMarker,
                pathInfo = pathInfo)
        }
    }

    fun onRoomSelectedAsDestination() {
        val currentState = state.value
        if (currentState is State.Map && currentState.mapState is MapState.MarkerSelected) {
            val pathInfo = currentState.mapState.departureMarker?.let { departureMarker ->
                val mapData = realmRepository.mapData.value
                mapPathResolver.findPath(
                    pathDots = mapData.pathDots,
                    pathConnections = mapData.pathConnections,
                    startDot = departureMarker.id,
                    finishDot = currentState.mapState.selectedMarker.id
                )
            }
            transformToViewingState(currentState, destinationMarker = currentState.mapState.selectedMarker,
                pathInfo = pathInfo)
        }
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
                buildings = currentState.mapState.buildings,
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
                        .filter { it.floor == currentState.mapState.floor + 1 }
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
                        .filter { it.floor == currentState.mapState.floor - 1 }
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
            buildings = currentState.mapState.buildings,
            markers = markers ?: currentState.mapState.markers,
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
            buildings = currentState.mapState.buildings,
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
        private val buildings = listOf(
            Building(id = 0, title = "Главный корпус", address = "Кронверкский проспект, д. 49", floors = 5),
            Building(id = 1, title = "Корпус на Ломоносово", address = "Улица Ломоносова, д. 9", floors = 5),
            Building(id = 2, title = "Корпус на Гривцова", address = "Переулок Гривцова, д. 14", floors = 5),
            Building(id = 3, title = "Корпус на Чайковского", address = "Улица Чайковского, д. 11/2", floors = 5),
            Building(id = 4, title = "Корпус на Биржевой линии", address = "Биржевая линия, д. 14", floors = 5)
        )

        private const val TAG = "MainViewModel"
    }
}
