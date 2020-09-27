package ru.zakoulov.navigatorx.ui.map

import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.otaliastudios.zoom.ZoomEngine
import kotlinx.android.synthetic.main.fragment_map.markers_layout
import kotlinx.android.synthetic.main.fragment_map.zoom_layout
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.map.Map
import ru.zakoulov.navigatorx.map.Marker
import ru.zakoulov.navigatorx.map.RawMarkerData
import kotlin.random.Random


class MapFragment : Fragment(R.layout.fragment_map) {

    private val rawMarkers = List(200) {
        RawMarkerData(
            id = it,
            x = Random.nextInt(IMAGE_WIDTH),
            y = Random.nextInt(IMAGE_HEIGHT),
            label = Random.nextInt(100, 10000).toString()
        )
    }

    private val map = Map(rawMarkers)

    private var markers = mutableListOf<MarkerViewHolder>()
//    private val json = map.markers.joinToString(prefix = "[", postfix = "]"){ """{"id":${it.id},"label":"${it.label}","depth_rate":${it.depthRate},"parent_id":${it.parentId}}""" }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutInflater = LayoutInflater.from(view.context)
        val clipboard = getSystemService(requireContext(),
            ClipboardManager::class.java
        )
//        val clip = ClipData.newPlainText("label", json)
//        clipboard!!.setPrimaryClip(clip)
        val before = System.currentTimeMillis()
        map.markers.forEach {
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
                        zoom = engine.realZoom,
                        zoom2 = engine.zoom
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

        private var depthRate = 0f

        private var xOffset: Float = 0f
        private var yOffset: Float = 0f

        fun setupWith(marker: Marker) {
            textView.text = marker.label
            positionX = marker.x.toFloat()
            positionY = marker.y.toFloat()
            depthRate = marker.depthRate
            xOffset = view.width / 2f
            yOffset = markerPointer.bottom.toFloat()
            view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    xOffset = view.width / 2f
                    yOffset = markerPointer.bottom.toFloat()
                }
            })
        }

        fun updatePosition(scaledPan: Pair<Float, Float>, parentSize: Pair<Float, Float>, zoom: Float, zoom2: Float) {
            view.apply {
                translationX = scaledPan.first - xOffset + positionX / IMAGE_WIDTH * parentSize.first * zoom
                translationY = scaledPan.second - yOffset + positionY / IMAGE_HEIGHT * parentSize.second * zoom
                val zoomWidthPercent = (zoom2 - MIN_ZOOM) / (MAX_ZOOM - MIN_ZOOM)
                val zoomDepthWidth = (MAX_DEPTH_RATE_AT_ZOOM - MIN_DEPTH_RATE_AT_ZOOM)
                val zoomDepthRate = zoomWidthPercent * zoomDepthWidth + MIN_DEPTH_RATE_AT_ZOOM
                Log.d(TAG, "updatePosition: zoomDepthRate ${zoomDepthRate} zoom ${zoom}} ${zoom2}")
                if (zoomDepthRate >= depthRate) {
                    visibility = View.VISIBLE
                } else {
                    visibility = View.GONE
                }
            }
        }
    }

    companion object {
        const val TAG = "MapFragment"

        private const val IMAGE_WIDTH = 4096
        private const val IMAGE_HEIGHT = 4096

        private const val MAX_DEPTH_RATE_AT_ZOOM = 1.2f
        private const val MIN_DEPTH_RATE_AT_ZOOM = 0.4f
        private const val MIN_ZOOM = 1f
        private const val MAX_ZOOM = 6f

        val instance: MapFragment by lazy { MapFragment() }
    }
}
