package com.app.ecolive.taximodule.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.databinding.FragmentActivityBinding
import com.app.ecolive.localmodel.MyOrderListModel
import com.app.ecolive.rider_module.TrackingWithProgressActivity
import com.app.ecolive.user_module.user_adapter.UserMyOrderList2Adapter
import com.app.ecolive.utils.Utils


class ActivityFragment : Fragment() {

        lateinit var binding:FragmentActivityBinding
    lateinit var adapter: UserMyOrderList2Adapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =FragmentActivityBinding.inflate(inflater, container, false)
        binding.toolbar.toolbarTitle.text ="User activities"
        binding.toolbar.ivBack.visibility =View.INVISIBLE
        productList()
        return binding.root


    }
    private fun productList() {
        val arrayList = ArrayList<MyOrderListModel>()
        var item = MyOrderListModel("(4.1)","Relish analogue men's watch","Delivered on wed, oct 26th","$50.2",resources.getDrawable(R.drawable.product_image1))
        arrayList.add(item)
        item = MyOrderListModel("(3.8)","The best Beats headphones","Delivered on wed, oct 26th","$50.2",resources.getDrawable(R.drawable.apple_watch_white))
        arrayList.add(item)
        item = MyOrderListModel("(4.1)","Black office chair","Delivered on wed, oct 26th","$50.2",resources.getDrawable(R.drawable.apple_watch))
        arrayList.add(item)
        item = MyOrderListModel("(4.1)","Lunch box","Delivered on wed, oct 26th","$50.2",resources.getDrawable(R.drawable.apple_watch_white))
        arrayList.add(item)
        item = MyOrderListModel("(4.1)","Relish analogue men's watch","Delivered on wed, oct 26th","$50.2",resources.getDrawable(R.drawable.product_image3))
        arrayList.add(item)
        binding.recyclerviewMyOrder.layoutManager = LinearLayoutManager(requireContext())
        adapter = UserMyOrderList2Adapter(requireContext(), arrayList,object : UserMyOrderList2Adapter.ClickListener{
            override fun onClick(pos: Int) {
                startActivity(Intent(requireContext(), TrackingWithProgressActivity::class.java))
            }
        })
        binding.recyclerviewMyOrder.adapter = adapter

    }

}