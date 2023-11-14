package com.cjras.thepresentmovement

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment


class GlobalClass : Application()
{

    companion object {

        //variable to track if the database must be updated
        var UpdateDataBase: Boolean = true

        //instance of document ID to track the IDs of the loaded data, used when modifying data in the firebase database
        var documents = DocumentID()

        //the current signed in users data
        var currentUser = UserDataClass()

        //the current signed in users member type string. eg: Senior Member
        var currentUserMemberType = ""

        //the current users contact image
        var currentUserImage: Bitmap? = null

        //list of the users data
        var Users = arrayListOf<UserDataClass>()

        //list of the member type data
        var MemberTypes = arrayListOf<MemberTypeDataClass>()

        //list of announcement data
        var Announcements = arrayListOf<AnnouncementDataClass>()

        //list of event data
        var Events = arrayListOf<EventDataClass>()

        //list of project data
        var Projects = arrayListOf<ProjectDataClass>()

        //list of user project data
        var UserProjects = arrayListOf<UserProjectDataClass>()


        fun checkUser(context: Fragment)
        {
             val myPrefsFile = "MyPrefsFile";
             val myUserID = "";

            //define preference file
            val pref = context.requireActivity().getSharedPreferences(myPrefsFile, MODE_PRIVATE)

            //get the stored user ID
            val userID = pref.getString(myUserID, null)

            if (userID != null) {

                if (currentUser.UserID.isNullOrEmpty())
                {
                    currentUser.UserID = userID
                    RefreshFragment(context)
                }
            }
            else
            {
                var intent = Intent(context.requireActivity(), MainActivity::class.java) //ViewActivity
                context.startActivity(intent)
            }


        }


        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //Method to show a popup message to the user
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        fun InformUser(messageTitle: String, messageText: String, context: Context) {
            val alert = AlertDialog.Builder(context)
            alert.setTitle(messageTitle)
            alert.setMessage(messageText)
            alert.setPositiveButton(context.getString(R.string.okText), null)
            alert.show()
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //Method to reload the currently active fragment
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        fun RefreshFragment(currentFragment: Fragment)
        {
            //set database update controller to true, update the database
            UpdateDataBase = true

            //check the current sdk version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                //detach and reattach fragment
                currentFragment.fragmentManager?.beginTransaction()?.detach(currentFragment)?.commitNow();
                currentFragment.fragmentManager?.beginTransaction()?.attach(currentFragment)?.commitNow();
            } else {
                //detach and reattach fragment
                currentFragment.fragmentManager?.beginTransaction()?.detach(currentFragment)?.attach(currentFragment)?.commit();
            }
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //Method to open a link in the browser
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        fun openBrowser(url: String, context: Context) {

            //new weblink intent
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            //start intent with passed context
            context.startActivity(intent)
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //Method to check whether a link is valid
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        fun isValidUrl(url: String): Boolean { //source - https://stackoverflow.com/a/62327818

            //define the validation pattern
            val p = Patterns.WEB_URL

            //if the pattern aligns with the passed URL
            val m = p.matcher(url)

            //return the state of the match
            return m.matches()
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    }

}