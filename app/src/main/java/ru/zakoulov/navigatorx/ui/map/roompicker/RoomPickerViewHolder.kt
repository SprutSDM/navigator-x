package ru.zakoulov.navigatorx.ui.map.roompicker

import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.data.Marker

sealed class RoomPickerViewHolder(
    view: View,
    protected val callbacks: RoomPickerCallbacks
) : RecyclerView.ViewHolder(view) {
    protected val roomImage: AppCompatImageView = view.findViewById(R.id.room_image)
    protected val roomTitle: TextView = view.findViewById(R.id.room_title)
    protected val roomNumber: TextView = view.findViewById(R.id.marker_text)
    protected val roomFloor: TextView = view.findViewById(R.id.room_floor)

    @CallSuper
    open fun setup(marker: Marker) {
        itemView.setOnClickListener {
            callbacks.onRoomPicked(marker)
        }
    }

    class Room(view: View, callbacks: RoomPickerCallbacks) : RoomPickerViewHolder(view, callbacks) {
        override fun setup(marker: Marker) {
            super.setup(marker)
            (marker as? Marker.Room)?.let { roomMarker ->
                if (roomMarker.roomInfo.name != null) {
                    roomTitle.visibility = View.VISIBLE
                    roomTitle.text = roomMarker.roomInfo.name
                } else {
                    roomTitle.visibility = View.GONE
                }
                roomFloor.text = "${roomMarker.floor} ЭТАЖ"
                roomNumber.text = "${roomMarker.roomNumber} ${roomMarker.roomInfo.realUsage ?: ""}"
                roomImage.setImageResource(R.drawable.ic_place)
            }
        }
    }

    class Entrance(view: View, callbacks: RoomPickerCallbacks) : RoomPickerViewHolder(view, callbacks) {
        override fun setup(marker: Marker) {
            super.setup(marker)
            (marker as? Marker.Entrance)?.let { entranceMarker ->
                roomTitle.text = entranceMarker.labelText
                roomNumber.text = entranceMarker.labelText
                roomImage.setImageResource(R.drawable.ic_entrance)
                roomFloor.text = "${entranceMarker.floor} ЭТАЖ"
            }
        }
    }
}
