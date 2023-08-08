package com.app.ecolive.shop_owner.adapters
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowColorcodeBinding
import com.app.ecolive.databinding.RowProductListBinding
import com.app.ecolive.databinding.RowStoreListBinding
import com.app.ecolive.localmodel.SimilarProductListModel
import com.app.ecolive.shop_owner.model.ColorModel
import com.app.ecolive.shop_owner.model.ShopListModel
import com.app.ecolive.utils.Utils


class ColorCodeAdapter(var context: Context, var dataList: ArrayList<ColorModel>, var onClickListener: ClickListener) :
    RecyclerView.Adapter<ColorCodeAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowColorcodeBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowColorcodeBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowColorcodeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_colorcode, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // holder.binding.rowClrcodeText.text=dataList[position].colorCode
        holder.binding.rowClrcodeText.setBackgroundColor(Color.parseColor(dataList[position].colorCode))

        holder.itemView.setOnClickListener {
            onClickListener.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ClickListener {
        fun onClick(pos: Int)

    }
}

