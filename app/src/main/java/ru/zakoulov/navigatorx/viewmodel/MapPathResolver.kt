package ru.zakoulov.navigatorx.viewmodel

import ru.zakoulov.navigatorx.data.PathDot
import java.util.PriorityQueue
import kotlin.math.pow

class MapPathResolver(
    private val stairsDist: Float = 50f
) {
    fun findPath(
        pathDots: Map<String, PathDot>,
        pathConnections: Map<String, List<String>>,
        startDot: String,
        finishDot: String
    ): FullPathInfo? {
        fun getDist(dotId1: String, dotId2: String): Float{
            val pathDot1 = pathDots[dotId1] ?: return Float.POSITIVE_INFINITY
            val pathDot2 = pathDots[dotId2] ?: return Float.POSITIVE_INFINITY
            val floorDist = if (pathDot1.floor != pathDot2.floor) stairsDist else 0f
            return ((pathDot1.positionX - pathDot2.positionX).pow(2) +
                    (pathDot1.positionY - pathDot2.positionY).pow(2)).pow(0.5f) + floorDist
        }

        val dots = PriorityQueue<Pair<Float, String>>(128, compareBy { it.first })
        val dists = mutableMapOf<String, Float>()
        val prevs = mutableMapOf<String, String>()
        dots.add(0f to startDot)
        dists[startDot] = 0f
        while (dots.isNotEmpty()) {
            val dot = dots.poll()!!
            val currentDist = dot.first
            val dotId = dot.second
            if (dotId == finishDot) {
                break
            }
            if (currentDist > dists[dotId] ?: Float.POSITIVE_INFINITY) {
                continue
            }
            pathConnections[dotId]?.forEach { toDotId ->
                val relaxedDist = dists[toDotId] ?: Float.POSITIVE_INFINITY
                val dist = getDist(dotId, toDotId)
                if (currentDist + dist < relaxedDist) {
                    dists[toDotId] = currentDist + dist
                    prevs[toDotId] = dotId
                    dots.add(currentDist + dist to toDotId)
                }
            }
        }
        val path = mutableListOf(pathDots[finishDot] ?: return null)
        while (path.last().id in prevs) {
            path.add(pathDots[prevs[path.last().id]] ?: return null)
        }
        val floorPaths = splitPathByFloors(path.reversed())
        val pathBreakTypes = mutableMapOf<String, Path.BreakType>()
        floorPaths.forEach { (_, paths) ->
            paths.forEach {
                pathBreakTypes[it.path.last().id] = it.breakType
            }
        }
        return FullPathInfo(
            floorPaths = floorPaths,
            virtualDist = dists[finishDot] ?: return null,
            pathBreakTypes = pathBreakTypes
        )
    }

    private fun splitPathByFloors(path: List<PathDot>): Map<Int, List<Path>> {
        val floorPaths = mutableMapOf<Int, MutableList<Path>>()
        var currentPath = mutableListOf(path[0])
        for (i in 1 until path.size) {
            val dot = path[i]
            if (dot.floor == currentPath[0].floor) {
                currentPath.add(dot)
            } else {
                if (currentPath[0].floor !in floorPaths) {
                    floorPaths[currentPath[0].floor] = mutableListOf()
                }
                floorPaths[currentPath[0].floor]!!.add(
                    Path(
                        path = currentPath,
                        breakType = if (dot.floor > currentPath[0].floor) {
                            Path.BreakType.FLOOR_UP
                        } else {
                            Path.BreakType.FLOOR_DOWN
                        }
                    )
                )
                currentPath = mutableListOf(dot)
            }
        }
        if (currentPath[0].floor !in floorPaths) {
            floorPaths[currentPath[0].floor] = mutableListOf()
        }
        floorPaths[currentPath[0].floor]!!.add(
            Path(
                path = currentPath,
                breakType = Path.BreakType.DESTINATION
            )
        )
        return floorPaths
    }
}
