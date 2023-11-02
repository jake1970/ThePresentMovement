package com.cjras.thepresentmovement

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager


class FragmentManager {


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to load a given fragment
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     fun replaceFragment(fragment : Fragment, fragmentContainerID : Int, fragmentManager : FragmentManager) {

        //begin transition to desired fragment
        val fragmentTransaction = fragmentManager.beginTransaction()

        //add fragment reference and generic tag
        fragmentTransaction.add(fragment, "genericTag")

        //add backstack reference so back buttons function
        fragmentTransaction.addToBackStack(null)

        //set the desired fragment to be swapped
        fragmentTransaction.replace(fragmentContainerID, fragment)

        //commit fragment change
        fragmentTransaction.commit()
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


}