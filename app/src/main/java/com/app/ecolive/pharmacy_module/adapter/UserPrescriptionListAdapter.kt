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
import com.app.ecolive.databinding.RowPrescriptionListBinding
import com.app.ecolive.pharmacy_module.health_profile.PrescribedMedicationsActivity
import com.app.ecolive.pharmacy_module.model.UserPrescriptionData
import com.app.ecolive.utils.Utils
import java.util.Locale


class UserPrescriptionListAdapter(var context: Context, private var userPrescriptionList: ArrayList<UserPrescriptionData>) :
    RecyclerView.Adapter<UserPrescriptionListAdapter.ViewHolder>(), Filterable {


    private var originalTagListData: ArrayList<UserPrescriptionData> = ArrayList()
    init {
        originalTagListData = userPrescriptionList
    }
    inner class ViewHolder(itemView : RowPrescriptionListBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowPrescriptionListBinding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowPrescriptionListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_prescription_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = userPrescriptionList[position]
        //viewHolder.binding.nameTv.text = item.fullName
        viewHolder.binding.symptomsTv.text = "Symptoms:- ${item.symptomDescription}"
        viewHolder.binding.symptomsDurationTv.text = "Sypmtoms Duration:-${item.symptomDuration}"
        viewHolder.binding.dateTimeTv.text = Utils.formatDateFromDateString(
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "hh:mm a, yyyy-MMM-dd",
            item.createdAt
        )
        viewHolder.itemView.setOnClickListener {
            context.startActivity(Intent(context, PrescribedMedicationsActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return userPrescriptionList.size
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
                    val filterResultsData: ArrayList<UserPrescriptionData> = ArrayList<UserPrescriptionData>()
                    for (data in userPrescriptionList) {
                        //In this loop, you'll filter through originalData and compare each item to charSequence.
                        //If you find a match, add it to your new ArrayList
                        //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional
                        if (data.symptomDescription.lowercase(Locale.ROOT).contains(charSequence.toString().lowercase(Locale.ROOT))) {
                            filterResultsData.add(data)
                        }
                    }
                    results.values = filterResultsData
                    results.count = filterResultsData.size
                }
                return results
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                userPrescriptionList = filterResults.values as ArrayList<UserPrescriptionData>
                notifyDataSetChanged()
            }
        }
    }

}

