package com.app.ecolive.pharmacy_module.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.ServiceItemBinding


class ServiceListAdapter(var context: Context, private var serviceList: ArrayList<String>) :
    RecyclerView.Adapter<ServiceListAdapter.ViewHolder>() /*,Filterable*/ {

/*
    private var originalTagListData: ArrayList<String> = ArrayList()
    init {
        originalTagListData = doctorList
    }*/
    inner class ViewHolder(itemView : ServiceItemBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : ServiceItemBinding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ServiceItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.service_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = serviceList[position]
        viewHolder.binding.serviceTv.text = item
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

/*    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val results = FilterResults()

                //If there's nothing to filter on, return the original data for your list
                if (charSequence == null || charSequence.isEmpty()) {
                    results.values = originalTagListData
                    results.count = originalTagListData.size
                } else {
                    val filterResultsData: ArrayList<DoctorListModel.Data> = ArrayList<DoctorListModel.Data>()
                    for (data in doctorList) {
                        //In this loop, you'll filter through originalData and compare each item to charSequence.
                        //If you find a match, add it to your new ArrayList
                        //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional
                        if (data.fullName.lowercase(Locale.ROOT).contains(charSequence.toString().lowercase(Locale.ROOT))
                            || data.mobileNumber.lowercase(Locale.ROOT).contains(charSequence.toString().lowercase(Locale.ROOT))) {
                            filterResultsData.add(data)
                        }
                    }
                    results.values = filterResultsData
                    results.count = filterResultsData.size
                }
                return results
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                doctorList = filterResults.values as ArrayList<DoctorListModel.Data>
                notifyDataSetChanged()
            }
        }
    }*/

}

