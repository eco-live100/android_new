package com.app.ecolive.user_module

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecolive.R
import com.app.ecolive.common_screen.adapters.MyCartListAdapter
import com.app.ecolive.databinding.ActivityMyCartBinding
import com.app.ecolive.localmodel.MyCartListModel
import com.app.ecolive.login_module.LocationPickerActivity
import com.app.ecolive.payment_module.SucessActivity
import com.app.ecolive.payment_module.UserVerificationAddMoneyActivity
import com.app.ecolive.service.Status
import com.app.ecolive.shop_owner.model.GetCartModel
import com.app.ecolive.shop_owner.model.PlaceOrderModel
import com.app.ecolive.shop_owner.model.ShopListModel
import com.app.ecolive.user_module.model.AddressModel
import com.app.ecolive.utils.AppConstant
import com.app.ecolive.utils.CustomProgressDialog
import com.app.ecolive.utils.MyApp
import com.app.ecolive.utils.PreferenceKeeper
import com.app.ecolive.utils.Utils
import com.app.ecolive.viewmodel.CommonViewModel
import com.app.ecolive.viewmodel.PaymentViewModel
import com.google.android.datatransport.ProductData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonArray
import org.json.JSONArray
import org.json.JSONObject

class MyCartActivity : AppCompatActivity() {
    private var userIdOfShopOwner: String = ""
    private var cartId: String = ""
    private var productPrice: Double = 0.0

