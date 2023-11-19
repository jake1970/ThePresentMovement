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


        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //Data population
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        try {

            GlobalClass.checkUser(this)

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
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //When the refresh button is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.ivRefresh.setOnClickListener()
        {
            try {
                //call method to refresh the current fragment to pull new information from the database manually
                GlobalClass.RefreshFragment(this@all_events)
            }
            catch (e: Exception) {
                //call method to show the error
                GlobalClass.InformUser(
                    getString(R.string.errorText),
                    "$e",
                    requireContext()
                )
            }
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //When the page header (title and back button) is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.llHeader.setOnClickListener()
        {
            //navigate one fragment backwards in the stack
            fragmentManager?.popBackStackImmediate()
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //when the filter dropdown is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.llExpansionMenu.setOnClickListener()
        {
            //animation handler instance
            val animationManager = AnimationHandler()

            //call method to animate arrow rotation and menu show/hide
            animationManager.rotatingArrowMenu(binding.llExpansionContent, binding.ivExpandArrow)
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //when the start date filter is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tvStartDate.setOnClickListener(){
            //call method to show the start date picker
            scrollViewUtils.datePicker(this, true, binding.tvStartDate)
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //when the end date filter is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tvEndDate.setOnClickListener(){
            //call method to show the end date picker
            scrollViewUtils.datePicker(this, false, binding.tvEndDate)
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //after the start date filter is selected
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tvStartDate.doAfterTextChanged { char ->
            //call method to filter the list of events according to the start date
            filterManager.LoadEvents(binding.etSearch.text.toString(), binding.llUpcomingEvents, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@all_events, false)
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //after the end date filter is selected
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tvEndDate.doAfterTextChanged { char ->
            //call method to filter the list of events according to the end date
            filterManager.LoadEvents(binding.etSearch.text.toString(), binding.llUpcomingEvents, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@all_events, false)
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //when the search bar text is changed
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.etSearch.addTextChangedListener { charSequence ->
            //call method to filter the list of events according to the search bar text
            filterManager.LoadEvents(charSequence.toString(), binding.llUpcomingEvents, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@all_events, false)
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


        // Inflate the layout for this fragment
        return view
    }



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to update the screen data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private fun UpdateUI()
    {

        //method to load the initial list of events, unfiltered
        filterManager.LoadEvents("", binding.llUpcomingEvents, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@all_events, false)

        //hide the loading screen
        requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE

    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

}