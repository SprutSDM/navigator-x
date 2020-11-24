package ru.zakoulov.navigatorx.ui.map

import androidx.recyclerview.widget.DiffUtil

class MarkerDiffCallback(
    private val oldMarkers: List<MarkerData>,
    private val newMarkers: List<MarkerData>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldMarkers.size
    override fun getNewListSize() = newMarkers.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldMarkers[oldItemPosition].marker.id == newMarkers[newItemPosition].marker.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldMarkerData = oldMarkers[oldItemPosition]
        val newMarkerData = newMarkers[newItemPosition]
        return (oldMarkerData.isSelected == newMarkerData.isSelected &&
                oldMarkerData.additionalText == newMarkerData.additionalText &&
                oldMarkerData.marker == newMarkerData.marker)
    }
}
