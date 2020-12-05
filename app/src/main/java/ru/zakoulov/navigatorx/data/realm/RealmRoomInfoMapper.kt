package ru.zakoulov.navigatorx.data.realm

import ru.zakoulov.navigatorx.data.RoomInfo

class RealmRoomInfoMapper {
    fun map(realmRoomInfo: RealmRoomInfo): RoomInfo {
        return RoomInfo(
            square = realmRoomInfo.square?.takeIf { !it.equals("нет информации", ignoreCase = true) },
            name = realmRoomInfo.rtName?.takeIf { !it.equals("нет информации", ignoreCase = true) }?.capitalize(),
            capacity = realmRoomInfo.capacity?.takeIf { !it.equals("нет информации", ignoreCase = true) }?.capitalize(),
            realUsage = realmRoomInfo.realUsage?.takeIf { !it.equals("нет информации", ignoreCase = true) }?.capitalize(),
            departmentName = realmRoomInfo.departmentName?.takeIf { !it.equals("нет информации", ignoreCase = true) }?.capitalize(),
            equipment = realmRoomInfo.equipment.map {
                when {
                    it.name == null || it.count == 0 -> "${it.name}, ${it.count}"
                    it.count > 1 -> "${it.name.capitalize()} x${it.count}"
                    else -> it.name.capitalize()
                }
            },
            bookUrl = realmRoomInfo.bookUrl
        )
    }
}
