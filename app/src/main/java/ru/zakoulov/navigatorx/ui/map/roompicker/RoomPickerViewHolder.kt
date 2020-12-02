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
    protected val roomImage = view.findViewById<AppCompatImageView>(R.id.room_image).apply {
        clipToOutline = true
    }
    protected val roomTitle: TextView = view.findViewById(R.id.room_title)
    protected val roomNumber: TextView = view.findViewById(R.id.marker_text)

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
                roomTitle.text = roomMarker.roomTitle
                roomNumber.text = roomMarker.roomNumber
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
                roomImage.setImageResource(R.drawable.ic_enterance)
            }
        }
    }
}