    private lateinit var binding: ActivityMyCartBinding
    lateinit var adapter: MyCartListAdapter
    private val progressDialog = CustomProgressDialog()
    var cartCount = 0
    var OldcartCount = 0
    var productId: String = ""
    var shopId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_cart)
        initView()

        statusBarColor()
        getCart()
        binding.appCompatBuyNow.setOnClickListener {
            if (binding.grandTotal.text.toString()
                    .toDouble() > AppConstant.walletAmount.toDouble()
            ) {
                Toast.makeText(
                    this,
                    "Amount is less then order please add amount",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.address.text.toString().trim() == "") {
                Toast.makeText(this, "Please select address", Toast.LENGTH_SHORT).show()

            } else if (binding.checkwallet.isChecked) {
                sendMoneyApi()
            } else {
                Toast.makeText(this, "Please checked wallet pay", Toast.LENGTH_SHORT).show()

            }

        }

        binding.address.setOnClickListener {
            val intentaddress =
                Intent(this@MyCartActivity, MyAddressActivity::class.java)
            intentaddress.putExtra("Key", "ForSelect")
            resultActivity.launch(intentaddress)
        }

        binding.addMoney.setOnClickListener {
            startActivity(
                Intent(
                    this@MyCartActivity,
                    UserVerificationAddMoneyActivity::class.java
                )
            )
        }

    }

    override fun onStart() {
        super.onStart()
        getWalletAmountApi()
    }

    private fun initView() {
        binding.toolbar.toolbarTitle.text = "Cart"
        binding.toolbar.ivBack.setOnClickListener { finish() }
    }

    private fun productList(data: List<GetCartModel.Product>) {

        val currentListModel = ArrayList<MyCartListModel>()

        for (i in 0 until data.size) {
            var item = MyCartListModel(
                data[i].qty.toString(),
                data[i].productId.name,
                data[i].productId.price.toString(),
                data[i].productId.firstImage
            )
            currentListModel.add(item)
        }

        binding.orderListRecyclerview.layoutManager = LinearLayoutManager(this)
        adapter =
            MyCartListAdapter(this, currentListModel, object : MyCartListAdapter.ClickListener {
                override fun onClick(pos: Int) {
                    startActivity(
                        Intent(
                            this@MyCartActivity,
                            ProductDetailActivity::class.java
                        ).putExtra("productId", productId)
                    )
                }

                override fun onDelete(pos: Int) {
                    removeCart()
                }

                override fun onMinus(pos: Int) {
                    cartCount--
                    addToCart()


                }

                override fun onPlus(pos: Int) {
                    cartCount++
                    addToCart()
                }
            })
        binding.orderListRecyclerview.adapter = adapter

    }

    private fun statusBarColor() {
        Utils.changeStatusColor(this, R.color.color_050D4C)

    }

    private fun getCart() {
        //progressDialog.show(this)
        binding.shimmerMyCart.visibility = View.VISIBLE
        binding.cartCons.visibility = View.GONE
        var addtoCartViewModel = CommonViewModel(this)
        var json = JSONObject()
        /* json.put("shop_id", shopId)
         json.put("qty", cartCount)
         json.put("product_id", productId)
         json.put("purchase_type", "")
         json.put("product_color", "")*/
        Log.d("ok", "getCart: " + json)
        addtoCartViewModel.getCart(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    binding.shimmerMyCart.visibility = View.GONE
                    binding.cartCons.visibility = View.VISIBLE
                    //  progressDialog.dialog.dismiss()
                    it.data?.let {
                        productList(it.data.products)
                        userIdOfShopOwner = it.data.shopId.userId
                        OldcartCount = it.data.totalQty
                        cartId = it.data._id
                        cartCount = it.data.totalQty
                        shopId = it.data.shopId._id
                        productId = it.data.products[0].productId._id
                        productPrice = it.data.products[0].price.toString().toDouble()
                        var total = Utils.priceMultiplyByQty(
                            it.data.products[0].price.toString(),
                            it.data.totalQty.toString()
                        )
                        binding.itemTotal.text = total
                        binding.grandTotal.text = total
                    }

                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    //    progressDialog.dialog.dismiss()
                    var vv = it.message
                    // var msg = JSONObject(it.message)
                    // MyApp.popErrorMsg("", "" + msg.getString("msg"), THIS!!)
                    MyApp.popErrorMsg("", "" + vv, this)
                }
            }
        }
    }


    private fun removeCart() {
        //  progressDialog.show(this)
        var addtoCartViewModel = CommonViewModel(this)
        var json = JSONObject()
        json.put("cartId", cartId)
        /* json.put("shop_id", shopId)
         json.put("qty", cartCount)
         json.put("product_id", productId)
         json.put("purchase_type", "")
         json.put("product_color", "")*/
        Log.d("ok", "getCart: " + json)
        addtoCartViewModel.removeCart(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {

                    //  progressDialog.dialog.dismiss()
                    it.data?.let {
                        getCart()

                    }

                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    // progressDialog.dialog.dismiss()
                    /* var vv = it.message
                     // var msg = JSONObject(it.message)
                     // MyApp.popErrorMsg("", "" + msg.getString("msg"), THIS!!)
                     MyApp.popErrorMsg("", "" + vv, this)*/
                }
            }
        }
    }


    private fun addToCart() {

        progressDialog.show(this)
        var addtoCartViewModel = CommonViewModel(this)

        var json = JSONObject()
        json.put("shopId", shopId)
        if (OldcartCount < cartCount) {
            var count = cartCount - OldcartCount
            json.put("qty", count)
        } else if (OldcartCount > cartCount) {
            var count = OldcartCount - cartCount
            json.put("qty", -count)
        } else {
            var count = 0
            json.put("qty", count)
        }

        json.put("productId", productId)
        json.put("price", productPrice)

        Log.d("ok", "addProductAPICall: " + json)
        addtoCartViewModel.addToCart(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("ok", "productListAPICall:")
                    progressDialog.dialog.dismiss()

                    it.data?.let {
                        OldcartCount = it.data.totalQty
                        cartCount = it.data.totalQty
                        adapter.updateQTy(it.data.totalQty)
                        var total = Utils.priceMultiplyByQty(
                            it.data.products[0].price.toString(),
                            it.data.totalQty.toString()
                        )
                        binding.itemTotal.text = total
                        binding.grandTotal.text = total
                    }

                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    // var msg = JSONObject(it.message)
                    // MyApp.popErrorMsg("", "" + msg.getString("msg"), THIS!!)
                    MyApp.popErrorMsg("", "" + vv, this)
                }
            }
        }
    }

    private fun PlaceOrderapi() {
        binding.appCompatBuyNow.visibility = View.GONE
        progressDialog.show(this)
        var addtoCartViewModel = CommonViewModel(this)

        var json = JSONObject()
        var json2 = JSONObject()
        var list = ArrayList<PlaceOrderModel.productData>()
        json2.put("vendorShopId", shopId)
        json2.put("productId", productId)
        json2.put("quantity", cartCount)
        json2.put("price", productPrice)
        var jsonArray = JSONArray()
        jsonArray.put(json2)

        //list.add(PlaceOrderModel.productData(shopId,productId,cartCount,productPrice))
        json.put("products", jsonArray)
        json.put("totalAmount", binding.grandTotal.text.toString())
        json.put("status", "Pending")
        json.put("shippingAddress", binding.address.text.toString())
        json.put("paymentMethod", "Credit Card")
        json.put("paymentStatus", "Paid")

        Log.d("ok", "addProductAPICall: " + json)
        addtoCartViewModel.PlaceOrderapi(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("ok", "productListAPICall:")
                    progressDialog.dialog.dismiss()
                    binding.appCompatBuyNow.visibility = View.VISIBLE
                    it.data?.let {
                        Toast.makeText(this, "Order place successfully ", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                }

                Status.LOADING -> {}
                Status.ERROR -> {
                    binding.appCompatBuyNow.visibility = View.VISIBLE
                    progressDialog.dialog.dismiss()
                    var vv = it.message
                    // var msg = JSONObject(it.message)
                    // MyApp.popErrorMsg("", "" + msg.getString("msg"), THIS!!)
                    MyApp.popErrorMsg("", "" + vv, this)
                }
            }
        }
    }

    private fun getWalletAmountApi() {
        //  binding.isShimmerShow =true
        var paymentViewModel = PaymentViewModel(this)
        var json = JSONObject()

        paymentViewModel.getWalletApi(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {

                    it.data?.let {
                        var vv = it.data
                        AppConstant.walletAmount = it.data.money.toString()
                        AppConstant.walletCurrency = it.data.currency.toString()
                      //  PreferenceKeeper.instance.loginResponse!!.wallet.money = it.data.money
                        binding.walletAmount.text = "${it.data.money} ${it.data.currency}"

                    }
                    //   binding.isShimmerShow =false

                }

                Status.LOADING -> {}
                Status.ERROR -> {

                    var vv = it.message
                    var msg = JSONObject(it.message)
                    MyApp.popErrorMsg("", "" + msg.getString("msg"), this)
                    // MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }

    private fun sendMoneyApi() {
        binding.appCompatBuyNow.visibility = View.GONE

        if (userIdOfShopOwner == "") {
            Toast.makeText(
                this@MyCartActivity,
                "Something went wrong please refresh page",
                Toast.LENGTH_SHORT
            ).show()
        }
        //  binding.isShimmerShow =true
        var paymentViewModel = PaymentViewModel(this)
        var json = JSONObject()
        json.put("receiverId", userIdOfShopOwner)
        json.put("transactionType", 3)
        json.put("amount", binding.grandTotal.text.toString())
        json.put("currency", "YEN")
        json.put("paymentFor", "E commerce order")
        json.put("paymentNote", "Order placed")

        paymentViewModel.sendMoneyApi(json).observe(this) { it ->
            when (it.status) {
                Status.SUCCESS -> {

                    it.data?.let {
                        var vv = it.data
                        PlaceOrderapi()

                    }
                    //   binding.isShimmerShow =false

                }

                Status.LOADING -> {}
                Status.ERROR -> {

                    var vv = it.message
                    var msg = JSONObject(it.message)
                    MyApp.popErrorMsg("", "" + msg.getString("msg"), this)
                    // MyApp.popErrorMsg("", "" + vv, THIS!!)
                }
            }
        }
    }

    val resultActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { results ->
            if (results.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = results.data
                if (data!!.getSerializableExtra("result") as AddressModel.Data != null) {
                    var address = data.getSerializableExtra("result") as AddressModel.Data
                    binding.address.text =
                        address.fullName + "\n" + address.title + "\n" + address.mobile
                }
            }

        }

}