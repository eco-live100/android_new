package com.app.ecolive.pharmacy_module.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.MedicineItemBinding
import com.app.ecolive.pharmacy_module.model.CommonMedicationModel
import java.util.Locale


class SearchMedicineListAdapter(var context: Context, private var searchMedicineList: ArrayList<CommonMedicationModel.Data>) :
    RecyclerView.Adapter<SearchMedicineListAdapter.ViewHolder>(), Filterable {


    private var originalTagListData: ArrayList<CommonMedicationModel.Data> = ArrayList()
    init {
        originalTagListData = searchMedicineList
    }
    inner class ViewHolder(itemView : MedicineItemBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : MedicineItemBinding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: MedicineItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.medicine_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = searchMedicineList[position]
        viewHolder.binding.medicineNameTv.text = item.name
        /*viewHolder.itemView.setOnClickListener {
            context.startActivity(
                Intent(context, PrescriptionRequestActivity::class.java)
                    .putExtra("doctorName","${item.fullName}")
                    .putExtra("doctorProfile","${item.fullName}")
            )
        }*/
    }

    override fun getItemCount(): Int {
        return searchMedicineList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val results = FilterResults()

                //If there's nothing to filter on, return the original data for your list
                if (charSequence == null || charSequence.isEmpty()) {
                    results.values = originalTagListData
                    results.count = originalTagListData.size
                } else {
                    val filterResultsData: ArrayList<CommonMedicationModel.Data> = ArrayList<CommonMedicationModel.Data>()
                    for (data in searchMedicineList) {
                        //In this loop, you'll filter through originalData and compare each item to charSequence.
                        //If you find a match, add it to your new ArrayList
                        //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional
                        if (data.name.lowercase(Locale.ROOT).contains(charSequence.toString().lowercase(Locale.ROOT))) {
                            filterResultsData.add(data)
                        }
                    }
                    results.values = filterResultsData
                    results.count = filterResultsData.size
                }
                return results
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                searchMedicineList = filterResults.values as ArrayList<CommonMedicationModel.Data>
                notifyDataSetChanged()
            }
        }
    }

}

