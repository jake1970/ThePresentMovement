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

    // lateinit var loadingCover: ViewGroup

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
        //window.statusBarColor = ContextCompat.getColor(this, R.color.main_grey)
        window.statusBarColor = ContextCompat.getColor(this, R.color.sub_grey)

        //-----------------------------------------------------------------------//


        //create local fragment controller
        val fragmentControl = FragmentManager()

        fragmentControl.replaceFragment(home(), R.id.flContent, supportFragmentManager)

       // replaceFragment(home())
        binding.bnvHomeNavigation.selectedItemId = R.id.iHome

        //
        val bottomNavBarStateList = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )

        fun updateBottomNavBarColor(currentSelectedColor: Int) {
            val colorList = intArrayOf(
                ContextCompat.getColor(this, currentSelectedColor),
                ContextCompat.getColor(this, R.color.white)
            )
            val colorStateList = ColorStateList(bottomNavBarStateList, colorList)
            binding.bnvHomeNavigation.itemIconTintList = colorStateList
            binding.bnvHomeNavigation.itemTextColor = colorStateList
        }
        //

        binding.bnvHomeNavigation.setOnItemSelectedListener {
           when(it.itemId)
           {

               R.id.iNotices -> {
                   fragmentControl.replaceFragment(notices(), R.id.flContent, supportFragmentManager)
                   updateBottomNavBarColor(R.color.present_yellow)
               }
               R.id.iHome -> {
                   fragmentControl.replaceFragment(home(), R.id.flContent, supportFragmentManager)
                   updateBottomNavBarColor(R.color.present_blue)
               }
               R.id.iContact -> {
                   fragmentControl.replaceFragment(contacts(), R.id.flContent, supportFragmentManager)
                   updateBottomNavBarColor(R.color.present_red)
               }
               R.id.iSettings -> {
                   fragmentControl.replaceFragment(settings(), R.id.flContent, supportFragmentManager)
                   updateBottomNavBarColor(R.color.present_green)
               }
               R.id.iAdmin -> {
                   fragmentControl.replaceFragment(admin(), R.id.flContent, supportFragmentManager)
                   updateBottomNavBarColor(R.color.admin_orange)
               }

               else -> {

               }
           }
            true
        }




        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        //initial data population
        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        //loadingCover = GlobalClass.addLoadingCover(layoutInflater, binding.root)
        try {

           // var loadingCover = GlobalClass.addLoadingCover(layoutInflater, binding.root)
            binding.rlLoadingCover.visibility = View.VISIBLE

            //Read Data
            GlobalScope.launch{
                if (GlobalClass.UpdateDataBase == true)
                {
                    var DBManger = DatabaseManager()

                    GlobalClass.Users = DBManger.getAllUsersFromFirestore()
                    GlobalClass.MemberTypes = DBManger.getAllMemberTypesFromFirestore()
                    GlobalClass.UpdateDataBase = false

                    //********************************************************
                    //temp code to set current user
                    //GlobalClass.currentUser = GlobalClass.Users[0]
                    //********************************************************

                    //get the users image
                    GlobalClass.currentUserImage = DBManger.getUserImage(baseContext, GlobalClass.currentUser.UserID, GlobalClass.currentUser.HasImage)
                }
                withContext(Dispatchers.Main) {

                    //hide admin menu
                    if (GlobalClass.currentUser.MemberTypeID != 2)
                    {
                        binding.bnvHomeNavigation.menu.removeItem(R.id.iAdmin)
                    }

                    //loadingCover.visibility = View.GONE
                    binding.rlLoadingCover.visibility = View.GONE
                }
            }
        }
        catch (e: Exception)
        {
            GlobalClass.InformUser(getString(R.string.errorText), "${e.toString()}", this)
        }
        //---------------------------------------------------------------------------------------------------------------------------------------------------------

    }



}