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
import com.app.ecolive.pharmacy_module.model.PharmacyOrderListModel
import com.app.ecolive.utils.AppConstant
import com.bumptech.glide.Glide
import java.util.Locale


class RequestListPharmacyAdapter(
    var context: Context,
    var list: ArrayList<PharmacyOrderListModel>
) :
    RecyclerView.Adapter<RequestListPharmacyAdapter.ViewHolder>(), Filterable {


    private var originalTagListData: ArrayList<PharmacyOrderListModel> = ArrayList()

    init {
        originalTagListData = list
    }

    inner class ViewHolder(itemView: RowRequestListPharmacyBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: RowRequestListPharmacyBinding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowRequestListPharmacyBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_request_list_pharmacy, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val item = list[position].precriptionDetails
        viewHolder.binding.symptomsTv.text = "Symptoms : "+item?.symptomDescription?.capitalize()
        viewHolder.binding.symptomsDurationTv.text = "Symptoms Duration : " + item?.symptomDuration
        viewHolder.binding.userLocationTv.text = "Allergies : " + item?.allergies?.capitalize()
        if(item?.user!=null) {
            val user = item.user
            viewHolder.binding.nameTv.text = "${user.firstName} ${user.lastName}".capitalize()
            Glide.with(context).load("${AppConstant.BASE_URL_Image}${user.profilePicture}")
                .placeholder(R.drawable.ic_user_blue).centerCrop()
                .into(viewHolder.binding.profileImage)
        }
        viewHolder.itemView.setOnClickListener {
            context.startActivity(
                Intent(context, PrescriptionRequestSendByActivity::class.java)
                    .putExtra(AppConstant.data,list[position])
                    .putExtra(AppConstant.fromScreen, "pharmacy")
            )
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
                    val filterResultsData = ArrayList<PharmacyOrderListModel>()
                    for (data in list) {
                        //In this loop, you'll filter through originalData and compare each item to charSequence.
                        //If you find a match, add it to your new ArrayList
                        //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional
                        if (data.precriptionDetails?.symptomDescription?.lowercase(Locale.ROOT)
                                ?.contains(charSequence.toString().lowercase(Locale.ROOT)) == true
                        ) {
                            filterResultsData.add(data)
                        }
                    }
                    results.values = filterResultsData
                    results.count = filterResultsData.size
                }
                return results
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                list = filterResults.values as ArrayList<PharmacyOrderListModel>
                notifyDataSetChanged()
            }
        }
    }

}

