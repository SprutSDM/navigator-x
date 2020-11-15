package ru.zakoulov.navigatorx.data.realm

import ru.zakoulov.navigatorx.data.Building
import ru.zakoulov.navigatorx.data.MapData
import ru.zakoulov.navigatorx.data.Marker
import ru.zakoulov.navigatorx.data.PathDot

class RealmMapper {
    fun map(realmPoints: List<MapPointModel>): MapData {
        val markers = mutableListOf<Marker>()
        val pathDots = emptyList<PathDot>()
        realmPoints.forEach {
            when (it.typeEnum) {
                PointTypeEnum.PATH -> Unit //TODO
                PointTypeEnum.ROOM -> {
                    markers.add(Marker.Room(
                        scaleVisible = it.scaleVisible.toFloat(),
                        positionX = it.positionX.toFloat() - 3500,
                        positionY = it.positionY.toFloat() - 3500,
                        corpus = it.korpus,
                        building = Building(id = it.building, title = "", address = "", floors = 6),
                        floor = it.floor,
                        roomNumber = it.labelText,
                        roomTitle = it.info
                    ))
                }
                PointTypeEnum.MESSAGE -> Unit //TODO
                PointTypeEnum.ICON -> Unit //TODO
                PointTypeEnum.OTHER -> Unit //TODO
                PointTypeEnum.STAIRS_UP, PointTypeEnum.STAIRS_DOWN -> {
                    markers.add(Marker.Stairs(
                        scaleVisible = it.scaleVisible.toFloat(),
                        positionX = it.positionX.toFloat() - 3500,
                        positionY = it.positionY.toFloat() - 3500,
                        corpus = it.korpus,
                        building = Building(id = it.building, title = "", address = "", floors = 6),
                        floor = it.floor,
                        isUp = it.typeEnum == PointTypeEnum.STAIRS_UP
                    ))
                }
                PointTypeEnum.ELEVATOR -> Unit //TODO
            }
        }
        return MapData(markers, pathDots)
    }
}
