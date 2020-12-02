package ru.zakoulov.navigatorx.ui.buildingpicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import ru.zakoulov.navigatorx.R
import ru.zakoulov.navigatorx.data.Building

class BuildingPickerAdapter(
    buildings: List<Building>,
    private val callbacks: BuildingPickerCallbacks
) : RecyclerView.Adapter<BuildingPickerAdapter.ViewHolder>() {

    var buildings: List<Building> = buildings
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.element_building, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val building = buildings[position]
        holder.setup(building) {
            callbacks.onBuildingPicked(building)
        }
    }

    override fun getItemCount() = buildings.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val buildingImage: ImageView = view.findViewById<AppCompatImageView>(R.id.building_image).apply {
            clipToOutline = true
        }
        private val buildingTitle: TextView = view.findViewById(R.id.building_title)
        private val buildingDescription: TextView = view.findViewById(R.id.building_description)

        fun setup(building: Building, onClickListener: View.OnClickListener) {
            buildingTitle.text = building.title
            buildingDescription.text = building.address
            itemView.setOnClickListener(onClickListener)
        }
    }
}
