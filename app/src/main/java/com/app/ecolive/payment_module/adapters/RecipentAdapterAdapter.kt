package com.app.ecolive.payment_module.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowDoctorListBinding


class RecipentAdapterAdapter(var context: Context, var clickListern:ClickListener) :
    RecyclerView.Adapter<RecipentAdapterAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowDoctorListBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowDoctorListBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowDoctorListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_doctor_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 10
    }

    interface ClickListener {
        fun onClick(pos: Int)

    }
}

