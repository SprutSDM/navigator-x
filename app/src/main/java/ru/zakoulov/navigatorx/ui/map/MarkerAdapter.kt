package ru.zakoulov.navigatorx.ui.map

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.otaliastudios.zoom.ZoomMapAdapter
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.data.Marker

class MarkerAdapter(
    data: List<Marker>,
    private val callbacks: MarkerCallbacks
) : ZoomMapAdapter<MarkerViewHolder>() {

    var data = data
        set(value) {
            Log.d(TAG, "value size: ${value.size}")
            field = value
            Log.d(TAG, "DataSetChanged: ")
            notifyDataSetChanged()
        }

    override fun createViewHolder(parent: ViewGroup, type: Int): MarkerViewHolder {
        fun inflateView(@LayoutRes layoutResId: Int): View {
            return LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        }

        return when (type) {
            TYPE_ROOM -> MarkerViewHolder.Room(inflateView(R.layout.marker_room), callbacks)
            TYPE_STAIRS -> MarkerViewHolder.Stairs(inflateView(R.layout.marker_stairs), callbacks)
            else -> MarkerViewHolder.Room(inflateView(R.layout.marker_room), callbacks)
        }
    }

    override fun bindViewHolder(viewHolder: MarkerViewHolder, position: Int, type: Int) {
        val marker = data[position]
        viewHolder.setupViewHolder(marker)
    }

    override fun getTypeFor(position: Int): Int {
        return when (data[position]) {
            is Marker.Room -> TYPE_ROOM
            is Marker.Stairs -> TYPE_STAIRS
            else -> TYPE_ROOM
        }
    }

    override fun getChildCount() = data.size

    companion object {
        private const val TAG = "MarkerAdapter"

        private const val TYPE_ROOM = 0
        private const val TYPE_STAIRS = 1
    }
}
