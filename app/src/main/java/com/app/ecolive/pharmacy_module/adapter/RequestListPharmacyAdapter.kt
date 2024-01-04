package com.app.ecolive.pharmacy_module.adapter
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowRequestListPharmacyBinding
import com.app.ecolive.pharmacy_module.doctor.PrescriptionRequestSendByActivity
import com.app.ecolive.pharmacy_module.model.PrescriptionDataModel
import java.util.Locale


class RequestListPharmacyAdapter(var context: Context, var list: ArrayList<PrescriptionDataModel>) :
    RecyclerView.Adapter<RequestListPharmacyAdapter.ViewHolder>(), Filterable {


    private var originalTagListData: ArrayList<PrescriptionDataModel> = ArrayList()
    init {
        originalTagListData = list
    }
    inner class ViewHolder(itemView : RowRequestListPharmacyBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowRequestListPharmacyBinding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowRequestListPharmacyBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_request_list_pharmacy, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = list[position]
        viewHolder.binding.symptomsTv.text = item.symptomDescription
        viewHolder.binding.symptomsDurationTv.text = item.symptomDuration
        //viewHolder.binding.userLocationTv.text = item.location
        viewHolder.itemView.setOnClickListener {
            context.startActivity(Intent(context, PrescriptionRequestSendByActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return list.size
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
    }

}

