package ru.zakoulov.navigatorx.ui.map.markers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.otaliastudios.zoom.ZoomMapAdapter
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.data.Marker

class MarkerAdapter(
    data: List<MarkerData>,
    private val callbacks: MarkerCallbacks
) : ZoomMapAdapter<MarkerViewHolder>() {

    private val markerListUpdateCallback = MarkerListUpdateCallback(this)

    var data = data
        set(value) {
            val diffCallback = MarkerDiffCallback(field, value)
            field = value
            val diffResult = DiffUtil.calculateDiff(diffCallback, false)
            diffResult.dispatchUpdatesTo(markerListUpdateCallback)
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
        val markerData = data[position]
        viewHolder.setupViewHolder(markerData)
    }

    override fun onViewDetached(viewHolder: MarkerViewHolder) {
        viewHolder.clearViewHolder()
    }

    override fun getTypeFor(position: Int): Int {
        val markerData = data[position]
        return when (markerData.marker) {
            is Marker.Room -> TYPE_ROOM
            is Marker.Stairs -> TYPE_STAIRS
            else -> TYPE_ROOM
        }
    }

    override fun getChildCount() = data.size

    private class MarkerListUpdateCallback(private val adapter: ZoomMapAdapter<*>) : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {
            adapter.notifyDataSetInserted(position = position, count = count)
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetRemoved(position = position, count = count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetMoved(fromPosition = fromPosition, toPosition = toPosition)
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyDataSetChanged(position = position, count = count)
        }
    }

    companion object {
        private const val TAG = "MarkerAdapter"

        private const val TYPE_ROOM = 0
        private const val TYPE_STAIRS = 1
    }
}
