package com.app.ecolive.common_screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.app.ecolive.R
import com.app.ecolive.common_screen.fragment.DynamicFragment
import com.app.ecolive.databinding.ActivityGetStartedBinding
import com.app.ecolive.localmodel.SliderListModel
import com.app.ecolive.login_module.LoginActivity
import com.app.ecolive.login_module.model.IntroModel
import com.app.ecolive.service.Status
import com.app.ecolive.utils.*
import com.app.ecolive.viewmodel.CommonViewModel
import com.offercity.base.BaseActivity
import org.json.JSONObject

class GetStartedActivity : BaseActivity() {
    lateinit var binding: ActivityGetStartedBinding
  //  private var sliderList = ArrayList<SliderListModel>()
    private var sliderList = ArrayList<IntroModel.Data>()
    private lateinit var viewPager: ViewPager
    private lateinit var lytPageIndicator: LinearLayout
    private var currentIndex: Int = 0
    private val progressDialog = CustomProgressDialog()
    override fun onCreate(savedInstanceState: Bundle?) {
        statusBarColor()
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_get_started)
        initView()
        utilityAPICAll()
    }

    private fun utilityAPICAll() {
        progressDialog.show(THIS!!)
        var loginViewModel = CommonViewModel(THIS!!)
        var json = JSONObject()


        loginViewModel.introPage().observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        sliderList = ArrayList()
                        sliderList.addAll(it.data)
                        initView()

                    }

                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    MyApp.popErrorMsg("", "" + it.message, THIS!!)
                }
            }
        }
    }

    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.white)
        Utils.changeStatusTextColor(this)
    }

    private fun initView() {
        /*   var item = SliderListModel(
            "LOREM IPSUM",
            "Lorem dolor sit amet consectetur adipisicing elit, sed do eiusmod tempor incididunt ut ero labore et dolore",
            resources.getDrawable(R.drawable.slider1)
        )
        sliderList.add(item)
        item = SliderListModel(
            "LOREM IPSUM",
            "Lorem dolor sit amet consectetur adipisicing elit, sed do eiusmod tempor incididunt ut ero labore et dolore",
            resources.getDrawable(R.drawable.slider2)
        )
        sliderList.add(item)
        item = SliderListModel(
            "LOREM IPSUM",
            "Lorem dolor sit amet consectetur adipisicing elit, sed do eiusmod tempor incididunt ut ero labore et dolore",
            resources.getDrawable(R.drawable.slider3)
        )
        sliderList.add(item)*/

        viewPager = binding.viewPager
        lytPageIndicator = binding!!.lytPageIndicator

        val adapter = PlansPagerAdapter(supportFragmentManager, sliderList.size, sliderList)
        viewPager.adapter = adapter
        addPageIndicators()
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (position == sliderList.size - 1) {
                    binding.tvGetStarted.visibility = View.VISIBLE
                    binding.group.visibility = View.GONE

                } else {
                    binding.tvGetStarted.visibility = View.GONE
                    binding.group.visibility = View.VISIBLE
                }
                updatePageIndicator(position)
            }
        })

        binding.tvNext.setOnClickListener {
            var currentPage: Int = viewPager.currentItem
            //return to first page, if current page is last page
            //return to first page, if current page is last page
            if (currentPage == sliderList.size - 1) {
                currentPage = -1
            }
            viewPager.setCurrentItem(++currentPage, true)
        }

        binding.tvSkip.setOnClickListener { startActivity(Intent(this@GetStartedActivity, LoginActivity::class.java).putExtra(AppConstant.INTENT_EXTRAS.INTENT_TYPE,AppConstant.INTENT_VALUE.INTENT_TYPE_GET_STARTED)) }
        binding.tvGetStarted.setOnClickListener { startActivity(Intent(this@GetStartedActivity, LoginActivity::class.java).putExtra(AppConstant.INTENT_EXTRAS.INTENT_TYPE,AppConstant.INTENT_VALUE.INTENT_TYPE_GET_STARTED)) }
    }



    private class PlansPagerAdapter(
        fm: FragmentManager?,
        var mNumOfTabs: Int,
        var sliderList: ArrayList<IntroModel.Data>,

        ) :
        FragmentStatePagerAdapter(fm!!) {
        override fun getItem(position: Int): Fragment {

            return DynamicFragment(sliderList, position)
        }

        override fun getCount(): Int {
            return mNumOfTabs
        }
    }
    private fun addPageIndicators() {
        lytPageIndicator.removeAllViews()
        for (i in sliderList.indices) {
            val view = ImageView(applicationContext)
            view.setImageResource(R.drawable.ic_inactive)

            lytPageIndicator.addView(view)
        }
        updatePageIndicator(currentIndex)
    }

    private fun updatePageIndicator(position: Int) {
        var imageView: ImageView

        val lp =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        lp.setMargins(16, 0, 16, 0)
        for (i in 0 until lytPageIndicator.childCount) {
            imageView = lytPageIndicator.getChildAt(i) as ImageView
            imageView.layoutParams = lp
            when (position) {
                i -> {
                    imageView.setImageResource(R.drawable.ic_active)
                }
                else -> {
                    imageView.setImageResource(R.drawable.ic_inactive)
                }
            }
        }
    }



}