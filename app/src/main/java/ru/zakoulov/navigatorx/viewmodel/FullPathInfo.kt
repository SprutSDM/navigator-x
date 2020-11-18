package ru.zakoulov.navigatorx.viewmodel

class FullPathInfo(
    val floorPaths: Map<Int, FloorPaths>,
    val virtualDist: Float
)

class FloorPaths(
    val paths: List<Path>
)

class Path(
    val path: List<Pair<Float, Float>>
)

