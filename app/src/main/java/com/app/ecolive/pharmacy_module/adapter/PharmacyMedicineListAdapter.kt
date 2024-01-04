package com.app.ecolive.pharmacy_module.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowPharmacyMedicineListBinding
import com.app.ecolive.pharmacy_module.model.MedicineDataModel
import com.app.ecolive.utils.AppConstant
import com.bumptech.glide.Glide
import java.util.Locale


class PharmacyMedicineListAdapter(var context: Context, var list: ArrayList<MedicineDataModel>) :
    RecyclerView.Adapter<PharmacyMedicineListAdapter.ViewHolder>(), Filterable {


    private var originalTagListData: ArrayList<MedicineDataModel> = ArrayList()
    init {
        originalTagListData = list
    }
    inner class ViewHolder(itemView : RowPharmacyMedicineListBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowPharmacyMedicineListBinding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowPharmacyMedicineListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_pharmacy_medicine_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = list[position]
        viewHolder.binding.nameTv.text = item.medicineName
        viewHolder.binding.priceTv.text = "Price: ${item.price}"
        viewHolder.binding.quantityTv.text = "Stock: ${item.quantity}"
        Glide.with(context)
            .load("${AppConstant.BASE_URL_Image}${item.image}")
            .placeholder(R.drawable.doctor_bg).centerCrop()
            .into(viewHolder.binding.imageView)
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
                    val filterResultsData: ArrayList<MedicineDataModel> = ArrayList<MedicineDataModel>()
                    for (data in list) {
                        //In this loop, you'll filter through originalData and compare each item to charSequence.
                        //If you find a match, add it to your new ArrayList
                        //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional
                        if (data.medicineName.lowercase(Locale.ROOT).contains(charSequence.toString().lowercase(Locale.ROOT))
                            || data.price.lowercase(Locale.ROOT).contains(charSequence.toString().lowercase(Locale.ROOT))) {
                            filterResultsData.add(data)
                        }
                    }
                    results.values = filterResultsData
                    results.count = filterResultsData.size
                }
                return results
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                list = filterResults.values as ArrayList<MedicineDataModel>
                notifyDataSetChanged()
            }
        }
    }

}

