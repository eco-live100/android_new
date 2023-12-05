package com.app.ecolive.utils

object AppConstant {

    const val BASE_URL = "https://api.ecolive.global/api1/"
    const val NO_INTERNET = 502
    const val INTERNAL_ERROR = 500
    const val PARSING_ERROR = 504
    const val BearerTOKEN = "BearerTOKEN"
    const val MOBILE_NUMBER = "MOBILE_NUMBER"
    const val COUNTRY_CODE = "COUNTRY_CODE"
    const val LOGIN_POJO = "LOGIN_POJO"
    const val IS_APPINSTALL_FIRST = "IS_APPINSTALL_FIRST"
    const val IS_LOGIN = "IS_LOGIN"
    const val IS_HEALTH_PROFILE_CREATE = "IS_LOGIN"
    const val SOCIAL_LOGIN_TYPE = "SOCIAL_LOGIN_TYPE"
    const val SOCIAL_LOGIN_ID = "SOCIAL_LOGIN_ID"
    const val CAMERA_KEY = "CAMERA_KEY"
    const val GALLERY_KEY = "GALLERY_KEY"
    const val DELETE_KEY = "DELETE_KEY"
    const val SOCIAL_LOGIN_NAME = "SOCIAL_LOGIN_NAME"
    const val SOCIAL_LOGIN_EMAIL = "SOCIAL_LOGIN_EMAIL"
    const val IS_FROM_USERTYPEOPTIONACTIVITY = "IS_FROM_USERTYPEOPTIONACTIVITY"
    const val STORE_TYPE = "STORE_TYPE"
    const val CATEGORY = "CATEGORY"
    const val FOOD = "FOOD"
    const val GROCERY = "GROCERY"
    const val PHARMACY = "PHARMACY"
    const val RETAIL = "RETAIL"
    const val LOGIN_TO_TWITTER = 10001
    const val EXTRA_AUTH_URL_KEY = "EXTRA_AUTH_URL_KEY"
    const val IS_TWITTERLOGIN = "IS_TWITTERLOGIN"
    const val EXTRA_CALLBACK_URL_KEY = "EXTRA_CALLBACK_URL_KEY"
    const val STORE_DATA = "STORE_DATA"



    const val CODE_Direction_Api = 100
    // Service Intent Actions
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"
    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    // Tracking Options
    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_UPDATE_INTERVAL = 2000L
    const val TIMER_UPDATE_INTERVAL = 50L

    // Notifications
    const val NOTIFICATION_CHANNEL_ID = "ecolive"
    const val NOTIFICATION_CHANNEL_NAME = "Ecolive"
    const val NOTIFICATION_ID = 1
    var FireBaseToken = "fireBaseToken"
    var fcmToken = "fcmToken"


    var trackOrderDetail = "trackOrder"
    var driverId = "DriverId"
    var IsDriverOnline = "isDriverOnline"
    var notificationModel = "NotificationModel"
















    object INTENT_EXTRAS {
        const val INTENT_TYPE = "intent_type"
        const val USER_TYPE = "user_type"
        const val IsFromHOME = "IsFromHOME"

    }

    object INTENT_VALUE {
        const val INTENT_TYPE_GET_STARTED = "intent_type_get_started"
        const val INTENT_TYPE_SIGNUP = "intent_type_SIGN"
    }


    object USER_TYPE {
        const val TYPE_USER="User"
        const val TYPE_RIDER="Rider"
        const val TYPE_SHOP="Shop"
    }


}