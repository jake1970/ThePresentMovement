package com.cjras.thepresentmovement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.cjras.thepresentmovement.databinding.FragmentAdminBinding
import com.cjras.thepresentmovement.databinding.FragmentCreateAnnouncementBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class create_announcement : Fragment() {

    private var _binding: FragmentCreateAnnouncementBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentCreateAnnouncementBinding.inflate(inflater, container, false)
        val view = binding.root




        return view;
    }
}