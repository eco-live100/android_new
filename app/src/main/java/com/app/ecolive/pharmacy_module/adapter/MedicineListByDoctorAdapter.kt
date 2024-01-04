package com.app.ecolive.pharmacy_module.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowMedicineByDoctorBinding
import com.app.ecolive.pharmacy_module.model.PrescriptionMedicationData


class MedicineListByDoctorAdapter(var context: Context, var list: ArrayList<PrescriptionMedicationData>) :
    RecyclerView.Adapter<MedicineListByDoctorAdapter.ViewHolder>() {


    private var originalTagListData: ArrayList<PrescriptionMedicationData> = ArrayList()
    init {
        originalTagListData = list
    }
    inner class ViewHolder(itemView : RowMedicineByDoctorBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowMedicineByDoctorBinding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowMedicineByDoctorBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_medicine_by_doctor, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Log.d("TAG", "onBindViewHolder: $position")
        if (list.isNotEmpty()) {
            val item = list[position]
            viewHolder.binding.apply {
                if ((position + 1) / 2 == 0) {
                    itemLL.setBackgroundColor(context.resources.getColor(com.adevinta.leku.R.color.quantum_bluegrey100))
                } else {
                    itemLL.setBackgroundColor(context.resources.getColor(`in`.aabhasjindal.otptextview.R.color.transparent))
                }
                "Medication #${position + 1} ${item.medicineName}".also { nameTv.text = it }
                strengthTv.text = item.strength
                doseTv.text = item.dose
                routeTv.text = item.route
                frequencyTv.text = item.frequency
                refillTv.text = item.refills
                indicationTv.text = item.indication
                additionalDirectionTv.text = item.additionalDirection
            }
        }
    }

    override fun getItemCount(): Int {
        return if( list.size>0) list.size else 4
    }
/*
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val results = FilterResults()

                //If there's nothing to filter on, return the original data for your list
                if (charSequence == null || charSequence.isEmpty()) {
                    results.values = originalTagListData
                    results.count = originalTagListData.size
                } else {
                    val filterResultsData = ArrayList<PrescriptionDataModel>()
                    for (data in list) {
                        //In this loop, you'll filter through originalData and compare each item to charSequence.
                        //If you find a match, add it to your new ArrayList
                        //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional
                        if (data.symptomDuration.lowercase(Locale.ROOT).contains(charSequence.toString().lowercase(Locale.ROOT))) {
                            filterResultsData.add(data)
                        }
                    }
                    results.values = filterResultsData
                    results.count = filterResultsData.size
                }
                return results
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                list = filterResults.values as ArrayList<PrescriptionDataModel>
                notifyDataSetChanged()
            }
        }
    }*/

}

