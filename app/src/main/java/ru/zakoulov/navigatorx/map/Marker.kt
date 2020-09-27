package ru.zakoulov.navigatorx.map

class Marker(
    id: Int,
    parentId: Int,
    x: Int,
    y: Int,
    visibilityRate: Float,
    val label: String
) : UnifiedMapDot(id = id, parentId = parentId, x = x, y = y, depthRate = visibilityRate)
