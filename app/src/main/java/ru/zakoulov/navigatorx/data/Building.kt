package ru.zakoulov.navigatorx.data

import ru.zakoulov.navigatorx.R

enum class Building(
    val id: Int,
    val title: String,
    val address: String,
    val floors: Int,
    val floorsBitmapRes: List<Int>
) {
    MAIN_CORPUS(
        id = 0,
        title = "Главный корпус",
        address = "Кронверкский проспект, д. 49",
        floors = 5,
        floorsBitmapRes = listOf(
            R.drawable.lomo1,
            R.drawable.lomo1,
            R.drawable.lomo1,
            R.drawable.lomo1,
            R.drawable.lomo1,
        )
    ),
    LOMO(
        id = 1,
        title = "Корпус на Ломоносово",
        address = "Улица Ломоносова, д. 9",
        floors = 5,
        floorsBitmapRes = listOf(
            R.drawable.lomo1,
            R.drawable.lomo1,
            R.drawable.lomo1,
            R.drawable.lomo1,
            R.drawable.lomo1,
        )
    ),
    GRIVA(
        id = 2,
        title = "Корпус на Гривцова",
        address = "Переулок Гривцова, д. 14",
        floors = 5,
        floorsBitmapRes = listOf(
            R.drawable.lomo1,
            R.drawable.lomo1,
            R.drawable.lomo1,
            R.drawable.lomo1,
            R.drawable.lomo1,
        )
    ),
    CHAIKA(
        id = 3,
        title = "Корпус на Чайковского",
        address = "Улица Чайковского, д. 11/2",
        floors = 5,
        floorsBitmapRes = listOf(
            R.drawable.lomo1,
            R.drawable.lomo1,
            R.drawable.lomo1,
            R.drawable.lomo1,
            R.drawable.lomo1,
        )
    ),
    BIRZHA(
        id = 4,
        title = "Корпус на Биржевой линии",
        address = "Биржевая линия, д. 14",
        floors = 5,
        floorsBitmapRes = listOf(
            R.drawable.lomo1,
            R.drawable.lomo1,
            R.drawable.lomo1,
            R.drawable.lomo1,
            R.drawable.lomo1,
        )
    )
}
