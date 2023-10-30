package com.cjras.thepresentmovement

import android.content.Context

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




    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to validate the users password
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun validateUserPassword(attemptedPassword : String, context : Context): String
    {

        //the errors found with the users password
        var validationErrors = ""

        //check the length of the password
        if (attemptedPassword.length < 8)
        {
            validationErrors += context.getString(R.string.passwordShort) + "\n"
        }

        //check the amount of numbers in the password
        if (attemptedPassword.count(Char::isDigit) == 0)
        {
            validationErrors+=(context.getString(R.string.passwordNeedsNumber))+ "\n"
        }

        //check if the password contains any lower case characters
        if (!attemptedPassword.any(Char::isLowerCase))
        {
            validationErrors+=(context.getString(R.string.passwordNeedsLowerCase))+ "\n"
        }

        //check if the user password contains any uppercase characters
        if (!attemptedPassword.any(Char::isUpperCase))
        {
            validationErrors+=(context.getString(R.string.passwordNeedsUpperCase))+ "\n"
        }

        //check if the users passwords first character is uppercase
        if (!attemptedPassword[0].isUpperCase())
        {
            validationErrors+=(context.getString(R.string.passwordNeedsToStartWithUpperCaseLetter))+ "\n"
        }

        //check if the users password contains a valid special character
        if (!attemptedPassword.any { it in context.getString(R.string.passwordSpecialCharacters) })
        {
            validationErrors+=(context.getString(R.string.passwordNeedsSpecialCharacter))+ "\n"
        }

        //return the password errors
        return validationErrors


    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}