package com.cjras.thepresentmovement

import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView

class AnimationHandler {

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to handle the rotation of an arrow and expansion of a menu
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun rotatingArrowMenu (menuContents: View, menuArrow: ImageView)
    {

        if (menuContents.visibility == View.VISIBLE)
        {
            //if the menu is open

            //configure arrow up animation
            val rotateAnimation = RotateAnimation(
                90.0f,
                0.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )

            //apply animation settings
            rotateAnimation.interpolator = DecelerateInterpolator()
            rotateAnimation.repeatCount = 0
            rotateAnimation.duration = 150
            rotateAnimation.fillAfter = true

            //start the animation
            menuArrow.startAnimation(rotateAnimation)

            //hide the menu
            menuContents.visibility = View.GONE
        }
        else
        {
            //if the menu is closed

            //configure arrow down animation
            val rotateAnimation = RotateAnimation(
                0.0f,
                90.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )

            //apply animation settings
            rotateAnimation.interpolator = DecelerateInterpolator()
            rotateAnimation.repeatCount = 0
            rotateAnimation.duration = 150
            rotateAnimation.fillAfter = true

            //start the animation
            menuArrow.startAnimation(rotateAnimation)

            //show the menu
            menuContents.visibility = View.VISIBLE
        }
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
