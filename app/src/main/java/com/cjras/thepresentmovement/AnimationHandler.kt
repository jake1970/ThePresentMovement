package com.cjras.thepresentmovement

import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView

class AnimationHandler {

    fun rotatingArrowMenu (menuContents: View, menuArrow: ImageView)
    {
        if (menuContents.visibility == View.VISIBLE)
        {

            val rotateAnimation = RotateAnimation(
                90.0f,
                0.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )

            rotateAnimation.interpolator = DecelerateInterpolator()
            rotateAnimation.repeatCount = 0
            rotateAnimation.duration = 150
            rotateAnimation.fillAfter = true
            menuArrow.startAnimation(rotateAnimation)

            menuContents.visibility = View.GONE
        }
        else
        {
            val rotateAnimation = RotateAnimation(
                0.0f,
                90.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )

            rotateAnimation.interpolator = DecelerateInterpolator()
            rotateAnimation.repeatCount = 0
            rotateAnimation.duration = 150
            rotateAnimation.fillAfter = true
            menuArrow.startAnimation(rotateAnimation)

            menuContents.visibility = View.VISIBLE
        }
    }
}
