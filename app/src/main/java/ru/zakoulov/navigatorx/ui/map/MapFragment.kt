package ru.zakoulov.navigatorx.ui.map

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.otaliastudios.zoom.ZoomMapAdapter
import com.otaliastudios.zoom.ZoomMapViewHolder
import kotlinx.android.synthetic.main.fragment_map.building_title
import kotlinx.android.synthetic.main.fragment_map.toolbar
import kotlinx.android.synthetic.main.fragment_map.zoom_layout
import kotlinx.android.synthetic.main.room_info_bottom_sheet.bottom_sheet_room_info
import kotlinx.android.synthetic.main.room_info_bottom_sheet.room_number
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.map.Map
import ru.zakoulov.navigatorx.map.Marker
import ru.zakoulov.navigatorx.map.RawMarkerData
import ru.zakoulov.navigatorx.state.MapState
import ru.zakoulov.navigatorx.state.State
import ru.zakoulov.navigatorx.ui.MainViewModel
import ru.zakoulov.navigatorx.ui.buildingpicker.BuildingPickerFragment
import kotlin.random.Random

class MapFragment : Fragment(R.layout.fragment_map), MarkerCallbacks {

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var roomInfoBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

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
        markerAdapter = MarkerAdapter(map.markers, this)
        zoom_layout.adapter = markerAdapter as ZoomMapAdapter<ZoomMapViewHolder>
        roomInfoBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_room_info)
        bottom_sheet_room_info.setOnClickListener {  }
        zoom_layout.setOnOutsideClickListener {
            Log.d(TAG, "onViewCreated: outsideclick")
            viewModel.onOutsideClick()
        }

        lifecycleScope.launch {
            viewModel.state.collect {
                Log.d(TAG, "onViewCreated: state: ${it}")
                when (it) {
                    is State.Loading -> {

                    }
                    is State.Map -> {
                        when (val mapState = it.mapState) {
                            is MapState.Viewing -> {
                                building_title.text = mapState.selectedBuilding.title
                                roomInfoBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                            }
                            is MapState.RoomSelected -> {
                                room_number.text = "Ауд. ${mapState.roomNumber}"
                                if (roomInfoBottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                                    roomInfoBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                                }
                            }
                        }
                    }
                }
            }
        }
        roomInfoBottomSheetBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    viewModel.onRoomInfoBSClosed()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
        })

        toolbar.setOnClickListener {
            val buildingPicker = BuildingPickerFragment()
            buildingPicker.show(requireActivity().supportFragmentManager, buildingPicker.tag)
        }
    }


    override fun onMarkerSelected(marker: Marker) {
        viewModel.onRoomSelected(marker.label)
    }

    companion object {
        const val TAG = "MapFragment"

        private const val IMAGE_WIDTH = 4086
        private const val IMAGE_HEIGHT = 4086

        val instance: MapFragment by lazy { MapFragment() }
    }
}
