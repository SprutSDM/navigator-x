package ru.zakoulov.navigatorx.data.realm

import ru.zakoulov.navigatorx.data.Building
import ru.zakoulov.navigatorx.data.MapData
import ru.zakoulov.navigatorx.data.Marker
import ru.zakoulov.navigatorx.data.PathDot

class RealmMapper {
    fun map(realmPoints: List<MapPointModel>): MapData {
        val markers = mutableListOf<Marker>()
        val pathDots = mutableMapOf<String, PathDot>()
        val pathConnections = mutableMapOf<String, MutableList<String>>()
        realmPoints.forEach {
            val id = it._id.toString()
            pathDots[id] = PathDot(
                id = id,
                positionX = it.positionX.toFloat(),
                positionY = it.positionY.toFloat(),
                floor = it.floor
            )
            pathConnections[id] = it.connectedIDs
            when (it.typeEnum) {
                PointTypeEnum.PATH -> Unit //TODO
                PointTypeEnum.ROOM -> {
                    markers.add(Marker.Room(
                        id = it._id.toString(),
                        scaleVisible = it.scaleVisible.toFloat(),
                        positionX = it.positionX.toFloat(),
                        positionY = it.positionY.toFloat(),
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
                        id = it._id.toString(),
                        scaleVisible = it.scaleVisible.toFloat(),
                        positionX = it.positionX.toFloat(),
                        positionY = it.positionY.toFloat(),
                        corpus = it.korpus,
                        building = Building(id = it.building, title = "", address = "", floors = 6),
                        floor = it.floor,
                        isUp = it.typeEnum == PointTypeEnum.STAIRS_UP
                    ))
                }
                PointTypeEnum.ELEVATOR -> Unit //TODO
            }
        }
        return MapData(markers, pathDots, pathConnections)
    }
}
