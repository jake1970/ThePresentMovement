package com.cjras.thepresentmovement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.cjras.thepresentmovement.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
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

    override fun onBackPressed() {
        // Do nothing
    }
}