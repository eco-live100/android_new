package com.app.ecolive.shop_owner.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowProductListBinding
import com.app.ecolive.databinding.RowStoreListBinding
import com.app.ecolive.localmodel.SimilarProductListModel
import com.app.ecolive.shop_owner.model.ShopListModel
import com.app.ecolive.utils.Utils


class ShopListAdapter(var context: Context, var dataList: ArrayList<ShopListModel.Data>, var onClickListener: ClickListener) :
    RecyclerView.Adapter<ShopListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowStoreListBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowStoreListBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowStoreListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_store_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         holder.binding.homeStoreName.text=dataList[position].shopName
         holder.binding.homeStoreAddrs.text=dataList[position].storeAddress
        try {
            Utils.setImageFullPath(context,holder.binding.homeStoreLogo,dataList[position].storeLogo)
        } catch (e: Exception) {
            holder.binding.homeStoreLogo.setImageResource(R.drawable.app_logo_bgtrans)
        }
        holder.binding.homeStoreSetting.setOnClickListener{
            onClickListener.onSetting(position)
        }
        holder.itemView.setOnClickListener {
            onClickListener.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)
        fun onSetting(pos: Int)

    }
}

