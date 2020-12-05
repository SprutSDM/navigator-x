package ru.zakoulov.navigatorx.data.realm

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.types.ObjectId

enum class PointTypeEnum(val id: Int) {
    PATH(0),
    ROOM(1),
    MESSAGE(2),
    ICON(3),
    OTHER(4),
    STAIRS_UP(5),
    STAIRS_DOWN(6),
    ELEVATOR(7),
    MAIN_ENTRANCE(8),
    ENTRANCE(9),
    TOILET_MALE(10),
    TOILET_FEMALE(11),
    TOILET(12)
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
    var positionY: Double = 0.0,
    @Required var connectedIDs: RealmList<String> = RealmList()
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

class RealmRoomInfo {
    @SerializedName("real_usage")
    val realUsage: String? = null

    @SerializedName("rt_name")
    val rtName: String? = null

    @SerializedName("square")
    val square: String? = null

    @SerializedName("capacity")
    val capacity: String? = null

    @SerializedName("dep_names")
    val departmentName: String? = null

    @SerializedName("equipment")
    val equipment: List<RealmEquipment> = emptyList()

    @SerializedName("reserv_url")
    val bookUrl: String? = null
}

class RealmEquipment {
    @SerializedName("eq_name")
    val name: String? = null

    @SerializedName("eq_quan")
    val count: Int = 0
}
