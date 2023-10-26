package com.cjras.thepresentmovement

import android.widget.LinearLayout
import android.widget.Space
import androidx.fragment.app.FragmentActivity

class ScrollViewTools {

    //---------------------------------------------------------------------------------------------
    //method to generate a space between custom list items
    //---------------------------------------------------------------------------------------------
    fun generateSpacer(activityLayout: LinearLayout, thisFragment: FragmentActivity, spaceSize: Int)
    {
        //define the scale of the measurements
        val scale = thisFragment.resources.displayMetrics.density

        //set the siz in pixels
        val pixels = (spaceSize * scale + 0.5f)

        //create the spacer
        val spacer = Space(thisFragment)

        //set the spacer height
        spacer.minimumHeight = pixels.toInt()

        //add the spacer
        activityLayout.addView(spacer)
    }
    //---------------------------------------------------------------------------------------------

}