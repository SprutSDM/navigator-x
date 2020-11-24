package ru.zakoulov.navigatorx.ui.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.otaliastudios.zoom.ZoomMapAdapter
import com.otaliastudios.zoom.ZoomMapViewHolder
import kotlinx.android.synthetic.main.fragment_map.building_title
import kotlinx.android.synthetic.main.fragment_map.down_floor_arrow
import kotlinx.android.synthetic.main.fragment_map.floor_number
import kotlinx.android.synthetic.main.fragment_map.toolbar
import kotlinx.android.synthetic.main.fragment_map.up_floor_arrow
import kotlinx.android.synthetic.main.fragment_map.zoom_layout
import kotlinx.android.synthetic.main.navigation_bottom_sheet.bottom_sheet_navigation
import kotlinx.android.synthetic.main.navigation_bottom_sheet.input_departure_room
import kotlinx.android.synthetic.main.navigation_bottom_sheet.input_destination_room
import kotlinx.android.synthetic.main.room_info_bottom_sheet.bottom_sheet_room_info
import kotlinx.android.synthetic.main.room_info_bottom_sheet.button_select_as_departure
import kotlinx.android.synthetic.main.room_info_bottom_sheet.button_select_as_destination
import kotlinx.android.synthetic.main.room_picker_bottom_sheet.bottom_sheet_room_picker_info
import kotlinx.android.synthetic.main.room_picker_bottom_sheet.input_room
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.data.Marker
import ru.zakoulov.navigatorx.viewmodel.MapState
import ru.zakoulov.navigatorx.viewmodel.State
import ru.zakoulov.navigatorx.viewmodel.MainViewModel
import ru.zakoulov.navigatorx.ui.buildingpicker.BuildingPickerFragment
import ru.zakoulov.navigatorx.ui.hideKeyboard
import ru.zakoulov.navigatorx.ui.showKeyboardFor
import ru.zakoulov.navigatorx.viewmodel.Event
import ru.zakoulov.navigatorx.viewmodel.core.modelWatcher

class MapFragment : Fragment(R.layout.fragment_map), MarkerCallbacks {

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var navigationBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var roomInfoBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var roomPickerBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var roomPickerRoomInfo: TextView

//    private val rawMarkers = List(200) {
//        RawMarkerData(
//            id = it,
//            x = Random.nextInt(IMAGE_WIDTH),
//            y = Random.nextInt(IMAGE_HEIGHT),
//            label = Random.nextInt(100, 10000).toString()
//        )
//    }
//
//    private val map = Map(rawMarkers)

    lateinit var markerAdapter: MarkerAdapter

    val mapWatcher = modelWatcher<MapState> {
        watch(MapState::floorPaths) { floorPaths ->
            if (floorPaths == null) {
                zoom_layout.resetPaths()
            } else {
                zoom_layout.resetPaths()
                floorPaths.paths.forEach {
                    zoom_layout.addPath(it.path)
                }
                zoom_layout.animatePaths()
            }
        }
        watch(MapState::markers) { markers ->
            markerAdapter.data = markers
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        markerAdapter = MarkerAdapter(emptyList(), this)
        zoom_layout.adapter = markerAdapter as ZoomMapAdapter<ZoomMapViewHolder>
        roomInfoBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_room_info)
        navigationBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_navigation).apply {
            isDraggable = false
        }
        roomPickerBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_room_picker_info).apply {
            skipCollapsed = true
        }
        bottom_sheet_room_info.setOnClickListener {  }
        bottom_sheet_navigation.setOnClickListener {  }
        bottom_sheet_room_picker_info.setOnClickListener {  }
        zoom_layout.setOnOutsideClickListener {
            viewModel.onOutsideClick()
        }
        roomPickerRoomInfo = bottom_sheet_room_info.findViewById(R.id.room_number)
        lifecycleScope.launch {
            viewModel.events.collect {
                when (it) {
                    is Event.NoPathFound -> {
                        Toast.makeText(context, getString(R.string.path_was_not_found), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.state.collect {
                Log.d(TAG, "onViewCreated: state: ${it}")
                when (it) {
                    is State.Loading -> Unit
                    is State.Map -> {
                        mapWatcher(it.mapState)
                        floor_number.text = it.mapState.floor.toString()
                        up_floor_arrow.setColorFilter(
                            ContextCompat.getColor(
                                requireContext(),
                                if (it.mapState.floor == it.mapState.selectedBuilding.floors) {
                                    android.R.color.darker_gray
                                } else {
                                    android.R.color.black
                                }
                            )
                        )
                        down_floor_arrow.setColorFilter(
                            ContextCompat.getColor(
                                requireContext(),
                                if (it.mapState.floor == 1) {
                                    android.R.color.darker_gray
                                } else {
                                    android.R.color.black
                                }
                            )
                        )
                        input_departure_room.apply {
                            it.mapState.departureMarker?.let { marker ->
                                text = when (marker) {
                                    is Marker.Room -> marker.roomNumber
                                    else -> ""
                                }
                                setTextColor(ContextCompat.getColor(context, android.R.color.black))
                            } ?: run {
                                setText(R.string.departure)
                                setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                            }
                        }
                        input_destination_room.apply {
                            it.mapState.destinationMarker?.let { marker ->
                                text = when (marker) {
                                    is Marker.Room -> marker.roomNumber
                                    else -> ""
                                }
                                setTextColor(ContextCompat.getColor(context, android.R.color.black))
                            } ?: run {
                                setText(R.string.destination)
                                setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                            }
                        }
                        when (val mapState = it.mapState) {
                            is MapState.Viewing -> {
                                if (navigationBottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                                    navigationBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                                }
                                roomInfoBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                                roomPickerBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                                building_title.text = mapState.selectedBuilding.title
                            }
                            is MapState.MarkerSelected -> {
                                navigationBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                                roomPickerBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                                if (roomInfoBottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                                    roomInfoBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                                }
                                roomPickerRoomInfo.text = when (mapState.selectedMarker) {
                                    is Marker.Room -> "Ауд. ${mapState.selectedMarker.roomNumber}"
                                    else -> ""
                                }
                            }
                            is MapState.RoomPicking -> {
                                navigationBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                                roomPickerBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                                showKeyboardFor(input_room)
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
        roomPickerBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> viewModel.onRoomPickerBSClosed()
                    BottomSheetBehavior.STATE_DRAGGING -> hideKeyboard()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
        })

        input_departure_room.setOnClickListener {
            viewModel.pickDepartureRoom()
        }
        input_destination_room.setOnClickListener {
            viewModel.pickDestinationRoom()
        }

        button_select_as_destination.setOnClickListener {
            viewModel.onRoomSelectedAsDestination()
        }
        button_select_as_departure.setOnClickListener {
            viewModel.onRoomSelectedAsDeparture()
        }

        toolbar.setOnClickListener {
            val buildingPicker = BuildingPickerFragment()
            buildingPicker.show(requireActivity().supportFragmentManager, buildingPicker.tag)
        }

        up_floor_arrow.setOnClickListener {
            viewModel.onUpFloorSelected()
        }
        down_floor_arrow.setOnClickListener {
            viewModel.onDownFloorSelected()
        }
    }

    override fun onMarkerSelected(marker: Marker) {
        when (marker) {
            is Marker.Room -> viewModel.onMarkerSelected(marker)
        }
    }

    companion object {
        const val TAG = "MapFragment"

        private const val IMAGE_WIDTH = 4086
        private const val IMAGE_HEIGHT = 4086
    }
}
