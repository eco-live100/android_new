package com.app.ecolive.pharmacy_module.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowPharmacyListBinding
import com.app.ecolive.pharmacy_module.health_profile.PharmacyListActivity
import com.app.ecolive.pharmacy_module.model.PharmacyData
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.Utils.Companion.showMessage
import com.bumptech.glide.Glide
import java.util.Locale


class PharmacyListAdapter(var context: Context, var pharmacyList: ArrayList<PharmacyData>) :
    RecyclerView.Adapter<PharmacyListAdapter.ViewHolder>(), Filterable {


    private var originalTagListData: ArrayList<PharmacyData> = ArrayList()

    init {
        originalTagListData = pharmacyList
    }

    inner class ViewHolder(itemView: RowPharmacyListBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: RowPharmacyListBinding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowPharmacyListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.row_pharmacy_list, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = pharmacyList[position]
        viewHolder.binding.chatTitle.text = item.pharmacyName
        "Timing : ${item.openingFrom}-${item.openingTo}".also {
            viewHolder.binding.servicesTv.text = it
        }
        viewHolder.binding.locationTv.text = item.location
        Glide.with(context).load(AppConstant.BASE_URL_Image+item.pharmacyImage).into(viewHolder.binding.chatImage)
        viewHolder.itemView.setOnClickListener {
            AlertDialog.Builder(context).setTitle("Order")
                .setMessage("Are you sure you want to place order?")
                .setPositiveButton("ok") { dialog, which ->
                    PharmacyListActivity().getInstance().placeOrderApi(item._id)
                    showMessage(context, "Order placed successfully")
                }.setNegativeButton(android.R.string.no, null).show()
        }
    }

    override fun getItemCount(): Int {
        return pharmacyList.size
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
                    val filterResultsData = ArrayList<PharmacyData>()
                    for (data in pharmacyList) {
                        //In this loop, you'll filter through originalData and compare each item to charSequence.
                        //If you find a match, add it to your new ArrayList
                        //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional
                        if (data.pharmacyName.lowercase(Locale.ROOT).contains(
                                charSequence.toString().lowercase(Locale.ROOT)
                            ) || data.mobileNumber.lowercase(Locale.ROOT)
                                .contains(charSequence.toString().lowercase(Locale.ROOT))
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
                pharmacyList = filterResults.values as ArrayList<PharmacyData>
                notifyDataSetChanged()
            }
        }
    }

}

