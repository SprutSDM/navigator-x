package ru.zakoulov.navigatorx.ui.map

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.otaliastudios.zoom.ZoomMap
import com.otaliastudios.zoom.ZoomMapAdapter
import com.otaliastudios.zoom.ZoomMapViewHolder
import kotlinx.android.synthetic.main.element_room.room_title
import kotlinx.android.synthetic.main.element_room_description.view.room_description_image
import kotlinx.android.synthetic.main.element_room_description.view.room_description_text
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
import kotlinx.android.synthetic.main.room_info_bottom_sheet.room_info_department
import kotlinx.android.synthetic.main.room_info_bottom_sheet.room_info_description
import kotlinx.android.synthetic.main.room_info_bottom_sheet.room_info_equipment
import kotlinx.android.synthetic.main.room_info_bottom_sheet.room_info_group
import kotlinx.android.synthetic.main.room_info_bottom_sheet.room_info_place
import kotlinx.android.synthetic.main.room_picker_bottom_sheet.bottom_sheet_room_picker_info
import kotlinx.android.synthetic.main.room_picker_bottom_sheet.could_not_find_rooms
import kotlinx.android.synthetic.main.room_picker_bottom_sheet.input_room
import kotlinx.android.synthetic.main.room_picker_bottom_sheet.picker_cancel_button
import kotlinx.android.synthetic.main.room_picker_bottom_sheet.room_picker_recycler_view
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.data.Marker
import ru.zakoulov.navigatorx.viewmodel.MapState
import ru.zakoulov.navigatorx.viewmodel.State
import ru.zakoulov.navigatorx.viewmodel.MainViewModel
import ru.zakoulov.navigatorx.ui.buildingpicker.BuildingPickerFragment
import ru.zakoulov.navigatorx.ui.hideKeyboard
import ru.zakoulov.navigatorx.ui.map.markers.MarkerAdapter
import ru.zakoulov.navigatorx.ui.map.markers.MarkerCallbacks
import ru.zakoulov.navigatorx.ui.map.roompicker.RoomPickerAdapter
import ru.zakoulov.navigatorx.ui.map.roompicker.RoomPickerCallbacks
import ru.zakoulov.navigatorx.ui.setTintColor
import ru.zakoulov.navigatorx.ui.showKeyboardFor
import ru.zakoulov.navigatorx.viewmodel.Event
import ru.zakoulov.navigatorx.viewmodel.core.modelWatcher

class MapFragment : Fragment(R.layout.fragment_map), MarkerCallbacks, RoomPickerCallbacks {

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var navigationBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var roomInfoBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var roomPickerBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var roomPickerRoomInfo: TextView
    private lateinit var roomPickerImage: AppCompatImageView

    lateinit var markerAdapter: MarkerAdapter
    lateinit var roomPickerAdapter: RoomPickerAdapter

    private val pathMapper = PathMapper()

