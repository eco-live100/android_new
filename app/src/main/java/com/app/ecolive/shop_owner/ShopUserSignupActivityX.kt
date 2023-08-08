package com.app.ecolive.shop_owner

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.app.ecolive.R
import com.app.ecolive.common_screen.UserHomePageNavigationActivity
import com.app.ecolive.databinding.ActivityShopUserSignupBinding
import com.app.ecolive.login_module.LoginActivity
import com.app.ecolive.service.Status
import com.app.ecolive.shop_owner.model.ShopCategryListModel
import com.app.ecolive.utils.*
import com.app.ecolive.viewmodel.CommonViewModel
import com.offercity.base.BaseActivity
import org.json.JSONObject

class ShopUserSignupActivityX : BaseActivity() {
    lateinit var binding: ActivityShopUserSignupBinding

    private val progressDialog = CustomProgressDialog()
    var cagrySubIdList = ArrayList<String>()
    lateinit var wholeCategoryList : ArrayList<ShopCategryListModel.Data>
    var selectedSubCategoryID = ""
    var cagryIdList = ArrayList<String>()
    var selectedCategoryID = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_user_signup)
        initView()
        shopCategoriesListAPICAll()
    }

    private fun shopCategoriesListAPICAll() {
        progressDialog.show(THIS!!)
        var loginViewModel = CommonViewModel(THIS!!)
        loginViewModel.shopCategoriesList().observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        wholeCategoryList = ArrayList ()
                        wholeCategoryList.addAll(it.data)
                        setCagrySpin(wholeCategoryList)


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

    private fun setCagrySpin(list: List<ShopCategryListModel.Data>) {
        var cagryNameList = ArrayList<String>()
        cagryNameList.add(resources.getString(R.string.plz_select_catgry))
        cagryIdList = ArrayList<String>()
        cagryIdList.add("00000")
        for (i in 0 until list.size) {
            cagryNameList.add(list[i].categoryName)
            cagryIdList.add(list[i]._id)

        }

        val aa: ArrayAdapter<Any> = ArrayAdapter<Any>(this, android.R.layout.simple_spinner_item, cagryNameList as List<Any>)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinCategory.adapter = aa

        binding.spinCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedCategoryID = cagryIdList[position]
                    if(position>0) {
                        var subCaglist = wholeCategoryList[position-1].subCategories
                        if (subCaglist.isNotEmpty()) {
                            setSubCatgrySpin(subCaglist)
                        }else{
                            setSubCatgrySpin(ArrayList<ShopCategryListModel.SubCategory>())
                        }
                    }else{
                        setSubCatgrySpin(ArrayList<ShopCategryListModel.SubCategory>())
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
    }


    private fun setSubCatgrySpin(subCaglist: List<ShopCategryListModel.SubCategory>) {
        var listSubCtgryName=ArrayList<String>()
        cagrySubIdList = ArrayList<String>()
        listSubCtgryName.add(resources.getString(R.string.plz_select_subcatgry))
        cagrySubIdList.add("000000")
        for (i in 0 until subCaglist.size){
            listSubCtgryName.add(subCaglist[i].subCategoryName)
            cagrySubIdList.add(subCaglist[i].shopCategoryId)
        }
        val aa: ArrayAdapter<Any> = ArrayAdapter<Any>(this, android.R.layout.simple_spinner_item, listSubCtgryName as List<Any>)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinSubCatgry.adapter = aa

        binding.spinSubCatgry.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedSubCategoryID = cagrySubIdList[position]


                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding.btnSignupShop->{
                if(isValidInput()){
                    //vendorshopdetailsAPICall()
                }
            }
        }
    }



    private fun isValidInput(): Boolean {
        if(binding.shopName.text.toString().isBlank()){
            MyApp.popErrorMsg("",resources.getString(R.string.plz_enter_shopname),THIS!!)
            return false
        }
        else if(binding.spinCategory.selectedItem.toString()==resources.getString(R.string.plz_select_catgry)){
            MyApp.popErrorMsg("",resources.getString(R.string.plz_enter_shppCAtegry),THIS!!)
            return false
        }
        else if(binding.spinSubCatgry.selectedItem.toString()==resources.getString(R.string.plz_select_subcatgry)){
            MyApp.popErrorMsg("",resources.getString(R.string.plz_enter_shppSubCategry),THIS!!)
            return false
        }
        return true

    }

    private fun vendorshopdetailsAPICall() {
        progressDialog.show(THIS!!)
        var loginViewModel = CommonViewModel(THIS!!)
        var jsonObject = JSONObject()
        jsonObject.put("shopCategoryId",selectedCategoryID)
        jsonObject.put("shopSubCategoryId",selectedSubCategoryID)
        jsonObject.put("shopName",binding.shopName.text.toString())

        loginViewModel.vendorSignup(jsonObject).observe(THIS!!) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog.dialog.dismiss()
                    it.data?.let {
                        MyApp.ShowMassage(THIS!!,it.message)
                        var mdol= PreferenceKeeper.instance.loginResponse
                        if (mdol != null) {
                            mdol.isVendor=true
                        }
                        if(intent.getBooleanExtra(AppConstant.IS_FROM_USERTYPEOPTIONACTIVITY,false)){
                            startActivity(Intent(THIS, UserHomePageNavigationActivity::class.java))
                        }
                        PreferenceKeeper.instance.loginResponse=mdol
                        finish()
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
    private fun initView() {
        Utils.changeStatusColor(this, R.color.color_F5F5F5)
        Utils.changeStatusTextColor(this)
        setTouchNClick(binding.btnSignupShop)

//        binding.imageViewArrow.setOnClickListener {
//            binding.tvShopCategory.performClick()
//        }
//        binding.tvShopCategory.setOnClickListener {
//            multiSelectDialog!!.show(supportFragmentManager, "multiSelectDialog")
//        }
        binding.signInLabel.setOnClickListener {
            startActivity(
                Intent(
                    this@ShopUserSignupActivityX,
                    LoginActivity::class.java
                )
            )
        }

    }

//    private fun setShopCategory() {
//        val listOfCountries: ArrayList<MultiSelectModel> = ArrayList()
//        listOfCountries.add(MultiSelectModel(1,
//            "Food"))
//        listOfCountries.add(MultiSelectModel(2,
//            "Clothing"))
//        listOfCountries.add(MultiSelectModel(3,
//            "Grocery"))
//
//
//        multiSelectDialog = MultiSelectDialog()
//            .title("Select categories") //setting title for dialog
//            .titleSize(17f)
//            .positiveText("Done")
//            .negativeText("Cancel")
//            .setMinSelectionLimit(0)
//            .setMaxSelectionLimit(listOfCountries.size)
//            .multiSelectList(listOfCountries) // the multi select model list with ids and name
//            .onSubmit(object : MultiSelectDialog.SubmitCallbackListener {
//                override fun onSelected(
//                    selectedIds: ArrayList<Int>,
//                    selectedNames: ArrayList<String>,
//                    dataString: String,
//                ) {
//
//                    for (i in 0 until selectedIds.size) {
//                        catList.add(selectedIds[i].toString())
//
//                        if (categoriesId.isBlank()) {
//                            categoriesId = selectedIds[i].toString()
//                        } else {
//                            categoriesId = categoriesId + "," + selectedIds[i].toString()
//                        }
//                    }
//
//                    if (dataString.isBlank()) {
//                        binding.tvShopCategory.text = "Select category"
//                    } else {
//                        binding.tvShopCategory.text = dataString
//                    }
//
//                }
//
//                override fun onCancel() {
//
//                }
//            })
//    }
}