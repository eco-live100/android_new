package com.app.ecolive.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.app.ecolive.login_module.model.LoginModel

import com.google.gson.Gson

/**
 * Class is used to save user data in preference.
 */
class PreferenceKeeper private constructor(context: Context?) {
    private var prefs: SharedPreferences? = null
    var PRIVATE_MODE = 0


    companion object {
        private var keeper: PreferenceKeeper? = null
        private var context: Context? = null

        @JvmStatic
        val instance: PreferenceKeeper
            get() {
                if (keeper == null) {
                    keeper = PreferenceKeeper(context)
                }
                return keeper as PreferenceKeeper
            }

        fun setContext(ctx: Context?) {
            context = ctx
        }
    }

    init {
        if (context != null) prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun clearData() {
        val editor = prefs!!.edit()
        editor.clear()
        editor.apply()
    }

  var bearerTokenSave: String?
        get() = prefs!!.getString(AppConstant.BearerTOKEN, "")
        set(cnt) {
            prefs!!.edit().putString(AppConstant.BearerTOKEN, cnt).apply()
        }

    /*

    var isFavApiCall: Boolean
        get() = prefs!!.getBoolean(AppConstant.IsFavApiCall, false)
        set(islogin) {
            prefs!!.edit().putBoolean(AppConstant.IsFavApiCall, islogin)
                .apply()
        }
*/

    var isUserLogin: Boolean
        get() = prefs!!.getBoolean(AppConstant.IS_LOGIN, false)
        set(islogin) {
            prefs!!.edit().putBoolean(AppConstant.IS_LOGIN, islogin)
                .apply()
        }

    var isTwitterLoggedInAlready: Boolean
        get() = prefs!!.getBoolean(AppConstant.IS_TWITTERLOGIN, false)
        set(islogin) {
            prefs!!.edit().putBoolean(AppConstant.IS_TWITTERLOGIN, islogin)
                .apply()
        }

    var loginResponse: LoginModel.Data?
        get() = Gson().fromJson(prefs!!.getString(AppConstant.LOGIN_POJO, ""), LoginModel.Data::class.java)
        set(type) {
            val json = Gson().toJson(type)
            prefs!!.edit().putString(AppConstant.LOGIN_POJO, json.toString()).apply()
        }

    var isAPPInstallFirstTime: Boolean
        get() = prefs!!.getBoolean(AppConstant.IS_APPINSTALL_FIRST, false)
        set(islogin) {
            prefs!!.edit().putBoolean(AppConstant.IS_APPINSTALL_FIRST, islogin)
                .apply()
        }

    var isHealthProfileCreate: Boolean
        get() = prefs!!.getBoolean(AppConstant.IS_HEALTH_PROFILE_CREATE, false)
        set(islogin) {
            prefs!!.edit().putBoolean(AppConstant.IS_HEALTH_PROFILE_CREATE, islogin)
                .apply()
        }







}