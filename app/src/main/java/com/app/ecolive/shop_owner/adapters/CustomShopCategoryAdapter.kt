package com.app.ecolive.shop_owner.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.app.ecolive.R
import com.app.ecolive.localmodel.VehicleTypeListModel

class CustomShopCategoryAdapter(val context: Context, var dataSource: ArrayList<VehicleTypeListModel>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.custom_spinner_item, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        vh.label.text = dataSource[position].title

        when (dataSource[position].type) {
            "0" -> {
                vh.cardView.visibility=View.GONE
            }
            "1" -> {
                vh.cardView.visibility=View.VISIBLE
                vh.cardView.setCardBackgroundColor(context.resources.getColor(R.color.color_pedestrian))
            }
            "2" -> {
                vh.cardView.visibility=View.VISIBLE
                vh.cardView.setCardBackgroundColor(context.resources.getColor(R.color.color_ev))
            }
            "3" -> {
                vh.cardView.visibility=View.VISIBLE
                vh.cardView.setCardBackgroundColor(context.resources.getColor(R.color.color_motorcycles))
            }
            "4" -> {
                vh.cardView.visibility=View.VISIBLE
                vh.cardView.setCardBackgroundColor(context.resources.getColor(R.color.color_gas))
            }
        }


        return view
    }

    override fun getItem(position: Int): Any? {
        return dataSource[position];
    }

    override fun getCount(): Int {
        return dataSource.size;
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    private class ItemHolder(row: View?) {
        val label: TextView
        val cardView: CardView

        init {
            label = row?.findViewById(R.id.text) as TextView
            cardView = row?.findViewById(R.id.cardView) as CardView
        }
    }

}
