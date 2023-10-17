package com.cjras.thepresentmovement

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout


class GlobalClass : Application()
{

    companion object {
        var UpdateDataBase: Boolean = true
        var documents = DocumentID()

        var currentUser = UserDataClass()
        var currentUserImage: Bitmap? = null

        var Users = arrayListOf<UserDataClass>()

        fun addLoadingCover(fragmentInflater: LayoutInflater, fragmentFrameLayout: FrameLayout) : ViewGroup
        {
            val loadingProgressBar = fragmentInflater.inflate(R.layout.loading_cover, null) as ViewGroup
            fragmentFrameLayout.addView(loadingProgressBar)

            return loadingProgressBar
        }



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