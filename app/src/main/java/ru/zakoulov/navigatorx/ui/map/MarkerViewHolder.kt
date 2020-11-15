package ru.zakoulov.navigatorx.ui.map

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.otaliastudios.zoom.ZoomMapViewHolder
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.data.Marker

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

    class Stairs(view: View, callbacks: MarkerCallbacks) : MarkerViewHolder(view, callbacks) {
        private var markerPointer: View = view.findViewById(R.id.marker_pointer)
        private var arrow: ImageView = view.findViewById(R.id.arrow_image)

        override fun getPivotX() = view.width / 2f
        override fun getPivotY() = markerPointer.bottom.toFloat()

        override fun setupViewHolder(markerData: Marker) {
            super.setupViewHolder(markerData)
            (markerData as? Marker.Stairs)?.let {
                arrow.rotation = if (it.isUp) 180f else 0f
            }
        }
    }

    companion object {
        private const val ANIMATION_TIME = 200L
        private const val DEPTH_SHIFT = 0.035f
    }
}