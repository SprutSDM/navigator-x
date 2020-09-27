package ru.zakoulov.navigatorx.map

class RawMarkerData(
    id: Int,
    x: Int,
    y: Int,
    val label: String
): MapDot(x = x, y = y, id = id)
