package com.cjras.thepresentmovement

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cjras.thepresentmovement.databinding.ActivityHomeBottomNavigationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class home_bottom_navigation : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBottomNavigationBinding

    //the current checked state of the bottom navigation menu
    private val bottomNavBarStateList = arrayOf(
        intArrayOf(android.R.attr.state_checked),
        intArrayOf(-android.R.attr.state_checked)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_bottom_navigation)


        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //Set View Binding
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding = ActivityHomeBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        //Hide the action bar
        supportActionBar?.hide()

        //set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.sub_grey)


        //create local fragment controller
        val fragmentControl = FragmentManager()


        //set the initial selected screen to be the home screen
        fragmentControl.replaceFragment(home(), R.id.flContent, supportFragmentManager)
        binding.bnvHomeNavigation.selectedItemId = R.id.iHome



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //When the bottom navigation menu is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.bnvHomeNavigation.setOnItemSelectedListener {

            //check which item was selected
            when (it.itemId) {

                //select notices screen
                R.id.iNotices -> {

                    //call method to load the notices fragment
                    fragmentControl.replaceFragment(
                        notices(),
                        R.id.flContent,
                        supportFragmentManager
                    )

                    //call method to update the color of the bottom navigation menus selected item
                    updateBottomNavBarColor(R.color.present_yellow)
                }

                //selected home screen
                R.id.iHome -> {

                    //call method to load the home fragment
                    fragmentControl.replaceFragment(home(), R.id.flContent, supportFragmentManager)

                    //call method to update the color of the bottom navigation menus selected item
                    updateBottomNavBarColor(R.color.present_blue)
                }

                //selected contacts screen
                R.id.iContact -> {

                    //call method to load the contacts fragment
                    fragmentControl.replaceFragment(
                        contacts(),
                        R.id.flContent,
                        supportFragmentManager
                    )

                    //call method to update the color of the bottom navigation menus selected item
                    updateBottomNavBarColor(R.color.present_red)
                }

                //selected settings screen
                R.id.iSettings -> {

                    //call method to load the settings fragment
                    fragmentControl.replaceFragment(
                        settings(),
                        R.id.flContent,
                        supportFragmentManager
                    )

                    //call method to update the color of the bottom navigation menus selected item
                    updateBottomNavBarColor(R.color.present_green)
                }

                //selected admin screen (only senior members and admin users)
                R.id.iAdmin -> {

                    //call method to load the admin fragment
                    fragmentControl.replaceFragment(admin(), R.id.flContent, supportFragmentManager)

                    //call method to update the color of the bottom navigation menus selected item
                    updateBottomNavBarColor(R.color.admin_orange)
                }

                else -> {

                }
            }

            //select the item
            true
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to update the selected icon color for the bottom navigation menu
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private fun updateBottomNavBarColor(currentSelectedColor: Int) {

        //array of bottom navigation menu color selection
        val colorList = intArrayOf(
            ContextCompat.getColor(this, currentSelectedColor),
            ContextCompat.getColor(this, R.color.white)
        )

        //new color state list for the bottom navigation menu
        val colorStateList = ColorStateList(bottomNavBarStateList, colorList)

        //apply the styling (changed colors) to the bottom navigation menu
        binding.bnvHomeNavigation.itemIconTintList = colorStateList
        binding.bnvHomeNavigation.itemTextColor = colorStateList
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    override fun onBackPressed() {
        // Do nothing
    }

}