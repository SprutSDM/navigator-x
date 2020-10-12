package ru.zakoulov.navigatorx.ui.map

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.otaliastudios.zoom.ZoomMapAdapter
import com.otaliastudios.zoom.ZoomMapViewHolder
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.map.Marker

class MarkerAdapter(private val data: List<Marker>) : ZoomMapAdapter<MarkerAdapter.MarkerViewHolder>() {
    override fun createViewHolder(parent: ViewGroup): MarkerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.marker, parent, false)
        return MarkerViewHolder(view)
    }

    override fun bindViewHolder(viewHolder: MarkerViewHolder, position: Int) {
        val marker = data[position]
        viewHolder.setupViewHolder(marker)
    }

    override fun getChildCount() = data.size

    class MarkerViewHolder(view: View) : ZoomMapViewHolder(view) {
        private var positionX: Float = 0f
        private var positionY: Float = 0f
        private var visibilityRate: Float = 0f
        private var markerPointer: View = view.findViewById(R.id.marker_pointer)

        private var label: TextView = view.findViewById(R.id.marker_text)

        fun setupViewHolder(markerData: Marker) {
            positionX = markerData.x.toFloat()
            positionY = markerData.y.toFloat()
            visibilityRate = markerData.depthRate
            label.text = markerData.label
            view.setOnClickListener {
                Log.d(TAG, "setupViewHolder: onClicked ${markerData.label}")
            }
        }

        override fun getVisibilityRate() = visibilityRate
        override fun getPositionX() = positionX
        override fun getPositionY() = positionY
        override fun getXPivot() = view.width / 2f
        override fun getYPivot() = markerPointer.bottom.toFloat()
    }

    companion object {
        private const val TAG = "MarkerAdapter"
    }
}
