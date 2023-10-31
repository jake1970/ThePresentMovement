package com.cjras.thepresentmovement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import com.cjras.thepresentmovement.databinding.FragmentAllEventsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class all_events : Fragment() {


    private var _binding: FragmentAllEventsBinding? = null
    private val binding get() = _binding!!
    private val scrollViewUtils = ScrollViewTools()
    private val filterManager = FilterListFunctions()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        //view binding
        _binding = FragmentAllEventsBinding.inflate(inflater, container, false)
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
        catch (e: Exception)
        {
            GlobalClass.InformUser(getString(R.string.errorText), "${e.toString()}", requireContext())
        }


        binding.ivRefresh.setOnClickListener()
        {
            GlobalClass.RefreshFragment(this)
        }


        binding.llHeader.setOnClickListener()
        {

            fragmentManager?.popBackStackImmediate()

        }


        binding.llExpansionMenu.setOnClickListener()
        {

            val animationManager = AnimationHandler()

            animationManager.rotatingArrowMenu(binding.llExpansionContent, binding.ivExpandArrow)

        }


        binding.tvStartDate.setOnClickListener(){
            scrollViewUtils.datePicker(this, true, binding.tvStartDate)
        }


        binding.tvEndDate.setOnClickListener(){
            scrollViewUtils.datePicker(this, false, binding.tvEndDate)

        }

        binding.tvStartDate.doAfterTextChanged { char ->
            filterManager.LoadEvents(binding.etSearch.text.toString(), binding.llUpcomingEvents, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@all_events, false)
        }

        binding.tvEndDate.doAfterTextChanged { char ->
            filterManager.LoadEvents(binding.etSearch.text.toString(), binding.llUpcomingEvents, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@all_events, false)
        }


        binding.etSearch.addTextChangedListener { charSequence ->

            filterManager.LoadEvents(charSequence.toString(), binding.llUpcomingEvents, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@all_events, false)
        }


        // Inflate the layout for this fragment
        return view
    }


    private fun UpdateUI()
    {

        filterManager.LoadEvents("", binding.llUpcomingEvents, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@all_events, false)

        requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE

    }


}