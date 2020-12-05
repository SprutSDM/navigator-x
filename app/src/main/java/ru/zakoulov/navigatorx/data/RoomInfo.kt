package ru.zakoulov.navigatorx.data

data class RoomInfo(
    val square: String?,
    val name: String?,
    val capacity: String?,
    val realUsage: String?,
    val departmentName: String?,
    val equipment: List<String>,
    val bookUrl: String?
)
