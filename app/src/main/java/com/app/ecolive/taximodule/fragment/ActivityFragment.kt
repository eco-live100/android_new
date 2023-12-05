package com.app.ecolive.taximodule.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.databinding.FragmentActivityBinding
import com.app.ecolive.service.Status
import com.app.ecolive.taximodule.TrackingWithProgressActivity
import com.app.ecolive.taximodule.adapter.TaxiBookingRequestListAdapter
import com.app.ecolive.taximodule.taxiViewModel.TaxiViewModel
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import org.json.JSONObject


class ActivityFragment : Fragment() {

    lateinit var binding: FragmentActivityBinding
    lateinit var adapter: TaxiBookingRequestListAdapter
    private val progressDialog = CustomProgressDialog()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentActivityBinding.inflate(inflater, container, false)
        binding.toolbar.toolbarTitle.text = "User activities"
        binding.toolbar.ivBack.visibility = View.GONE

        binding.toolbar.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.recyclerviewMyOrder.layoutManager = LinearLayoutManager(requireContext())
        getTaxiBookingRequestList()
        return binding.root


    }

    private fun getTaxiBookingRequestList() {
        progressDialog.show(requireContext())
        var viewModel = TaxiViewModel(requireActivity())
        var json = JSONObject()

        viewModel.getTaxiBookingRequestList(json).observe(requireActivity()) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        adapter = TaxiBookingRequestListAdapter(
                            requireContext(),
                            it.data,
                            object : TaxiBookingRequestListAdapter.ClickListener {
                                override fun onClick(pos: Int) {
                                    Intent(requireContext(), TrackingWithProgressActivity::class.java).also { intent->
                                        intent.putExtra(AppConstant.trackOrderDetail, it.data[pos])
                                        startActivity(intent)
                                    }
                                }
                            })
                        binding.recyclerviewMyOrder.adapter = adapter
                    }

                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    //var msg = JSONObject(it.message)
                    MyApp.popErrorMsg("", "" +vv, requireContext())
                    // MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }

}