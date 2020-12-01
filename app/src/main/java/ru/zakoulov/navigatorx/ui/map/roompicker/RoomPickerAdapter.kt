package ru.zakoulov.navigatorx.ui.map.roompicker

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.data.Marker

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
        return RoomPickerViewHolder.RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomPickerViewHolder, position: Int) {
        val marker = markers[position]
        holder.setup(marker) {
            Log.d("RoomPickerAdapter", "onBindViewHolder: clicked on $holder")
            callbacks.onRoomPicked(marker)
        }
    }

    override fun getItemCount() = markers.size
}
