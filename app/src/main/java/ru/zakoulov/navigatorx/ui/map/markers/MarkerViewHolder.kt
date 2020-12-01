package ru.zakoulov.navigatorx.ui.map.markers

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import com.otaliastudios.zoom.ZoomMapViewHolder
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.data.Marker
import ru.zakoulov.navigatorx.ui.setBackgroundShapeColor
import ru.zakoulov.navigatorx.ui.setDrawablesColorRes
import ru.zakoulov.navigatorx.ui.setTextColorRes
import ru.zakoulov.navigatorx.ui.setTintColor

sealed class MarkerViewHolder(view: View, protected val callbacks: MarkerCallbacks) : ZoomMapViewHolder(view) {
    private var markerPointer: ImageView = view.findViewById(R.id.marker_pointer)
    private var markerText: TextView = view.findViewById(R.id.marker_text)

    private var positionX: Float = 0f
    private var positionY: Float = 0f
    private var visibilityRate: Float = 0f
    private var isVisible: Boolean = false
    private var markerId: String? = null
    private var forceVisible: Boolean = false

    @CallSuper
    open fun setupViewHolder(markerData: MarkerData) {
        positionX = markerData.marker.positionX
        positionY = markerData.marker.positionY
        visibilityRate = markerData.marker.scaleVisible
        forceVisible = markerData.forceVisible
        Log.d("MarkerViewHolder", "setupViewHolder: $visibilityRate")
        // If it's the same marker, we don't have to make appear animation
        if (markerId != markerData.marker.id) {
            view.visibility = View.INVISIBLE
            isVisible = false
        }
        if (markerData.forceVisible) {
            view.visibility = View.VISIBLE
            isVisible = true
        }
        markerId = markerData.marker.id
        view.setOnClickListener {
            callbacks.onMarkerSelected(markerData.marker)
        }
        markerText.text = getTextFrom(markerData)
        markerText.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            getIconResFrom(markerData),
            0
        )
        markerText.compoundDrawablePadding = if (markerText.text.isEmpty()) {
            0
        } else {
            markerText.resources.getDimension(R.dimen.markerDrawablePadding).toInt()
        }
        if (markerData.isSelected) {
            markerPointer.setTintColor(R.color.colorPrimary)
            markerText.apply {
                setBackgroundShapeColor(R.color.colorPrimary)
                setTextColorRes(android.R.color.white)
                setDrawablesColorRes(android.R.color.white)
            }
        } else {
            markerPointer.setTintColor(android.R.color.white)
            markerText.apply {
                setBackgroundShapeColor(android.R.color.white)
                setTextColorRes(android.R.color.black)
                setDrawablesColorRes(R.color.colorPrimary)
            }
        }
    }

    abstract fun getTextFrom(markerData: MarkerData): String

    @DrawableRes
    abstract fun getIconResFrom(markerData: MarkerData): Int

    fun clearViewHolder() {
        markerId = null
    }

    override fun getPivotX() = view.width / 2f
    override fun getPivotY() = markerPointer.bottom.toFloat()

    override fun getPositionX() = positionX
    override fun getPositionY() = positionY

    override fun onVisibilityRateChanged(rate: Float) {
        if (forceVisible) {
            if (!isVisible) {
                showView()
            }
            return
        }
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
                .setDuration(view.resources.getInteger(R.integer.animation_duration).toLong())
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
                .setDuration(view.resources.getInteger(R.integer.animation_duration).toLong())
                .alpha(0f)
                .withEndAction {
                    view.visibility = View.INVISIBLE
                }
                .start()
        }
        isVisible = false
    }

    class Room(view: View, callbacks: MarkerCallbacks) : MarkerViewHolder(view, callbacks) {
        override fun getTextFrom(markerData: MarkerData) = (markerData.marker as Marker.Room).roomNumber
        override fun getIconResFrom(markerData: MarkerData) = 0
    }

    class Stairs(view: View, callbacks: MarkerCallbacks) : MarkerViewHolder(view, callbacks) {
        override fun getTextFrom(markerData: MarkerData) = ""
        override fun getIconResFrom(markerData: MarkerData): Int {
            return if ((markerData.marker as Marker.Stairs).isUp) {
                R.drawable.ic_arrow_up
            } else {
                R.drawable.ic_arrow_down
            }
        }
    }

    class Toilet(view: View, callbacks: MarkerCallbacks) : MarkerViewHolder(view, callbacks) {
        override fun getTextFrom(markerData: MarkerData) = "WC"
        override fun getIconResFrom(markerData: MarkerData): Int {
            return when ((markerData.marker as Marker.Toilet).type) {
                Marker.Toilet.Type.MALE -> R.drawable.ic_toilet_male
                Marker.Toilet.Type.FEMALE -> R.drawable.ic_toilet_female
                Marker.Toilet.Type.COMBINED -> R.drawable.ic_toilet_combined
            }
        }
    }

    class Entrance(view: View, callbacks: MarkerCallbacks) : MarkerViewHolder(view, callbacks) {
        override fun getTextFrom(markerData: MarkerData) = (markerData.marker as Marker.Entrance).labelText
        override fun getIconResFrom(markerData: MarkerData) = 0
    }

    companion object {
        private const val DEPTH_SHIFT = 0.02f
    }
}
