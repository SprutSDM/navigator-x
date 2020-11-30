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
            val building = Building.values().find { building -> building.id == it.building }
            if (building != null) {
                when (it.typeEnum) {
                    PointTypeEnum.PATH -> Unit //TODO
                    PointTypeEnum.ROOM -> {
                        markers.add(
                            Marker.Room(
                                id = it._id.toString(),
                                scaleVisible = it.scaleVisible.toFloat(),
                                positionX = it.positionX.toFloat(),
                                positionY = it.positionY.toFloat(),
                                corpus = it.korpus,
                                building = building,
                                floor = it.floor,
                                roomNumber = it.labelText,
                                roomTitle = it.info
                            )
                        )
                    }
                    PointTypeEnum.MESSAGE -> Unit //TODO
                    PointTypeEnum.ICON -> Unit //TODO
                    PointTypeEnum.OTHER -> {
                        markers.add(
                            Marker.Room(
                                id = it._id.toString(),
                                scaleVisible = it.scaleVisible.toFloat(),
                                positionX = it.positionX.toFloat(),
                                positionY = it.positionY.toFloat(),
                                corpus = it.korpus,
                                building = building,
                                floor = it.floor,
                                roomNumber = it.labelText,
                                roomTitle = it.info
                            )
                        )
                    }
                    PointTypeEnum.STAIRS_UP, PointTypeEnum.STAIRS_DOWN -> {
                        markers.add(
                            Marker.Stairs(
                                id = it._id.toString(),
                                scaleVisible = it.scaleVisible.toFloat(),
                                positionX = it.positionX.toFloat(),
                                positionY = it.positionY.toFloat(),
                                corpus = it.korpus,
                                building = building,
                                floor = it.floor,
                                isUp = it.typeEnum == PointTypeEnum.STAIRS_UP
                            )
                        )
                    }
                    PointTypeEnum.ELEVATOR -> Unit //TODO
                    PointTypeEnum.ENTRANCE, PointTypeEnum.MAIN_ENTRANCE -> {
                        markers.add(
                            Marker.Entrance(
                                id = it._id.toString(),
                                scaleVisible = it.scaleVisible.toFloat(),
                                positionX = it.positionX.toFloat(),
                                positionY = it.positionY.toFloat(),
                                corpus = it.korpus,
                                building = building,
                                floor = it.floor,
                                type = if (it.typeEnum == PointTypeEnum.ENTRANCE) {
                                    Marker.Entrance.Type.COMMON
                                } else {
                                    Marker.Entrance.Type.MAIN
                                },
                                labelText = it.labelText
                            )
                        )
                    }
                    PointTypeEnum.TOILET, PointTypeEnum.TOILET_FEMALE, PointTypeEnum.TOILET_MALE -> {
                        markers.add(
                            Marker.Toilet(
                                id = it._id.toString(),
                                scaleVisible = it.scaleVisible.toFloat(),
                                positionX = it.positionX.toFloat(),
                                positionY = it.positionY.toFloat(),
                                corpus = it.korpus,
                                building = building,
                                floor = it.floor,
                                type = when (it.typeEnum) {
                                    PointTypeEnum.TOILET_MALE -> Marker.Toilet.Type.MALE
                                    PointTypeEnum.TOILET_FEMALE -> Marker.Toilet.Type.FEMALE
                                    else -> Marker.Toilet.Type.COMBINED
                                }
                            )
                        )
                    }
                }
            }
        }
        return MapData(markers, pathDots, pathConnections)
    }
}
