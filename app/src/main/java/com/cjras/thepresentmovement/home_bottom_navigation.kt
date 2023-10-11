package com.cjras.thepresentmovement

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cjras.thepresentmovement.databinding.ActivityHomeBottomNavigationBinding

class home_bottom_navigation : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBottomNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_bottom_navigation)

        //-----------------------------------------------------------------------//

        //Set view binding
        //val binding = ActivityHomeBottomNavigationBinding.inflate(layoutInflater)
        binding = ActivityHomeBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Hide the action bar
        supportActionBar?.hide()

        //set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.main_grey)

        //-----------------------------------------------------------------------//

        //create local fragment controller
        val fragmentControl = FragmentManager()

        fragmentControl.replaceFragment(home(), R.id.flContent, supportFragmentManager)

       // replaceFragment(home())
        binding.bnvHomeNavigation.selectedItemId = R.id.iHome



        binding.bnvHomeNavigation.setOnItemSelectedListener {
           when(it.itemId)
           {

               R.id.iNotices -> fragmentControl.replaceFragment(notices(), R.id.flContent, supportFragmentManager)
               R.id.iHome -> fragmentControl.replaceFragment(home(), R.id.flContent, supportFragmentManager)
               R.id.iContact -> fragmentControl.replaceFragment(contacts(), R.id.flContent, supportFragmentManager)
               R.id.iSettings -> fragmentControl.replaceFragment(settings(), R.id.flContent, supportFragmentManager)
               R.id.iAdmin -> fragmentControl.replaceFragment(admin(), R.id.flContent, supportFragmentManager)

               else -> {

               }
           }
            true
        }


    }



}