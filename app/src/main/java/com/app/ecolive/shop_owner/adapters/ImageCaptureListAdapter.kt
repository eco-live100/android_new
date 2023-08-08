package com.app.ecolive.shop_owner.adapters
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecolive.R
import com.app.ecolive.databinding.RowColorcodeBinding
import com.app.ecolive.databinding.RowImageBinding
import com.app.ecolive.databinding.RowProductListBinding
import com.app.ecolive.databinding.RowStoreListBinding
import com.app.ecolive.localmodel.SimilarProductListModel
import com.app.ecolive.shop_owner.model.ColorModel
import com.app.ecolive.shop_owner.model.ImageCaptureListModel
import com.app.ecolive.shop_owner.model.ShopListModel
import com.app.ecolive.utils.Utils


class ImageCaptureListAdapter(var context: Context, var dataList: ArrayList<ImageCaptureListModel>, var onClickListener: ClickListener) :
    RecyclerView.Adapter<ImageCaptureListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView : RowImageBinding)
        : RecyclerView.ViewHolder(itemView.root){
        var  binding : RowImageBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowImageBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_image, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.rowImageCapture.setImageBitmap(BitmapFactory.decodeFile(dataList[position].img))
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

