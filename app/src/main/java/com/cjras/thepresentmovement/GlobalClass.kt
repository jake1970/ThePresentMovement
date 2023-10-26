package com.cjras.thepresentmovement

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment


class GlobalClass : Application()
{

    companion object {
        var UpdateDataBase: Boolean = true
        var documents = DocumentID()

        var currentUser = UserDataClass()
        var currentUserMemberType = ""
        var currentUserImage: Bitmap? = null

        var Users = arrayListOf<UserDataClass>()
        var MemberTypes = arrayListOf<MemberTypeDataClass>()
        var Announcements = arrayListOf<AnnouncementDataClass>()



        fun InformUser(messageTitle: String, messageText: String, context: Context) {
            val alert = AlertDialog.Builder(context)
            alert.setTitle(messageTitle)
            alert.setMessage(messageText)
            alert.setPositiveButton(context.getString(R.string.okText), null)

            alert.show()
        }


        fun RefreshFragment(currentFragment: Fragment)
        {
            UpdateDataBase = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                currentFragment.fragmentManager?.beginTransaction()?.detach(currentFragment)?.commitNow();
                currentFragment.fragmentManager?.beginTransaction()?.attach(currentFragment)?.commitNow();
            } else {
                currentFragment.fragmentManager?.beginTransaction()?.detach(currentFragment)?.attach(currentFragment)?.commit();
            }
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