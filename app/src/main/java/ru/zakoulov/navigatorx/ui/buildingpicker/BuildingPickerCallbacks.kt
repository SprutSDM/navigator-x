package ru.zakoulov.navigatorx.ui.buildingpicker

import ru.zakoulov.navigatorx.data.Building

interface BuildingPickerCallbacks {
    fun onBuildingPicked(building: Building)
}
