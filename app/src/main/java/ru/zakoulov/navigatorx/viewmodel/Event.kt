package ru.zakoulov.navigatorx.viewmodel

import ru.zakoulov.navigatorx.data.Marker

sealed class Event {
    object NoPathFound : Event()
    class FocusOn(val marker: Marker) : Event()
}
