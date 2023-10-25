package com.app.ecolive.taximodule.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowVehicleListBinding
import com.app.ecolive.taximodule.VehicalListActivity
import com.app.ecolive.taximodule.model.VehicleModel

class VehicleAdapter(var context: Context, var data: List<VehicleModel.Data>):
    RecyclerView.Adapter<VehicleAdapter.ViewHolder>() {
    inner class ViewHolder(itemView : RowVehicleListBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowVehicleListBinding = itemView

    }

    var selectedPosition = 0
    var animZoomIn: Animation? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        animZoomIn = AnimationUtils.loadAnimation(context, R.anim.zoom_in)
        val binding: RowVehicleListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_vehicle_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.binding.let {
            var item = data[position]
            if (position == selectedPosition) {
                VehicalListActivity().getInstance()?.selectedVehicleData(item)
                it.cardView.startAnimation(animZoomIn)
                it.cardView.setCardBackgroundColor(Color.parseColor("#DAE6F9"))
            } else {
                it.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            }

            viewHolder.itemView.setOnClickListener { v: View? ->
                selectedPosition = position
                VehicalListActivity().getInstance()?.selectedVehicleData(item)
                notifyDataSetChanged()
            }


            it.Amount.text =item.amount.toString()
            it.vehicalName.text =item.taxiCategory.toString()
            it.dropOff.text =item.dropOffAt.toString()
            it.fuleType.text =item.taxiType.toString()
            it.color.setBackgroundColor(Color.parseColor(item.vehicleTypeColor))
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }
}