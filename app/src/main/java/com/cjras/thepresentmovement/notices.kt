package com.cjras.thepresentmovement


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cjras.thepresentmovement.databinding.FragmentNoticesBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat


/**
 * A simple [Fragment] subclass.
 * Use the [notices.newInstance] factory method to
 * create an instance of this fragment.
 */
class notices : Fragment() {

    private var _binding: FragmentNoticesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNoticesBinding.inflate(inflater, container, false)
        val view = binding.root


        binding.tvStartDate.setOnClickListener(){
            datePicker(true, binding.tvStartDate)
        }

        binding.tvEndDate.setOnClickListener(){
            datePicker(false, binding.tvEndDate)
        }

        //---------------------------------------------------------------------------------------------
        //test code for custom components
        //---------------------------------------------------------------------------------------------
        for (i in 1..8) {

            val activityLayout = binding.llNotices;
            var newAnnouncement = announcement_card(activity)

            newAnnouncement.binding.tvAnnouncementTime.text = "10/12/23"
            newAnnouncement.binding.tvAnnouncementTitle.text = "Changes to the Think For Good Event"
            newAnnouncement.binding.tvAnnouncementText.text = "Hello Members\n" +
                    "The think for good event has been postponed until the 26th, I have updated the card in the present movements app.\n" +
                    "Take care\n" +
                    "{Person}"

            //add the new view
            activityLayout.addView(newAnnouncement)

            val scale = requireActivity().resources.displayMetrics.density
            val pixels = (14 * scale + 0.5f)

            val spacer = Space(activity)
            spacer.minimumHeight = pixels.toInt()
            activityLayout.addView(spacer)

        }
        //---------------------------------------------------------------------------------------------

        return view
    }

    private fun datePicker(startPrompt: Boolean, entryField: TextView)
    {

        var entryPrompt = "Select an end date"

        if (startPrompt)
        {
            val entryPrompt = "Select a start date"
        }

        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText(entryPrompt)
        val picker = builder.build()
        picker.show(childFragmentManager, picker.toString())

        picker.addOnPositiveButtonClickListener {
            // Respond to positive button click.
            val selectedDate = SimpleDateFormat("dd/MM/yy")
            entryField.text = selectedDate.format(picker.selection)
        }

        picker.addOnNegativeButtonClickListener {
            // Respond to negative button click.
            entryField.text = getString(R.string.blankDate)
        }

        picker.addOnCancelListener {
            // Respond to cancel button click.
            entryField.text = getString(R.string.blankDate)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}