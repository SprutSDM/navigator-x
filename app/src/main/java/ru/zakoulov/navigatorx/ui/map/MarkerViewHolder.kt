package ru.zakoulov.navigatorx.ui.map

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.otaliastudios.zoom.ZoomMapViewHolder
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.data.Marker
import ru.zakoulov.navigatorx.ui.setBackgroundShapeColor
import ru.zakoulov.navigatorx.ui.setTextColorRes
import ru.zakoulov.navigatorx.ui.setTintColor

sealed class MarkerViewHolder(view: View, protected val callbacks: MarkerCallbacks) : ZoomMapViewHolder(view) {
    private var positionX: Float = 0f
    private var positionY: Float = 0f
    private var visibilityRate: Float = 0f
    private var isVisible: Boolean = false

    open fun setupViewHolder(markerData: MarkerData) {
        positionX = markerData.marker.positionX
        positionY = markerData.marker.positionY
        visibilityRate = markerData.marker.scaleVisible
        // TODO replace to View.INVISIBLE after optimize UI updates
        view.visibility = View.VISIBLE
        isVisible = false
        view.setOnClickListener {
            callbacks.onMarkerSelected(markerData.marker)
        }
    }

    override fun getPositionX() = positionX
    override fun getPositionY() = positionY

    override fun onVisibilityRateChanged(rate: Float) {
        // TODO uncomment after optimize UI updates
//        when {
//            !isVisible && rate >= visibilityRate -> showView()
//            isVisible && rate < visibilityRate - DEPTH_SHIFT -> hideView()
//        }
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
        private var markerPointer: ImageView = view.findViewById(R.id.marker_pointer)
        private var roomNumber: TextView = view.findViewById(R.id.room_number)

        override fun getPivotX() = view.width / 2f
        override fun getPivotY() = markerPointer.bottom.toFloat()

        override fun setupViewHolder(markerData: MarkerData) {
            super.setupViewHolder(markerData)
            if (markerData.isSelected) {
                markerPointer.setTintColor(R.color.colorPrimary)
                roomNumber.setBackgroundShapeColor(R.color.colorPrimary)
                roomNumber.setTextColorRes(android.R.color.white)
            } else {
                markerPointer.setTintColor(android.R.color.white)
                roomNumber.setBackgroundShapeColor(android.R.color.white)
                roomNumber.setTextColorRes(android.R.color.black)
            }
            (markerData.marker as? Marker.Room)?.let {
                roomNumber.text = markerData.marker.roomNumber
            }
        }
    }

    class Stairs(view: View, callbacks: MarkerCallbacks) : MarkerViewHolder(view, callbacks) {
        private var markerPointer: View = view.findViewById(R.id.marker_pointer)
        private var arrow: ImageView = view.findViewById(R.id.arrow_image)

        override fun getPivotX() = view.width / 2f
        override fun getPivotY() = markerPointer.bottom.toFloat()

        override fun setupViewHolder(markerData: MarkerData) {
            super.setupViewHolder(markerData)
            (markerData.marker as? Marker.Stairs)?.let {
                arrow.rotation = if (it.isUp) 180f else 0f
            }
        }
    }

    companion object {
        private const val ANIMATION_TIME = 200L
        private const val DEPTH_SHIFT = 0.035f
    }
}
