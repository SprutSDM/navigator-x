package ru.zakoulov.navigatorx.ui.map.roompicker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.data.Marker

sealed class RoomPickerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    protected val roomImage: ImageView = view.findViewById(R.id.room_image)
    protected val roomTitle: TextView = view.findViewById(R.id.room_title)
    protected val roomNumber: TextView = view.findViewById(R.id.room_number)

    @CallSuper
    open fun setup(marker: Marker, onClickListener: View.OnClickListener) {
        itemView.setOnClickListener(onClickListener)
    }

    class RoomViewHolder(view: View) : RoomPickerViewHolder(view) {
        override fun setup(marker: Marker, onClickListener: View.OnClickListener) {
            super.setup(marker, onClickListener)
            (marker as? Marker.Room)?.let { roomMarker ->
                roomTitle.text = roomMarker.roomTitle
                roomNumber.text = roomMarker.roomNumber
            }
        }
    }
}
