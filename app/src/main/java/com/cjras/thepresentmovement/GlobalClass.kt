package com.cjras.thepresentmovement

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory


class GlobalClass : Application()
{

    companion object {
        var UpdateDataBase: Boolean = true
        var documents = DocumentID()

        var currentUser = UserDataClass()
        var currentUserImage: Bitmap? = null

        var Users = arrayListOf<UserDataClass>()

        fun InformUser(messageTitle: String, messageText: String, context: Context) {
            val alert = AlertDialog.Builder(context)
            alert.setTitle(messageTitle)
            alert.setMessage(messageText)
            alert.setPositiveButton(context.getString(R.string.okText), null)

            alert.show()
        }

        /*
        fun setDefaultBitmap(context: Context)
        {
            currentUserImage = BitmapFactory.decodeResource(
                context.resources,
                R.drawable.person_icon
            )
        }

         */



    }

}