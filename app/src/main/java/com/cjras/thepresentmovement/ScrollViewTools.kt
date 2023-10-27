package com.cjras.thepresentmovement

import android.content.Context
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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


     fun datePicker(context: Fragment, startPrompt: Boolean, entryField: TextView)
    {

        var entryPrompt = context.getString(R.string.dateEndPrompt)

        if (startPrompt)
        {
            entryPrompt = context.getString(R.string.dateStartPrompt)
        }

        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText(entryPrompt)
        val picker = builder.build()
        picker.show(context.childFragmentManager, picker.toString())

        picker.addOnPositiveButtonClickListener {
            // Respond to positive button click.
            val selectedDate = SimpleDateFormat("dd/MM/yy")
            entryField.text = selectedDate.format(picker.selection)
        }

        picker.addOnNegativeButtonClickListener {
            // Respond to negative button click.
            entryField.text = context.getString(R.string.blankDate)
        }

        picker.addOnCancelListener {
            // Respond to cancel button click.
            entryField.text = context.getString(R.string.blankDate)
        }
    }






}