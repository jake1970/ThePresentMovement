package com.cjras.thepresentmovement

import android.app.AlertDialog
import android.app.Application
import android.content.Context

class GlobalClass : Application()
{

    companion object {
        var UpdateDataBase: Boolean = true
        var documents = DocumentID()

        var Users = arrayListOf<UserDataClass>()

        fun InformUser(messageTitle: String, messageText: String, context: Context) {
            val alert = AlertDialog.Builder(context)
            alert.setTitle(messageTitle)
            alert.setMessage(messageText)
            alert.setPositiveButton(context.getString(R.string.okText), null)

            alert.show()
        }



    }

}