package ru.zakoulov.navigatorx.map

import kotlin.math.pow

class MapUnifier {
    fun unifyDots(dots: List<MapDot>): List<UnifiedMapDot> {
        val unifiedDots = unifyDotsForArea(dots = dots, depth = 0, parentId = -1)
        val maxDepth = unifiedDots.maxOf { it.depth }
        return unifiedDots.map {
            UnifiedMapDot(
                id = it.id,
                parentId = it.parentId,
                x = it.x,
                y = it.y,
                depthRate = it.depth / maxDepth.toFloat()
            )
        }
    }

    private fun unifyDotsForArea(dots: List<MapDot>, depth: Int, parentId: Int): List<UnifyDotData> {
        if (dots.isEmpty()) {
            return emptyList()
        }
        val centerOfGravity = findCenterOfGravity(dots)
        val nearestDot = findNearestDot(dots, centerOfGravity)

        val leftUpArea = mutableListOf<MapDot>()
        val rightUpArea = mutableListOf<MapDot>()
        val leftDownArea = mutableListOf<MapDot>()
        val rightDownArea = mutableListOf<MapDot>()
        dots.forEach { dot ->
            when {
                dot.x < nearestDot.x && dot.y < nearestDot.y -> leftUpArea.add(dot)
                dot.x > nearestDot.x && dot.y < nearestDot.y -> rightUpArea.add(dot)
                dot.x < nearestDot.x && dot.y > nearestDot.y -> leftDownArea.add(dot)
                dot.x > nearestDot.x && dot.y > nearestDot.y -> rightDownArea.add(dot)
            }
        }
        return unifyDotsForArea(
            dots = leftUpArea,
            depth = depth + 1,
            parentId = nearestDot.id
        ) + unifyDotsForArea(
            dots = rightUpArea,
            depth = depth + 1,
            parentId = nearestDot.id
        ) + unifyDotsForArea(
            dots = leftDownArea,
            depth = depth + 1,
            parentId = nearestDot.id
        ) + unifyDotsForArea(
            dots = rightDownArea,
            depth = depth + 1,
            parentId = nearestDot.id
        ) + UnifyDotData(
            id = nearestDot.id,
            parentId = parentId,
            x = nearestDot.x,
            y = nearestDot.y,
            depth = depth
        )
    }

    private fun findCenterOfGravity(dots: List<MapDot>): Pair<Float, Float> {
        return Pair(
            first = dots.sumBy { it.x } / dots.size.toFloat(),
            second = dots.sumBy { it.y } / dots.size.toFloat()
        )
    }

    private fun findNearestDot(dots: List<MapDot>, point: Pair<Float, Float>): MapDot {
        var nearestDot = dots[0]
        var minDist = getEuclidDist(nearestDot, point)
        dots.forEach { marker ->
            val dist = getEuclidDist(marker, point)
            if (dist < minDist) {
                minDist = dist
                nearestDot = marker
            }
        }
        return nearestDot
    }

    private fun getEuclidDist(dot: MapDot, point: Pair<Float, Float>): Float {
        return ((dot.x - point.first).pow(2) + (dot.y - point.second).pow(2)).pow(0.5f)
    }

    private class UnifyDotData(
        id: Int,
        val parentId: Int,
        x: Int,
        y: Int,
        val depth: Int
    ) : MapDot(id = id, x = x, y = y)
}
