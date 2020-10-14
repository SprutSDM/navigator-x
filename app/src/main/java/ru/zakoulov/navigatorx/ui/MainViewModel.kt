package ru.zakoulov.navigatorx.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.zakoulov.navigatorx.data.Building
import ru.zakoulov.navigatorx.state.MapState
import ru.zakoulov.navigatorx.state.State

class MainViewModel : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Map(
        mapState = MapState.Viewing(buildings = buildings, selectedBuilding = buildings[0])
    ))
    val state: StateFlow<State> = _state

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

    companion object {
        private val buildings = listOf(
            Building(id = 0, title = "Главный корпус", address = "Кронверкский проспект, д. 49"),
            Building(id = 1, title = "Корпус на Ломоносово", address = "Улица Ломоносова, д. 9"),
            Building(id = 2, title = "Корпус на Гривцова", address = "Переулок Гривцова, д. 14"),
            Building(id = 3, title = "Корпус на Чайковского", address = "Улица Чайковского, д. 11/2"),
            Building(id = 4, title = "Корпус на Биржевой линии", address = "Биржевая линия, д. 14")
        )
    }
}
