package com.cjras.thepresentmovement

//user data class
data class UserDataClass
    (
    var UserID : String = "",
    var FirstName: String = "",
    var LastName: String = "",
    var EmailAddress : String = "",
    var MemberTypeID: Int = 1, //their member status, eg senior member
    var Quote: String = "",
    var ContactNumber : String = "",
    var CompanyName: String = "",
    var LinkedIn: String = "",
    var Website : String = "",
    var HasImage: Boolean = false //whether the user has added a profile image or not
            )
{

    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to get the users full name
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun getFullName(): String
    {
        return "$FirstName $LastName"
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}