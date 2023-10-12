package com.cjras.thepresentmovement

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import com.cjras.thepresentmovement.databinding.FragmentContactsBinding
import com.cjras.thepresentmovement.databinding.FragmentNoticesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


/**
 * A simple [Fragment] subclass.
 * Use the [contacts.newInstance] factory method to
 * create an instance of this fragment.
 */
class contacts : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        val view = binding.root


        //---------------------------------------------------------------------------------------------
        //test code for custom components
        //---------------------------------------------------------------------------------------------
        for (i in 1..8) {

            val activityLayout = binding.llContactsList;
            var newContact = contact_card(activity)

            newContact.binding.tvContactName.text = "Jake Young"
            newContact.binding.tvContactRole.text = "Senior Member"

            newContact.setOnClickListener()
            {
                //create local fragment controller
                val fragmentControl = FragmentManager()

                val expandedContactView = expanded_contact()
                val args = Bundle()

                args.putBoolean("myProfile", false)
                args.putString("contactFullName", "083 444 5566")
                args.putString("contactRole", "083 444 5566")
                args.putString("contactQuote", "083 444 5566")
                args.putString("contactPhoneNumber", "083 444 5566")
                args.putString("contactEmailAddress", "083 444 5566")
                args.putString("contactCompanyName", "083 444 5566")
                args.putString("contactLinkedIn", "083 444 5566")
                args.putString("contact", "083 444 5566")

                expandedContactView.arguments = args

                fragmentControl.replaceFragment(expandedContactView, R.id.flContent, parentFragmentManager)

                //fragmentControl.replaceFragment(expanded_contact(), R.id.flContent, parentFragmentManager)
            }


            //add the new view
            activityLayout.addView(newContact)


            val scale = requireActivity().resources.displayMetrics.density
            val pixels = (14 * scale + 0.5f)

            val spacer = Space(activity)
            spacer.minimumHeight = pixels.toInt()
            activityLayout.addView(spacer)

        }
        //---------------------------------------------------------------------------------------------


        // Inflate the layout for this fragment
        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}