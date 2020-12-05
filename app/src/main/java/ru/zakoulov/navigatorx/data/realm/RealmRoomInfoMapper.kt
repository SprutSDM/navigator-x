package ru.zakoulov.navigatorx.data.realm

import ru.zakoulov.navigatorx.data.RoomInfo

class RealmRoomInfoMapper {
    fun map(realmRoomInfo: RealmRoomInfo): RoomInfo {
        return RoomInfo(
            square = realmRoomInfo.square ?: "Нет информации",
            name = realmRoomInfo.rtName?.capitalize() ?: "Нет информации",
            capacity = realmRoomInfo.capacity ?: "Нет информации",
            realUsage = realmRoomInfo.realUsage?.capitalize() ?: "Нет информации",
            departmentName = realmRoomInfo.capacity?.capitalize() ?: "Нет информации",
            equipment = realmRoomInfo.equipment.map {
                when {
                    it.name == null || it.count == 0 -> ""
                    it.count > 1 -> "${it.name.capitalize()} x${it.count}"
                    else -> it.name.capitalize()
                }
            },
            bookUrl = realmRoomInfo.bookUrl
        )
    }
}
