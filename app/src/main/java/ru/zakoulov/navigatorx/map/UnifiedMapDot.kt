package ru.zakoulov.navigatorx.map

open class UnifiedMapDot(
    id: Int,
    val parentId: Int,
    x: Int,
    y: Int,
    val depthRate: Float
) : MapDot(x = x, y = y, id = id)
