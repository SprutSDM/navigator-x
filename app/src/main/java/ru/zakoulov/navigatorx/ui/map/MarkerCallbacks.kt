package ru.zakoulov.navigatorx.ui.map

import ru.zakoulov.navigatorx.map.Marker

interface MarkerCallbacks {
    fun onMarkerSelected(marker: Marker)
}
