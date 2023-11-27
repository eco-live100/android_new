package com.app.ecolive.msg_module.adapter
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowDoctorListBinding
import com.app.ecolive.pharmacy_module.PrescriptionRequestActivity
import com.app.ecolive.pharmacy_module.model.DoctorListModel
import java.util.Locale


class DoctorListAdapter(var context: Context, var doctorList: ArrayList<DoctorListModel.Data>) :
    RecyclerView.Adapter<DoctorListAdapter.ViewHolder>(), Filterable {


    private var originalTagListData: ArrayList<DoctorListModel.Data> = ArrayList()
    init {
        originalTagListData = doctorList
    }
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

    override fun onBindViewHolder(viewHolder: DoctorListAdapter.ViewHolder, position: Int) {
        val item = doctorList[position]
        viewHolder.binding.chatTitle.text = item.fullName
        viewHolder.itemView.setOnClickListener {
            context.startActivity(
                Intent(context, PrescriptionRequestActivity::class.java)
                    .putExtra("doctorName","${item.fullName}")
                    .putExtra("doctorProfile","${item.fullName}")
            )
        }
    }

    override fun getItemCount(): Int {
        return doctorList.size
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
    }

}

