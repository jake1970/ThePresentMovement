package com.cjras.thepresentmovement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.cjras.thepresentmovement.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val myPrefsFile = "MyPrefsFile";
    private val myUserID = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //View Binding
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        //Hide the action bar
        supportActionBar?.hide()

        //set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.main_grey)


        //define preference file
        val pref = getSharedPreferences(myPrefsFile, MODE_PRIVATE)

        //get the stored user ID
        val userID = pref.getString(myUserID, null)

        if (userID != null) {

            GlobalClass.currentUser.UserID = userID
            var intent = Intent(this, home_bottom_navigation::class.java) //ViewActivity
            startActivity(intent)

        }



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //When the welcome button is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.btnSignUp.setOnClickListener()
        {

            //take the user to the sign in screen
            var intent = Intent(this, login::class.java) //ViewActivity
            startActivity(intent)
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    }


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //When the native android back button/functionality is invoked
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    override fun onBackPressed() {
        // Do nothing
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}