package ru.zakoulov.navigatorx.ui.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.otaliastudios.zoom.ZoomMapAdapter
import com.otaliastudios.zoom.ZoomMapViewHolder
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.data.Marker

class MarkerAdapter(
    private val data: List<Marker>,
    private val callbacks: MarkerCallbacks
) : ZoomMapAdapter<MarkerAdapter.MarkerViewHolder>() {
    override fun createViewHolder(parent: ViewGroup, type: Int): MarkerViewHolder {
        fun inflateView(@LayoutRes layoutResId: Int): View {
            return LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        }

        return when (type) {
            ROOM_TYPE -> MarkerViewHolder.Room(inflateView(R.layout.marker_room), callbacks)
            else -> MarkerViewHolder.Room(inflateView(R.layout.marker_room), callbacks)
        }
    }

    override fun bindViewHolder(viewHolder: MarkerViewHolder, position: Int, type: Int) {
        val marker = data[position]
        viewHolder.setupViewHolder(marker)
    }

    override fun getTypeFor(position: Int): Int {
        return when (data[position]) {
            is Marker.Room -> ROOM_TYPE
            else -> ROOM_TYPE
        }
    }

    override fun getChildCount() = data.size

    sealed class MarkerViewHolder(view: View, protected val callbacks: MarkerCallbacks) : ZoomMapViewHolder(view) {
        private var positionX: Float = 0f
        private var positionY: Float = 0f
        private var visibilityRate: Float = 0f
        private var isVisible: Boolean = false

        open fun setupViewHolder(markerData: Marker) {
            positionX = markerData.positionX
            positionY = markerData.positionY
            visibilityRate = markerData.scaleVisible
            view.visibility = View.INVISIBLE
            view.setOnClickListener {
                callbacks.onMarkerSelected(markerData)
            }
        }

        override fun getPositionX() = positionX
        override fun getPositionY() = positionY

        override fun onVisibilityRateChanged(rate: Float) {
            when {
                !isVisible && rate >= visibilityRate -> showView()
                isVisible && rate < visibilityRate - DEPTH_SHIFT -> hideView()
            }
        }

        private fun showView() {
            with(view) {
                clearAnimation()
                visibility = View.VISIBLE
                alpha = 0f
                animate()
                    .setDuration(ANIMATION_TIME)
                    .alpha(1f)
                    .start()
            }
            isVisible = true
        }

        private fun hideView() {
            with(view) {
                clearAnimation()
                visibility = View.VISIBLE
                alpha = 1f
                animate()
                    .setDuration(ANIMATION_TIME)
                    .alpha(0f)
                    .withEndAction {
                        view.visibility = View.INVISIBLE
                    }
                    .start()
            }
            isVisible = false
        }

        class Room(view: View, callbacks: MarkerCallbacks) : MarkerViewHolder(view, callbacks) {
            private var markerPointer: View = view.findViewById(R.id.marker_pointer)
            private var roomNumber: TextView = view.findViewById(R.id.room_number)

            override fun getPivotX() = view.width / 2f
            override fun getPivotY() = markerPointer.bottom.toFloat()

            override fun setupViewHolder(markerData: Marker) {
                super.setupViewHolder(markerData)
                (markerData as? Marker.Room)?.let {
                    roomNumber.text = markerData.roomNumber
                }
            }
        }
    }

    companion object {
        private const val TAG = "MarkerAdapter"
        private const val ANIMATION_TIME = 200L
        private const val DEPTH_SHIFT = 0.035f

        private const val ROOM_TYPE = 0
    }
}
