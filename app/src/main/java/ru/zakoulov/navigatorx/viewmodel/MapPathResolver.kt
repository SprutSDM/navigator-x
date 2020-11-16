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
    ): PathInfo {
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
        val path = mutableListOf(finishDot)
        while (path.last() in prevs) {
            path.add(prevs[path.last()]!!)
        }
        return PathInfo(
            path = path.reversed(),
            virtualDist = dists[finishDot]!!
        )
    }

    class PathInfo(
        val path: List<String>,
        val virtualDist: Float
    )
}
