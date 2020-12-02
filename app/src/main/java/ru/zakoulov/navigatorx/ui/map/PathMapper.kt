package ru.zakoulov.navigatorx.ui.map

import com.otaliastudios.zoom.MapWithPathView
import ru.zakoulov.navigatorx.viewmodel.Path

class PathMapper {
    fun mapPathInfo(path: Path): List<MapWithPathView.PathPoint> {
        return path.path.map {
            MapWithPathView.PathPoint(
                positionX = it.positionX,
                positionY = it.positionY
            )
        }
    }
}
