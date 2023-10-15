package com.cjras.thepresentmovement

import android.provider.Settings.Global

data class UserDataClass
    (

    var UserID : String = "",
    var FirstName: String = "",
    var LastName: String = "",
    var EmailAddress : String = "",
    var MemberTypeID: Int = 1,
    var Quote: String = "",
    var ContactNumber : String = "",
    var CompanyName: String = "",
    var LinkedIn: String = "",
    var Website : String = "",
    var HasImage: Boolean = false
            )
{
fun getFullName(): String
{
    return "$FirstName $LastName"
}
}