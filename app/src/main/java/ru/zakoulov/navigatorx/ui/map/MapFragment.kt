package ru.zakoulov.navigatorx.ui.map

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.otaliastudios.zoom.ZoomMapAdapter
import com.otaliastudios.zoom.ZoomMapViewHolder
import kotlinx.android.synthetic.main.fragment_map.zoom_layout
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.map.Map
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

    lateinit var markerAdapter: MarkerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        markerAdapter = MarkerAdapter(map.markers)
        zoom_layout.adapter = markerAdapter as ZoomMapAdapter<ZoomMapViewHolder>
    }

    companion object {
        const val TAG = "MapFragment"

        private const val IMAGE_WIDTH = 4086
        private const val IMAGE_HEIGHT = 4086

        val instance: MapFragment by lazy { MapFragment() }
    }
}
