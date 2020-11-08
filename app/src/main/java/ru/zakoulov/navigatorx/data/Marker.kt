package ru.zakoulov.navigatorx.data

sealed class Marker(
    val scaleVisible: Float,
    val positionX: Float,
    val positionY: Float,
    val floor: Int,
    val building: Building,
    val corpus: Int
) {
    class Room(
        scaleVisible: Float,
        positionX: Float,
        positionY: Float,
        floor: Int,
        building: Building,
        corpus: Int,
        val roomNumber: String,
        val roomTitle: String
    ) : Marker(
        scaleVisible = scaleVisible,
        positionX = positionX,
        positionY = positionY,
        floor = floor,
        building = building,
        corpus = corpus
    )
    class Message(
        scaleVisible: Float,
        positionX: Float,
        positionY: Float,
        floor: Int,
        building: Building,
        corpus: Int,
        val message: String
    ) : Marker(
        scaleVisible = scaleVisible,
        positionX = positionX,
        positionY = positionY,
        floor = floor,
        building = building,
        corpus = corpus
    )

    class Icon(
        scaleVisible: Float,
        positionX: Float,
        positionY: Float,
        floor: Int,
        building: Building,
        corpus: Int
    ) : Marker(
        scaleVisible = scaleVisible,
        positionX = positionX,
        positionY = positionY,
        floor = floor,
        building = building,
        corpus = corpus
    )

    class StairsUp(
        scaleVisible: Float,
        positionX: Float,
        positionY: Float,
        floor: Int,
        building: Building,
        corpus: Int
    ) : Marker(
        scaleVisible = scaleVisible,
        positionX = positionX,
        positionY = positionY,
        floor = floor,
        building = building,
        corpus = corpus
    )

    class StairsDown(
        scaleVisible: Float,
        positionX: Float,
        positionY: Float,
        floor: Int,
        building: Building,
        corpus: Int
    ) : Marker(
        scaleVisible = scaleVisible,
        positionX = positionX,
        positionY = positionY,
        floor = floor,
        building = building,
        corpus = corpus
    )

    class Elevator(
        scaleVisible: Float,
        positionX: Float,
        positionY: Float,
        floor: Int,
        building: Building,
        corpus: Int
    ) : Marker(
        scaleVisible = scaleVisible,
        positionX = positionX,
        positionY = positionY,
        floor = floor,
        building = building,
        corpus = corpus
    )
}
