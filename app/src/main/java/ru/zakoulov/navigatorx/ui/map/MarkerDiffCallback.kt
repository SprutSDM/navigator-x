package ru.zakoulov.navigatorx.ui.map

import androidx.recyclerview.widget.DiffUtil
import ru.zakoulov.navigatorx.data.Marker

class MarkerDiffCallback(
    private val oldMarkers: List<Marker>,
    private val newMarkers: List<Marker>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldMarkers.size
    override fun getNewListSize() = newMarkers.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldMarkers[oldItemPosition].id == newMarkers[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldMarkers[oldItemPosition] == newMarkers[newItemPosition]
    }
}
