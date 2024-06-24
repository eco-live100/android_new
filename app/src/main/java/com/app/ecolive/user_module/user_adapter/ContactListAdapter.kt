package com.app.ecolive.user_module.user_adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.*
import com.app.ecolive.payment_module.model.Contact
import java.util.Locale


class ContactListAdapter(var list: ArrayList<Contact>,var mContactFilterLst:ArrayList<Contact>, var clickListern:  ClickListener) :
    RecyclerView.Adapter<ContactListAdapter.ViewHolder>(), Filterable {

    inner class ViewHolder(itemView : RowContactlistBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowContactlistBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding: RowContactlistBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_contactlist, parent, false)
            return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

         holder.binding.tvUserName.text = mContactFilterLst[position].name ?: ""
        holder.binding.tvSubTitle.text=mContactFilterLst[position].getFormattedMobile()
        holder.binding.ivUserImage.setImageResource(R.drawable.ic_user_blue)

        holder.binding.ivPhone.setOnClickListener {
            clickListern.onClick(mContactFilterLst[position])
        }

    }

    override fun getItemCount(): Int {
        return mContactFilterLst.size
    }

    fun update(phoneContacts: java.util.ArrayList<Contact>) {
        this.list = phoneContacts
        this.mContactFilterLst = phoneContacts
        notifyDataSetChanged()

    }

    interface ClickListener {
        fun onClick(data: Contact)

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                mContactFilterLst = if (charString.isEmpty()) {
                    list
                } else {
                    val filteredList: ArrayList<Contact> = ArrayList()
                    for (contactItem in list) {
                        if (contactItem.name?.toLowerCase()
                                ?.contains(charString.lowercase(Locale.getDefault()))
                            == true || contactItem.mobile?.contains(charString.lowercase(Locale.getDefault())) == true
                        ) {
                            filteredList.add(contactItem)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = mContactFilterLst
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {

                mContactFilterLst = (filterResults.values as? ArrayList<Contact>)!!
                notifyDataSetChanged()
            }
        }
    }

}

