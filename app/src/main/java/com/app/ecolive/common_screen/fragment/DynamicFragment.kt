package com.app.ecolive.common_screen.fragment


import android.R.attr.data
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.ecolive.R
import com.app.ecolive.login_module.model.IntroModel
import com.app.ecolive.utils.Utils


class DynamicFragment(var sliderList: ArrayList<IntroModel.Data>, var position: Int) : Fragment() {
    private lateinit var binding: com.app.ecolive.databinding.FragmentDynamicBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dynamic, container, false)
        if (sliderList.size > 0) {
           // binding.tvDescription.isClickable=false
          //  binding.tvDescription.isEnabled=false
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    var desc = sliderList[position].description
                    var desc1 = Html.fromHtml(desc, Html.FROM_HTML_MODE_LEGACY)
                    var desc2 = desc1.replace("\\<.*?\\>".toRegex(), "")
                    binding.tvTitle.text = desc2.replace("&nbsp;","").replace("amp;","")
                    desc = sliderList[position].content
                    desc1 = Html.fromHtml(desc, Html.FROM_HTML_MODE_LEGACY)
                    desc2 = desc1.replace("\\<.*?\\>".toRegex(), "")
                  //  binding.tvDescription.setText( desc2.replace("&nbsp;","").replace("amp;",""))
                } else {
                    binding.tvTitle.text = Html.fromHtml(sliderList[position].description);
                   // binding.tvDescription.setText( Html.fromHtml(sliderList[position].content));
                }

                binding.introWebView.settings.javaScriptEnabled = true
                //binding.introWebView.loadData("",sliderList[position].content, "text/html; charset=utf-8", "UTF-8")
                binding.introWebView.loadDataWithBaseURL("",sliderList[position].content, "text/html; charset=utf-8", "UTF-8","")

                Utils.setImageFullPath(activity, binding.ivImage, sliderList[position].banner)
            } catch (e: Exception) {
            }
        }
        return binding.root
    }
}