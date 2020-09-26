package ru.zakoulov.navigatorx.ui.map

import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.fragment.app.Fragment
import com.otaliastudios.zoom.ZoomEngine
import kotlinx.android.synthetic.main.fragment_map.markers_layout
import kotlinx.android.synthetic.main.fragment_map.zoom_layout
import ru.zakoulov.navigatorx.R

class MapFragment : Fragment(R.layout.fragment_map) {

    private val markers = listOf(232 to 231, 669 to 440)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutInflater = LayoutInflater.from(view.context)
        markers.forEach {
            val view = layoutInflater.inflate(R.layout.marker, markers_layout, false)
            markers_layout.addView(view)
        }
        zoom_layout.engine.addListener(object : ZoomEngine.Listener {
            override fun onIdle(engine: ZoomEngine) = Unit

            override fun onUpdate(engine: ZoomEngine, matrix: Matrix) {
                markers_layout.forEachIndexed { index, markerView ->
                    markerView.translationX =
                        engine.scaledPanX + markers[index].first / 775f * engine.contentWidth * engine.realZoom
                    markerView.translationY =
                        engine.scaledPanY + markers[index].second / 614f * engine.contentHeight * engine.realZoom
                }
            }
        })
    }

    companion object {
        const val TAG = "MapFragment"

        val instance: MapFragment by lazy { MapFragment() }
    }
}
