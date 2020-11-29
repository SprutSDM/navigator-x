package ru.zakoulov.navigatorx.ui.map.markers

import ru.zakoulov.navigatorx.data.Marker

interface MarkerCallbacks {
    fun onMarkerSelected(marker: Marker)
}
