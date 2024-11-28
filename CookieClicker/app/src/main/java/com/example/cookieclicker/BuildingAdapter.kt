package com.example.cookieclicker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cookieclicker.data.Building
import com.example.cookieclicker.databinding.ItemBuildingBinding

class BuildingsAdapter(
    private val onBuildingClick: (Building) -> Unit
) : RecyclerView.Adapter<BuildingsAdapter.BuildingViewHolder>() {

    private var buildingsList: List<Building> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuildingViewHolder {
        val binding = ItemBuildingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BuildingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BuildingViewHolder, position: Int) {
        val building = buildingsList[position]
        holder.bind(building)
    }

    override fun getItemCount(): Int = buildingsList.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: List<Building>) {
        buildingsList = newList
        notifyDataSetChanged()
    }

    inner class BuildingViewHolder(private val binding: ItemBuildingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(building: Building) {
            binding.name.text = building.name
            binding.count.text = building.count.toString()
            binding.cost.text = "${building.cost} 🍪"
            binding.icon.setImageResource(building.icon)
            binding.root.alpha = if (building.isAvailable) 1.0f else 0.5f
            binding.root.isClickable = building.isAvailable
            binding.root.setOnClickListener { onBuildingClick(building) }
        }
    }
}
