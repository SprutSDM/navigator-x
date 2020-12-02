package ru.zakoulov.navigatorx.viewmodel

import ru.zakoulov.navigatorx.data.PathDot

class FullPathInfo(
    val floorPaths: Map<Int, List<Path>>,
    val virtualDist: Float,
    val pathBreakTypes: Map<String, Path.BreakType>
)

class Path(
    val path: List<PathDot>,
    val breakType: BreakType
) {
    enum class BreakType {
        DESTINATION,
        FLOOR_UP,
        FLOOR_DOWN
    }
}
