package ru.zakoulov.navigatorx.map

import android.util.Log

class Map(rawMarkers: List<RawMarkerData>) {
    private val mapUnifier = MapUnifier()
    private val unifiedDots: List<UnifiedMapDot> = mapUnifier.unifyDots(rawMarkers)
    init {
        unifiedDots.forEach {
            Log.d(TAG, "unifiedDot VR: ${it.depthRate}")
        }
    }
    val markers = unifiedDots.map { unifiedMapDot ->
        Marker(
            id = unifiedMapDot.id,
            parentId = unifiedMapDot.parentId,
            x = unifiedMapDot.x,
            y = unifiedMapDot.y,
            visibilityRate = unifiedMapDot.depthRate,
            label = rawMarkers[unifiedMapDot.id].label
        )
    }

    companion object {
        private const val TAG = "Map"
    }
}
