package ru.zakoulov.navigatorx.ui.map

import ru.zakoulov.navigatorx.data.Marker

data class MarkerData(
    val marker: Marker,
    val isSelected: Boolean = false,
    val additionalText: String? = null
)
