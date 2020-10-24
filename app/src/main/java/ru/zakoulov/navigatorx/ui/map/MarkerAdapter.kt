package ru.zakoulov.navigatorx.ui.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.otaliastudios.zoom.ZoomMapAdapter
import com.otaliastudios.zoom.ZoomMapViewHolder
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.map.Marker

class MarkerAdapter(
    private val data: List<Marker>,
    private val callbacks: MarkerCallbacks
) : ZoomMapAdapter<MarkerAdapter.MarkerViewHolder>() {
    override fun createViewHolder(parent: ViewGroup): MarkerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.marker, parent, false)
        return MarkerViewHolder(view, callbacks)
    }

    override fun bindViewHolder(viewHolder: MarkerViewHolder, position: Int) {
        val marker = data[position]
        viewHolder.setupViewHolder(marker)
    }

    override fun getChildCount() = data.size

    class MarkerViewHolder(view: View, private val callbacks: MarkerCallbacks) : ZoomMapViewHolder(view) {
        private var id: Int = 0
        private var positionX: Float = 0f
        private var positionY: Float = 0f
        private var visibilityRate: Float = 0f
        private var markerPointer: View = view.findViewById(R.id.marker_pointer)
        private var isVisible: Boolean = false

        private var label: TextView = view.findViewById(R.id.marker_text)

        fun setupViewHolder(markerData: Marker) {
            positionX = markerData.x.toFloat()
            positionY = markerData.y.toFloat()
            visibilityRate = markerData.depthRate
            label.text = markerData.label
            id = markerData.id
            view.visibility = View.INVISIBLE
            view.setOnClickListener {
                callbacks.onMarkerSelected(markerData)
            }
        }

        override fun getPositionX() = positionX
        override fun getPositionY() = positionY
        override fun getPivotX() = view.width / 2f
        override fun getPivotY() = markerPointer.bottom.toFloat()

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
    }

    companion object {
        private const val TAG = "MarkerAdapter"
        private const val ANIMATION_TIME = 200L
        private const val DEPTH_SHIFT = 0.035f
    }
}
