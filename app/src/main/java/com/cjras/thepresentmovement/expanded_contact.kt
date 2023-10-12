package com.cjras.thepresentmovement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cjras.thepresentmovement.databinding.FragmentContactsBinding
import com.cjras.thepresentmovement.databinding.FragmentExpandedContactBinding


/**
 * A simple [Fragment] subclass.
 * Use the [expanded_contact.newInstance] factory method to
 * create an instance of this fragment.
 */
class expanded_contact : Fragment() { //R.layout.fragment_expanded_contact

    private var _binding: FragmentExpandedContactBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentExpandedContactBinding.inflate(inflater, container, false)
        val view = binding.root


        //----------------------------------------------------------------------------------------------------

        val myProfile = arguments?.getBoolean("myProfile")
        val myPhoneNumber = arguments?.getString("myPhoneNumber")

        //binding.tfContactNumber.text  = getString("Your text")//myPhoneNumber.toString()

        if (myProfile == true)
        {

        }

        binding.tfContactNumber.setText(myPhoneNumber) // = getString("Your text")//myPhoneNumber.toString()

        //----------------------------------------------------------------------------------------------------


        binding.ivBackArrow.setOnClickListener()
        {
            //create local fragment controller
            val fragmentControl = FragmentManager()
            fragmentControl.replaceFragment(contacts(), R.id.flContent, parentFragmentManager)

        }


        // Inflate the layout for this fragment
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}