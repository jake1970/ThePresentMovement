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

    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to generate a space between custom list items
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
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
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to show the material date picker to the user
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     fun datePicker(context: Fragment, startPrompt: Boolean, entryField: TextView)
    {

        //the prompt shown to the user when clicking on the date entry component
        var entryPrompt = context.getString(R.string.dateEndPrompt)

        //check if the prompt is for a start date
        if (startPrompt)
        {
            //set the prompt to the start date prompt
            entryPrompt = context.getString(R.string.dateStartPrompt)
        }

        //new material date picker
        val builder = MaterialDatePicker.Builder.datePicker()

        //set popup prompt
        builder.setTitleText(entryPrompt)

        //instantiate and show the date picker popup
        val picker = builder.build()
        picker.show(context.childFragmentManager, picker.toString())


        picker.addOnPositiveButtonClickListener {
            // Respond to positive button click.
            val selectedDate = SimpleDateFormat("dd/MM/yy")

            //set the component where the date is displayed to show the date
            entryField.text = selectedDate.format(picker.selection)
        }

        picker.addOnNegativeButtonClickListener {
            // Respond to negative button click.

            //reset the component where the date is displayed
            entryField.text = context.getString(R.string.blankDate)
        }

        picker.addOnCancelListener {
            // Respond to cancel button click.

            //reset the component where the date is displayed
            entryField.text = context.getString(R.string.blankDate)
        }
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

}