package com.app.ecolive.taximodule.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowContactlistBinding
import com.app.ecolive.databinding.RowVehicleListBinding
import com.app.ecolive.taximodule.VehicalListActivity
import com.app.ecolive.taximodule.model.VehicleModel

class VehicleAdapter(var context: Context, var data: List<VehicleModel.Data>):
    RecyclerView.Adapter<VehicleAdapter.ViewHolder>() {
    inner class ViewHolder(itemView : RowVehicleListBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowVehicleListBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowVehicleListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_vehicle_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.let {
            it.Amount.text =data[position].amount.toString()
            it.vehicalName.text =data[position].taxiCategory.toString()
            it.dropOff.text =data[position].dropOffAt.toString()
            it.fuleType.text =data[position].taxiType.toString()
            it.color.setBackgroundColor(Color.parseColor(data[position].vehicleTypeColor))
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }
}