package ru.zakoulov.navigatorx.viewmodel

sealed class Event {
    object NavigateBack : Event()
    object NoPathFound : Event()
}
