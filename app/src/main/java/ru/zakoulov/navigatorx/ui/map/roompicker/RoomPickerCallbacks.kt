package ru.zakoulov.navigatorx.ui.map.roompicker

import ru.zakoulov.navigatorx.data.Marker

interface RoomPickerCallbacks {
    fun onRoomPicked(marker: Marker)
}
