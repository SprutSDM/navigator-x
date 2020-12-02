package ru.zakoulov.navigatorx.ui.map.roompicker

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.data.Marker
import java.lang.IllegalStateException

class RoomPickerAdapter(
    markers: List<Marker>,
    private val callbacks: RoomPickerCallbacks
) : RecyclerView.Adapter<RoomPickerViewHolder>() {

    var markers: List<Marker> = markers
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomPickerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.clickable_element_room, parent, false)
        return when (viewType) {
            TYPE_ROOM -> RoomPickerViewHolder.Room(view, callbacks)
            TYPE_ENTRANCE -> RoomPickerViewHolder.Entrance(view, callbacks)
            else -> throw IllegalStateException("Unknown marker type: ${viewType}")
        }
    }

    override fun onBindViewHolder(holder: RoomPickerViewHolder, position: Int) {
        val marker = markers[position]
        holder.setup(marker)
    }

    override fun getItemViewType(position: Int): Int {
        val marker = markers[position]
        return when (marker) {
            is Marker.Room -> TYPE_ROOM
            is Marker.Entrance -> TYPE_ENTRANCE
            else -> throw IllegalStateException("Unsupported marker: ${marker}")
        }
    }

    override fun getItemCount() = markers.size

    companion object {
        private const val TYPE_ROOM = 0
        private const val TYPE_ENTRANCE = 1
    }
}
