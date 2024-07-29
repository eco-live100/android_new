package com.app.ecolive.payment_module.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowUserListBinding
import com.app.ecolive.payment_module.model.Contact
import com.app.ecolive.payment_module.model.Data
import com.app.ecolive.utils.PreferenceKeeper
import java.util.Locale


class UserListForAdapter(var list: ArrayList<Data>,var mUserFilterLst:ArrayList<Data>, var clickListern:ClickListener) :
    RecyclerView.Adapter<UserListForAdapter.ViewHolder>(),Filterable {

    inner class ViewHolder(itemView : RowUserListBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowUserListBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowUserListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_user_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.userNameTxt.text =
            "${mUserFilterLst[position].firstName ?: ""} ${mUserFilterLst[position].lastName ?: ""}"
        holder.binding.userPhoneTxt.text =mUserFilterLst[position].mobileNumber
        if (!mUserFilterLst[position].firstName.isNullOrEmpty()){
             holder.binding.nameLogo.text =mUserFilterLst[position].firstName.substring(0,1)
        }

        if (PreferenceKeeper.instance.loginResponse?._id.equals(mUserFilterLst[position]._id)){
             holder.itemView.rootView.visibility =View.GONE
            holder.itemView.layoutParams =
                RecyclerView.LayoutParams(0, 0)
        }
        holder.binding.mainView.setOnClickListener {
            clickListern.onClick(mUserFilterLst[position])
        }
    }

    override fun getItemCount(): Int {
        return mUserFilterLst.size
    }

    interface ClickListener {
        fun onClick(pos: Data)

    }
    fun update(userlist: java.util.ArrayList<Data>) {
        this.list = userlist
        this.mUserFilterLst = userlist
        notifyDataSetChanged()

    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                mUserFilterLst = if (charString.isEmpty()) {
                    list
                } else {
                    val filteredList: ArrayList<Data> = ArrayList()
                    for (contactItem in list) {
                        if (contactItem.firstName?.toLowerCase()
                                ?.contains(charString.lowercase(Locale.getDefault()))
                            == true || contactItem.mobileNumber?.contains(charString.lowercase(Locale.getDefault())) == true
                        ) {
                            filteredList.add(contactItem)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = mUserFilterLst
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {

                mUserFilterLst = (filterResults.values as? ArrayList<Data>)!!
                notifyDataSetChanged()
            }
        }
    }

}

