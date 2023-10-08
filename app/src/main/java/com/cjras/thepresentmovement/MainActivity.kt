package com.cjras.thepresentmovement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

//-----------------------------------------------------------------------//

    }
}


//-----------------------------------------------------------------------//
//Notes:
//-----------------------------------------------------------------------//
//
//-----------------------------------------------------------------------//