    val mapWatcher = modelWatcher<State.Map> {
        watch(State.Map::floorPaths) { floorPaths ->
            if (floorPaths == null) {
                zoom_layout.resetPaths()
            } else {
                zoom_layout.resetPaths()
                floorPaths.forEach {
                    zoom_layout.addPath(pathMapper.mapPathInfo(it))
                }
                zoom_layout.animatePaths()
            }
        }
        (State.Map::floor or State.Map::selectedBuilding) {
            it.selectedBuilding.floorsBitmapRes.getOrNull(it.floor - 1)?.also { backgroundRes ->
                val target = object : CustomViewTarget<ZoomMap, Bitmap>(zoom_layout) {
                    override fun onLoadFailed(errorDrawable: Drawable?) = Unit

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        zoom_layout.setBackground(resource)
                    }

                    override fun onResourceCleared(placeholder: Drawable?) = Unit
                }
                Glide
                    .with(requireContext())
                    .asBitmap()
                    .load(backgroundRes)
                    .dontAnimate()
                    .apply(RequestOptions().override(2048, 2048))
                    .into(target)
            }
        }
        watch(State.Map::markers) { markers ->
            markerAdapter.data = markers
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        markerAdapter = MarkerAdapter(emptyList(), this)
        zoom_layout.adapter = markerAdapter as ZoomMapAdapter<ZoomMapViewHolder>
        roomPickerAdapter = RoomPickerAdapter(emptyList(), this)
        val roomPickerLayoutManager = LinearLayoutManager(requireContext())
        room_picker_recycler_view.apply {
            adapter = roomPickerAdapter
            layoutManager = roomPickerLayoutManager
        }
        view.post {
            zoom_layout.moveToCenter(1f, animate = false)
        }

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
        roomPickerRoomInfo = bottom_sheet_room_info.findViewById(R.id.marker_text)
        roomPickerImage = bottom_sheet_room_info.findViewById(R.id.room_image)

        room_info_place.room_description_image.setImageResource(R.drawable.ic_description_place)
        room_info_department.room_description_image.setImageResource(R.drawable.ic_description_department)
        room_info_description.room_description_image.setImageResource(R.drawable.ic_description_info)

        lifecycleScope.launch {
            viewModel.events.collect {
                when (it) {
                    is Event.NoPathFound -> {
                        Toast.makeText(context, getString(R.string.path_was_not_found), Toast.LENGTH_SHORT).show()
                    }
                    is Event.FocusOn -> {
                        zoom_layout.post {
                            zoom_layout.scrollTo(
                                zoom = 2.5f,
                                x = it.marker.positionX,
                                y = it.marker.positionY,
                                animate = true
                            )
                        }
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
                        mapWatcher(it)
                        val uiFloorNumber = floor_number.text.toString().toInt()
                        when {
                            it.floor < uiFloorNumber -> {
                                runFloorDownAnimation(it.floor)
                            }
                            it.floor > uiFloorNumber -> {
                                runFloorUpAnimation(it.floor)
                            }
                        }
                        up_floor_arrow.setTintColor(
                            if (it.floor == it.selectedBuilding.floors) {
                                android.R.color.darker_gray
                            } else {
                                android.R.color.black
                            }
                        )
                        down_floor_arrow.setTintColor(
                            if (it.floor == 1) {
                                android.R.color.darker_gray
                            } else {
                                android.R.color.black
                            }
                        )
                        input_departure_room.apply {
                            it.departureMarker?.let { marker ->
                                text = when (marker) {
                                    is Marker.Room -> marker.roomNumber
                                    is Marker.Entrance -> marker.labelText
                                    is Marker.Toilet -> "WC"
                                    else -> ""
                                }
                                setTextColor(ContextCompat.getColor(context, android.R.color.black))
                            } ?: run {
                                setText(R.string.departure)
                                setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                            }
                        }
                        input_destination_room.apply {
                            it.destinationMarker?.let { marker ->
                                text = when (marker) {
                                    is Marker.Room -> marker.roomNumber
                                    is Marker.Entrance -> marker.labelText
                                    is Marker.Toilet -> "WC"
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
                                building_title.text = it.selectedBuilding.title
                                hideKeyboard()
                                input_room.setText("")
                            }
                            is MapState.MarkerSelected -> {
                                navigationBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                                roomPickerBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                                if (roomInfoBottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                                    roomInfoBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                                }
                                when (val marker = mapState.selectedMarker) {
                                    is Marker.Room -> {
                                        roomPickerRoomInfo.text = marker.roomNumber
                                        room_title.text = marker.roomInfo.name
                                        roomPickerImage.setImageResource(R.drawable.ic_place)

                                        room_info_group.visibility = View.VISIBLE
                                        room_info_place.room_description_text.text = "${marker.floor} Этаж"
                                        room_info_department.room_description_text.text = marker.roomInfo.departmentName ?: "Нет информации"
                                        room_info_description.room_description_text.text = marker.roomInfo.name ?: "Нет информации"
                                        room_info_equipment.removeAllViews()
                                        marker.roomInfo.equipment.forEach { equipment ->
                                            val view = layoutInflater.inflate(R.layout.element_room_equipment, room_info_equipment, false)
                                            (view as TextView).text = equipment
                                            room_info_equipment.addView(view)
                                        }
                                    }
                                    is Marker.Toilet -> {
                                        roomPickerRoomInfo.text = when (marker.type) {
                                            Marker.Toilet.Type.MALE -> "Мужской туалет"
                                            Marker.Toilet.Type.FEMALE -> "Женский туалет"
                                            Marker.Toilet.Type.COMBINED -> "Туалет"
                                        }
                                        room_title.text = "Туалет"
                                        roomPickerImage.setImageResource(R.drawable.ic_toilet)
                                        room_info_group.visibility = View.GONE
                                    }
                                    is Marker.Entrance -> {
                                        roomPickerRoomInfo.text = marker.labelText
                                        room_title.text = "Вход"
                                        roomPickerImage.setImageResource(R.drawable.ic_entrance)
                                        room_info_group.visibility = View.GONE
                                    }
                                    else -> {
                                        room_title.text = ""
                                        roomPickerRoomInfo.text = ""
                                        roomPickerImage.setImageResource(0)
                                        room_info_group.visibility = View.GONE
                                    }
                                }
                                input_room.setText("")
                                hideKeyboard()
                            }
                            is MapState.RoomPicking -> {
                                navigationBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                                roomPickerBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                                roomPickerAdapter.markers = mapState.filteredRooms
                                could_not_find_rooms.visibility = if (mapState.filteredRooms.isEmpty()) {
                                    View.VISIBLE
                                } else {
                                    View.GONE
                                }
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
            viewModel.openDepartureRoomPicker()
        }
        input_destination_room.setOnClickListener {
            viewModel.openDestinationRoomPicker()
        }

        button_select_as_destination.setOnClickListener {
            viewModel.onRoomSelectedAsDestination()
        }
        button_select_as_departure.setOnClickListener {
            viewModel.onRoomSelectedAsDeparture()
        }
        input_room.addTextChangedListener {
            viewModel.onRoomPickerTextUpdated(it?.toString() ?: "")
        }
        picker_cancel_button.setOnClickListener {
            viewModel.onPickerCanceled()
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

    private fun runFloorUpAnimation(newFloorNumber: Int) {
        val animationFloorToDown = AnimatorInflater.loadAnimator(context, R.animator.animator_floor_to_down) as AnimatorSet
        val animationFloorFromUp = AnimatorInflater.loadAnimator(context, R.animator.animator_floor_from_up) as AnimatorSet
        animationFloorFromUp.setTarget(floor_number)
        animationFloorToDown.apply {
            setTarget(floor_number)
            addListener(onEnd = {
                floor_number.text = newFloorNumber.toString()
                animationFloorFromUp.start()
            })
            start()
        }
    }

    private fun runFloorDownAnimation(newFloorNumber: Int) {
        val animationFloorToUp = AnimatorInflater.loadAnimator(context, R.animator.animator_floor_to_up) as AnimatorSet
        val animationFloorFromDown = AnimatorInflater.loadAnimator(context, R.animator.animator_floor_from_down) as AnimatorSet
        animationFloorFromDown.setTarget(floor_number)
        animationFloorToUp.apply {
            setTarget(floor_number)
            addListener(onEnd = {
                floor_number.text = newFloorNumber.toString()
                animationFloorFromDown.start()
            })
            start()
        }
    }

    override fun onMarkerSelected(marker: Marker) {
        when (marker) {
            is Marker.Room, is Marker.Toilet, is Marker.Entrance -> viewModel.onMarkerSelected(marker)
            else -> viewModel.onOutsideClick()
        }
    }

    override fun onRoomPicked(marker: Marker) {
        Log.d(TAG, "onRoomPicked: marker: ${marker}")
        viewModel.onRoomPickerSelected(marker)
    }

    companion object {
        const val TAG = "MapFragment"
    }
}
