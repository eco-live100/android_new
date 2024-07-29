package com.app.ecolive.utils

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.preference.PreferenceManager
import com.app.ecolive.login_module.model.UserModel
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

    var loginResponse: UserModel?
        get() = Gson().fromJson(prefs!!.getString(AppConstant.LOGIN_POJO, ""), UserModel::class.java)
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

    var fcmTokenSave: String?
        get() = prefs!!.getString(AppConstant.FireBaseToken, "")
        set(fcmToken) {
            prefs!!.edit().putString(AppConstant.FireBaseToken, fcmToken).apply()
        }

    var isDriverOnline: Boolean
        get() = prefs!!.getBoolean(AppConstant.IsDriverOnline, false)
        set(isDriverOnline) {
            prefs!!.edit().putBoolean(AppConstant.IsDriverOnline, isDriverOnline)
                .apply()
        }

    var lastLocationLat: String?
        get() = prefs!!.getString(AppConstant.lastSelectedLocationLat, "")
        set(lastLocationLat) {
            prefs!!.edit().putString(AppConstant.lastSelectedLocationLat, lastLocationLat)
                .apply()
        }
    var lastLocationLang: String?
        get() = prefs!!.getString(AppConstant.lastSelectedLocationLang, "")
        set(lastLocationLang) {
            prefs!!.edit().putString(AppConstant.lastSelectedLocationLang, lastLocationLang)
                .apply()
        }

    var lastAddress: String?
        get() = prefs!!.getString(AppConstant.lastAddress, "")
        set(lastAddress) {
            prefs!!.edit().putString(AppConstant.lastAddress, lastAddress)
                .apply()
        }

    var lastAddressTitle: String?
        get() = prefs!!.getString(AppConstant.lastAddressTitle, "")
        set(lastAddressTitle) {
            prefs!!.edit().putString(AppConstant.lastAddressTitle, lastAddressTitle)
                .apply()
        }
}