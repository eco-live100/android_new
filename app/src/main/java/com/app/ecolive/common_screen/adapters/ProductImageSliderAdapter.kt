package com.app.ecolive.common_screen.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.app.ecolive.R
import com.app.ecolive.localmodel.PropertyImageListModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener


class ProductImageSliderAdapter(
    internal var context: Context,
    internal var itemList: List<PropertyImageListModel>
) : PagerAdapter() {

    internal var mLayoutInflater: LayoutInflater
    private var pos = 0

    init {
        mLayoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val holder = ViewHolder()
        val itemView =
            mLayoutInflater.inflate(R.layout.list_layout_product_image_slider, container, false)
        holder.itemImage = itemView.findViewById(R.id.img_slider)
        holder.progressBar = itemView.findViewById(R.id.progressBar)
        holder.sliderItem = this.itemList[position]
        Glide.with(context).load(holder.sliderItem.image).listener(object :
            RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                holder.progressBar.visibility = View.GONE
                return false
            }
            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                holder.progressBar.visibility = View.GONE
                return false
            }
        }).into(holder.itemImage)


        (container as ViewPager).addView(itemView)
        return itemView
    }


    internal class ViewHolder {
        lateinit var sliderItem: PropertyImageListModel
        lateinit var itemImage: ImageView
        lateinit var progressBar: ProgressBar
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }

    override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
        return arg0 === arg1 as View
    }

    override fun destroyItem(arg0: View, arg1: Int, arg2: Any) {
        (arg0 as ViewPager).removeView(arg2 as View)
    }
    interface ClickListener {
        fun onClick(pos: Int)
    }
}