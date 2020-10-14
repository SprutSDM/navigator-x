package ru.zakoulov.navigatorx.ui.buildingpicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_pick_building_bottom_sheet.recycler_view
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.data.Building
import ru.zakoulov.navigatorx.state.State
import ru.zakoulov.navigatorx.ui.MainViewModel

class BuildingPickerFragment : BottomSheetDialogFragment(), BuildingPickerCallbacks {

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var buildingAdapter: BuildingPickerAdapter
    private lateinit var buildingLayoutManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pick_building_bottom_sheet, container, false)
    }

    override fun getTheme() = R.style.AppTheme_BottomSheet

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildingAdapter = BuildingPickerAdapter(emptyList(), this)
        buildingLayoutManager = LinearLayoutManager(requireContext())
        recycler_view.apply {
            adapter = buildingAdapter
            layoutManager = buildingLayoutManager
        }

        lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    is State.Map -> buildingAdapter.buildings = it.mapState.buildings
                    else -> dismiss()
                }
            }
        }
    }

    override fun onBuildingPicked(building: Building) {
        viewModel.selectBuilding(building)
        dismiss()
    }
}
