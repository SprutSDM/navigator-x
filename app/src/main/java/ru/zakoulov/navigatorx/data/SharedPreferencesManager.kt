package ru.zakoulov.navigatorx.data

import android.content.SharedPreferences

class SharedPreferencesManager(private val sharedPreferences: SharedPreferences) {
    fun saveSelectedBuilding(building: Building) {
        with(sharedPreferences.edit()) {
            putInt(SELECTED_BUILDING_KEY, building.id)
            commit()
        }
    }

    fun loadSelectedBuilding(): Building {
        val storedSelectedBuildingId = sharedPreferences.getInt(SELECTED_BUILDING_KEY, DEFAULT_SELECTED_BUILDING.id)
        return Building.values().find {
            it.id == storedSelectedBuildingId
        } ?: DEFAULT_SELECTED_BUILDING
    }

    companion object {
        private const val SELECTED_BUILDING_KEY = "selected_building_key"
        private val DEFAULT_SELECTED_BUILDING = Building.MAIN_CORPUS
    }
}
