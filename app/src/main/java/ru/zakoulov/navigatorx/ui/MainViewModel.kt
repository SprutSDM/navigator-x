package ru.zakoulov.navigatorx.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import ru.zakoulov.navigatorx.data.Building
import ru.zakoulov.navigatorx.state.Event
import ru.zakoulov.navigatorx.state.MapState
import ru.zakoulov.navigatorx.state.State

class MainViewModel : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Map(
        mapState = MapState.Viewing(buildings = buildings, selectedBuilding = buildings[0])
    ))
    val state: StateFlow<State> = _state

    private val _events: MutableSharedFlow<Event> = MutableSharedFlow(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events

    fun selectBuilding(building: Building) {
        when (val currentState = state.value) {
            is State.Map -> {
                _state.value = currentState.copy(mapState = MapState.Viewing(
                    buildings = currentState.mapState.buildings,
                    selectedBuilding = building
                ))
            }
        }
    }

    fun pickHereRoom() {
        val currentState = state.value
        if (currentState is State.Map) {
            transformToRoomPickState(currentState)
        }
    }

    fun pickFromHereRoom() {
        val currentState = state.value
        if (currentState is State.Map) {
            transformToRoomPickState(currentState)
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

    fun onRoomSelected(roomNumber: String) {
        val currentState = state.value
        if (currentState is State.Map) {
            _state.value = currentState.copy(mapState = MapState.RoomSelected(
                buildings = currentState.mapState.buildings,
                selectedBuilding = currentState.mapState.selectedBuilding,
                roomNumber = roomNumber
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
                is MapState.RoomSelected -> transformToViewingState(currentState)
                is MapState.Viewing -> _events.tryEmit(Event.NavigateBack)
            }
        }
    }

    private fun transformToViewingState(currentState: State.Map) {
        _state.value = currentState.copy(mapState = MapState.Viewing(
            buildings = currentState.mapState.buildings,
            selectedBuilding = currentState.mapState.selectedBuilding
        ))
    }

    private fun transformToRoomPickState(currentState: State.Map) {
        _state.value
        _state.value = currentState.copy(mapState = MapState.RoomPicking(
            buildings = currentState.mapState.buildings,
            selectedBuilding = currentState.mapState.selectedBuilding
        ))
    }

    companion object {
        private val buildings = listOf(
            Building(id = 0, title = "Главный корпус", address = "Кронверкский проспект, д. 49"),
            Building(id = 1, title = "Корпус на Ломоносово", address = "Улица Ломоносова, д. 9"),
            Building(id = 2, title = "Корпус на Гривцова", address = "Переулок Гривцова, д. 14"),
            Building(id = 3, title = "Корпус на Чайковского", address = "Улица Чайковского, д. 11/2"),
            Building(id = 4, title = "Корпус на Биржевой линии", address = "Биржевая линия, д. 14")
        )

        private const val TAG = "MainViewModel"
    }
}
