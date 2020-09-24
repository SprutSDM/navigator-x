package ru.zakoulov.navigatorx.ui.map

import androidx.fragment.app.Fragment
import ru.zakoulov.navigatorx.R

class MapFragment : Fragment(R.layout.fragment_map) {

    companion object {
        const val TAG = "MapFragment"

        private var instance: MapFragment? = null

        fun getInstance(): MapFragment {
            return instance ?: MapFragment().also { instance = it }
        }
    }
}
