package com.cjras.thepresentmovement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.cjras.thepresentmovement.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//-----------------------------------------------------------------------//

        //Set view binding
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Hide the action bar
        supportActionBar?.hide()

        //set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.main_grey)

//-----------------------------------------------------------------------//

    binding.btnSignUp.setOnClickListener()
    {
        /*
        var intent = Intent(this, home_bottom_navigation::class.java) //ViewActivity
        startActivity(intent)
         */

        var intent = Intent(this, login::class.java) //ViewActivity
        startActivity(intent)
    }


    }
}


//-----------------------------------------------------------------------//
//Notes:
//-----------------------------------------------------------------------//
//
//-----------------------------------------------------------------------//