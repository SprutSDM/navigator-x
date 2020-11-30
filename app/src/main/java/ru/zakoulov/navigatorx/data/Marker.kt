package ru.zakoulov.navigatorx.data

sealed class Marker(
    val id: String,
    val scaleVisible: Float,
    val positionX: Float,
    val positionY: Float,
    val floor: Int,
    val building: Building,
    val corpus: Int
) {
    class Room(
        id: String,
        scaleVisible: Float,
        positionX: Float,
        positionY: Float,
        floor: Int,
        building: Building,
        corpus: Int,
        val roomNumber: String,
        val roomTitle: String
    ) : Marker(
        id = id,
        scaleVisible = scaleVisible,
        positionX = positionX,
        positionY = positionY,
        floor = floor,
        building = building,
        corpus = corpus
    )

    class Message(
        id: String,
        scaleVisible: Float,
        positionX: Float,
        positionY: Float,
        floor: Int,
        building: Building,
        corpus: Int,
        val message: String
    ) : Marker(
        id = id,
        scaleVisible = scaleVisible,
        positionX = positionX,
        positionY = positionY,
        floor = floor,
        building = building,
        corpus = corpus
    )

    class Icon(
        id: String,
        scaleVisible: Float,
        positionX: Float,
        positionY: Float,
        floor: Int,
        building: Building,
        corpus: Int
    ) : Marker(
        id = id,
        scaleVisible = scaleVisible,
        positionX = positionX,
        positionY = positionY,
        floor = floor,
        building = building,
        corpus = corpus
    )

    class Stairs(
        id: String,
        scaleVisible: Float,
        positionX: Float,
        positionY: Float,
        floor: Int,
        building: Building,
        corpus: Int,
        val isUp: Boolean
    ) : Marker(
        id = id,
        scaleVisible = scaleVisible,
        positionX = positionX,
        positionY = positionY,
        floor = floor,
        building = building,
        corpus = corpus
    )

    class Elevator(
        id: String,
        scaleVisible: Float,
        positionX: Float,
        positionY: Float,
        floor: Int,
        building: Building,
        corpus: Int
    ) : Marker(
        id = id,
        scaleVisible = scaleVisible,
        positionX = positionX,
        positionY = positionY,
        floor = floor,
        building = building,
        corpus = corpus
    )

    class Entrance(
        id: String,
        scaleVisible: Float,
        positionX: Float,
        positionY: Float,
        floor: Int,
        building: Building,
        corpus: Int,
        val type: Type,
        val labelText: String
    ) : Marker(
        id = id,
        scaleVisible = scaleVisible,
        positionX = positionX,
        positionY = positionY,
        floor = floor,
        building = building,
        corpus = corpus
    ) {
        enum class Type {
            MAIN,
            COMMON
        }
    }

    class Toilet(
        id: String,
        scaleVisible: Float,
        positionX: Float,
        positionY: Float,
        floor: Int,
        building: Building,
        corpus: Int,
        val type: Type
    ) : Marker(
        id = id,
        scaleVisible = scaleVisible,
        positionX = positionX,
        positionY = positionY,
        floor = floor,
        building = building,
        corpus = corpus
    ) {
        enum class Type {
            MALE,
            FEMALE,
            COMBINED
        }
    }
}
