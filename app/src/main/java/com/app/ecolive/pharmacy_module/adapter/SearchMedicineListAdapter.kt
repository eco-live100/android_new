package com.app.ecolive.pharmacy_module.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.MedicineItemBinding
import com.app.ecolive.pharmacy_module.health_profile.CreateHealthActivity
import com.app.ecolive.pharmacy_module.model.SearchMedicineListData


class SearchMedicineListAdapter(
    var context: Context,
    private var searchMedicineList: ArrayList<SearchMedicineListData>
) :
    RecyclerView.Adapter<SearchMedicineListAdapter.ViewHolder>() {


    private var originalTagListData: ArrayList<SearchMedicineListData> = ArrayList()

    init {
        originalTagListData = searchMedicineList
    }

    inner class ViewHolder(itemView: MedicineItemBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: MedicineItemBinding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: MedicineItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.medicine_item, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = searchMedicineList[position]
        Log.d("TAG", "fsadfsfsafsaf: ${item.medicineName}")
        viewHolder.binding.medicineNameTv.text = item.medicineName

        viewHolder.binding.root.setOnClickListener {
            Log.d("TAG", "fsadfsfsafsaf: ${item.medicineName}")
            CreateHealthActivity().getInstance()?.selectMedicine(item)
        }
    }

    override fun getItemCount(): Int {
        return searchMedicineList.size
    }

}

