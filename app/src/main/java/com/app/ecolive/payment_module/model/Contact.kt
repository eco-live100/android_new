package com.app.ecolive.payment_module.model

data class Contact2(
    val id: String? = null,
    var numbers: String? = null,
    var emails: String? = null,
    val name: String? = null,
    val imageUri: String?=null,
    var mobile: String? = null,
    var isSelected:Boolean = false
) {
    var profile_pic: String? = null
    var relation: String? = null

    fun getFormattedMobile(): String? {
        val formattedMobile: String? = if (!mobile.isNullOrEmpty()){
            mobile?.filter { it.isDigit() && !it.isWhitespace() }?.trim()
        }else{
            ""
        }

        return formattedMobile
    }


}
data class Contact(val id: String, val name:String) {
    var numbers = ArrayList<String>()
    var emails = ArrayList<String>()
}