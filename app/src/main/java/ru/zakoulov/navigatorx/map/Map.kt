package ru.zakoulov.navigatorx.map

import android.util.Log
import ru.zakoulov.navigatorx.data.Building
import ru.zakoulov.navigatorx.data.Marker

class Map(rawMarkers: List<RawMarkerData>) {
    private val mapUnifier = MapUnifier()
    private val unifiedDots: List<UnifiedMapDot> = mapUnifier.unifyDots(rawMarkers)
    init {
        unifiedDots.forEach {
            Log.d(TAG, "unifiedDot VR: ${it.depthRate}")
        }
    }
//    val markers = unifiedDots.map { unifiedMapDot ->
//        Marker.Room(
//            positionX = unifiedMapDot.x.toFloat(),
//            positionY = unifiedMapDot.y.toFloat(),
//            building = Building(id = 0, title = "123", address = "qq"),
//            scaleVisible = unifiedMapDot.depthRate,
//            corpus = 0,
//            floor = 0,
//            roomNumber = rawMarkers[unifiedMapDot.id].label,
//            roomTitle = "Аудитория ${rawMarkers[unifiedMapDot.id].label}"
//        )
//    }

    companion object {
        private const val TAG = "Map"
    }
}
