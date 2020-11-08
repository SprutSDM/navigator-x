package ru.zakoulov.navigatorx.data.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.types.ObjectId
import org.jetbrains.annotations.NotNull
import java.lang.IllegalArgumentException

enum class PointTypeEnum(val id: Int) {
    PATH(0),
    ROOM(1),
    MESSAGE(2),
    ICON(3),
    OTHER(4),
    STAIRS_UP(5),
    STAIRS_DOWN(6),
    ELEVATOR(7)
}

open class MapPointModel(
    var scaleVisible: Double = 0.0,
    var labelText: String = "",
    var text: String = "",
    var info: String = "",
    var building: Int = 0,
    var floor: Int = 0,
    var korpus: Int = 0,
    var positionX: Double = 0.0,
    var positionY: Double = 0.0
) : RealmObject() {
    @PrimaryKey
    var _id: ObjectId = ObjectId()

    var type: Int = PointTypeEnum.OTHER.id
    var typeEnum: PointTypeEnum
        set(value) {
            type = value.id
        }
        get() {
            return PointTypeEnum.values().find { it.id == type } ?: PointTypeEnum.OTHER
        }
}
