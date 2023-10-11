package com.cjras.thepresentmovement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cjras.thepresentmovement.databinding.FragmentContactsBinding
import com.cjras.thepresentmovement.databinding.FragmentNoticesBinding


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

        // Inflate the layout for this fragment
        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}