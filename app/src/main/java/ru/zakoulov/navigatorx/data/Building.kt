package ru.zakoulov.navigatorx.data

import androidx.annotation.DrawableRes
import ru.zakoulov.navigatorx.R

enum class Building(
    val id: Int,
    val title: String,
    val address: String,
    val floors: Int,
    val floorsBitmapRes: List<Int>,
    @DrawableRes val buildingImage: Int
) {
    MAIN_CORPUS(
        id = 0,
        title = "Главный корпус",
        address = "Кронверкский проспект, д. 49",
        floors = 8,
        floorsBitmapRes = listOf(
            R.drawable.kronv_1,
            R.drawable.kronv_2,
            R.drawable.kronv_3,
            R.drawable.kronv_4,
            R.drawable.kronv_5,
            R.drawable.kronv_6,
            R.drawable.kronv_7,
            R.drawable.kronv_8,
        ),
        buildingImage = R.drawable.ic_kronva
    ),
    LOMO(
        id = 1,
        title = "Корпус на Ломоносово",
        address = "Улица Ломоносова, д. 9",
        floors = 6,
        floorsBitmapRes = listOf(
            R.drawable.lomo_1,
            R.drawable.lomo_2,
            R.drawable.lomo_3,
            R.drawable.lomo_4,
            R.drawable.lomo_5,
            R.drawable.lomo_6,
        ),
        buildingImage = R.drawable.ic_lomo
    ),
//    GRIVA(
//        id = 2,
//        title = "Корпус на Гривцова",
//        address = "Переулок Гривцова, д. 14",
//        floors = 5,
//        floorsBitmapRes = listOf(
//            R.drawable.lomo1,
//            R.drawable.lomo1,
//            R.drawable.lomo1,
//            R.drawable.lomo1,
//            R.drawable.lomo1,
//        )
//    ),
//    CHAIKA(
//        id = 3,
//        title = "Корпус на Чайковского",
//        address = "Улица Чайковского, д. 11/2",
//        floors = 5,
//        floorsBitmapRes = listOf(
//            R.drawable.lomo1,
//            R.drawable.lomo1,
//            R.drawable.lomo1,
//            R.drawable.lomo1,
//            R.drawable.lomo1,
//        )
//    ),
//    BIRZHA(
//        id = 4,
//        title = "Корпус на Биржевой линии",
//        address = "Биржевая линия, д. 14",
//        floors = 5,
//        floorsBitmapRes = listOf(
//            R.drawable.lomo1,
//            R.drawable.lomo1,
//            R.drawable.lomo1,
//            R.drawable.lomo1,
//            R.drawable.lomo1,
//        )
//    )
}
