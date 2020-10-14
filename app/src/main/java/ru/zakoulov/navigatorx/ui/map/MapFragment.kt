package ru.zakoulov.navigatorx.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.otaliastudios.zoom.ZoomMapAdapter
import com.otaliastudios.zoom.ZoomMapViewHolder
import kotlinx.android.synthetic.main.fragment_map.building_title
import kotlinx.android.synthetic.main.fragment_map.toolbar
import kotlinx.android.synthetic.main.fragment_map.zoom_layout
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.map.Map
import ru.zakoulov.navigatorx.map.RawMarkerData
import ru.zakoulov.navigatorx.state.MapState
import ru.zakoulov.navigatorx.state.State
import ru.zakoulov.navigatorx.ui.MainViewModel
import ru.zakoulov.navigatorx.ui.buildingpicker.BuildingPickerFragment
import kotlin.random.Random

class MapFragment : Fragment(R.layout.fragment_map) {

    private val viewModel: MainViewModel by activityViewModels()

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

        lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    is State.Loading -> {

                    }
                    is State.Map -> {
                        val mapState = it.mapState
                        when (it.mapState) {
                            is MapState.Viewing -> {
                                building_title.text = mapState.selectedBuilding.title
                            }
                        }
                    }
                }
            }
        }
        toolbar.setOnClickListener {
            val buildingPicker = BuildingPickerFragment()
            buildingPicker.show(requireActivity().supportFragmentManager, buildingPicker.tag)
        }
    }

    companion object {
        const val TAG = "MapFragment"

        private const val IMAGE_WIDTH = 4086
        private const val IMAGE_HEIGHT = 4086

        val instance: MapFragment by lazy { MapFragment() }
    }
}
