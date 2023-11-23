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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth


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


        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //Method to log the user out of the app
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        fun logout(context: FragmentActivity) {

            val firebaseAuth = FirebaseAuth.getInstance()

            //invalidate the users sign in status
            //https://firebase.google.com/docs/auth/android/start
            firebaseAuth.signOut()

            //intent to take the user back to the login screen
            var intent = Intent(context, login::class.java)

            //clear the current users data
            currentUser = UserDataClass()

            //prime the database to be read from upon the next sign in
            UpdateDataBase = true

            val myPrefsFile = "MyPrefsFile";
            val myUserID = "";

            context.getSharedPreferences(myPrefsFile, AppCompatActivity.MODE_PRIVATE)
                .edit()
                .putString(myUserID, null)
                .commit()

            //call intent and send user back to the login screen
            context.startActivity(intent)

        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //Method to re-pull the userID if unloaded
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        fun checkUser(context: Fragment)
        {

            //prime preference file
             val myPrefsFile = "MyPrefsFile";
             val myUserID = "";

            //define preference file
            val pref = context.requireActivity().getSharedPreferences(myPrefsFile, MODE_PRIVATE)

            //get the stored user ID
            val userID = pref.getString(myUserID, null)

            //check if the userID in the preference file is valid
            if (userID != null) {

                //check if the current loaded userID is invalid
                if (currentUser.UserID.isNullOrEmpty())
                {
                    //set the current userID
                    currentUser.UserID = userID

                    //call method to reload the currently active fragment
                    RefreshFragment(context)
                }
            }
            else
            {
                //if the userID cannot be restored

                //send user back to main welcome screen
                var intent = Intent(context.requireActivity(), MainActivity::class.java) //ViewActivity
                context.startActivity(intent)
            }
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



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

            val checkedURL = checkURLFormat(url)

            //new weblink intent
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(checkedURL))

            //start intent with passed context
            context.startActivity(intent)
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //Method to check the prefix of a url
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        private fun checkURLFormat(url : String) : String
        {
            //check for prefixing url string
            return if (url.startsWith("http://") || url.startsWith("https://")) {

                //if url is valid return
                url

            } else {

                //if url is invalid, attempt to correct it then return

                //source - https://stackoverflow.com/a/53847483
                URLUtil.guessUrl(url)
            }

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