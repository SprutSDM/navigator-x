package ru.zakoulov.navigatorx.ui.map

import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.otaliastudios.zoom.ZoomEngine
import kotlinx.android.synthetic.main.fragment_map.markers_layout
import kotlinx.android.synthetic.main.fragment_map.zoom_layout
import ru.zakoulov.navigatorx.R
import kotlin.random.Random

class MapFragment : Fragment(R.layout.fragment_map) {

    private val markersData = List(200) {
        Marker(
            x = Random.nextInt(IMAGE_WIDTH),
            y = Random.nextInt(IMAGE_HEIGHT),
            label = Random.nextInt(100, 10000).toString()
        )
    }

    private var markers = mutableListOf<MarkerViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutInflater = LayoutInflater.from(view.context)
        val before = System.currentTimeMillis()
        markersData.forEach {
            val view = layoutInflater.inflate(R.layout.marker_2, markers_layout, false)
            val viewHolder = MarkerViewHolder(view).apply {
                setupWith(it)
            }
            markers.add(viewHolder)
            markers_layout.addView(view)
        }
        val after = System.currentTimeMillis()
        Log.d(TAG, "onViewCreated: ${after - before}")
        zoom_layout.engine.addListener(object : ZoomEngine.Listener {
            override fun onIdle(engine: ZoomEngine) = Unit

            override fun onUpdate(engine: ZoomEngine, matrix: Matrix) {
                markers.forEach {
                    it.updatePosition(
                        scaledPan = engine.scaledPanX to engine.scaledPanY,
                        parentSize = engine.contentWidth to engine.contentHeight,
                        zoom = engine.realZoom
                    )
                }
            }
        })
    }

    private class MarkerViewHolder(val view: View) {
        private val textView = view.findViewById<TextView>(R.id.marker_text)
        private val markerPointer = view.findViewById<View>(R.id.marker_pointer)

        private var positionX: Float = 0f
        private var positionY: Float = 0f

        private var xOffset: Float = 0f
        private var yOffset: Float = 0f

        fun setupWith(marker: Marker) {
            textView.text = marker.label
            positionX = marker.x.toFloat()
            positionY = marker.y.toFloat()
            view.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    xOffset = view.width / 2f
                    yOffset = markerPointer.bottom.toFloat()
                }
            })
        }

        fun updatePosition(scaledPan: Pair<Float, Float>, parentSize: Pair<Float, Float>, zoom: Float) {
            view.apply {
                translationX = scaledPan.first - xOffset + positionX / IMAGE_WIDTH * parentSize.first * zoom
                translationY = scaledPan.second - yOffset + positionY / IMAGE_HEIGHT * parentSize.second * zoom
            }
        }
    }

    companion object {
        const val TAG = "MapFragment"

        private const val IMAGE_WIDTH = 4096
        private const val IMAGE_HEIGHT = 4096

        val instance: MapFragment by lazy { MapFragment() }
    }
}
