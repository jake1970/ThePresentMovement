package com.cjras.thepresentmovement


import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.cjras.thepresentmovement.databinding.FragmentNoticesBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class notices : Fragment() {

    private var _binding: FragmentNoticesBinding? = null
    private val binding get() = _binding!!
    private val scrollViewUtils = ScrollViewTools()
    private val filterManager = FilterListFunctions()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNoticesBinding.inflate(inflater, container, false)
        val view = binding.root

        try {

            //Read Data
            MainScope().launch{

                if (GlobalClass.UpdateDataBase == true) {
                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE
                withContext(Dispatchers.Default) {

                    var databaseManager = DatabaseManager()
                    databaseManager.updateFromDatabase()
                }
                }
                UpdateUI()
            }
        }
        catch (e: Error)
        {
            GlobalClass.InformUser(getString(R.string.errorText), "${e.toString()}", requireContext())
        }

        binding.tvStartDate.setOnClickListener(){
            scrollViewUtils.datePicker(this, true, binding.tvStartDate)

        }

        binding.tvEndDate.setOnClickListener(){
            scrollViewUtils.datePicker(this, false, binding.tvEndDate)
        }

        binding.tvStartDate.doAfterTextChanged { char ->
            filterManager.LoadAnnouncements(binding.etSearch.text.toString(), binding.llNotices, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), requireActivity())
        }

        binding.tvEndDate.doAfterTextChanged { char ->
            filterManager.LoadAnnouncements(binding.etSearch.text.toString(), binding.llNotices, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), requireActivity())
        }


        binding.ivRefresh.setOnClickListener()
        {
            GlobalClass.RefreshFragment(this)
        }


        binding.etSearch.addTextChangedListener { charSequence ->

            filterManager.LoadAnnouncements(charSequence.toString(), binding.llNotices, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(),  requireActivity())
        }

        return view
    }


    private fun UpdateUI()
    {


        try {


            filterManager.LoadAnnouncements("", binding.llNotices, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(),  requireActivity())
            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE

        }
        catch (e: Exception)
        {
            GlobalClass.InformUser(getString(R.string.errorText), "${e.toString()}", requireContext())
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}