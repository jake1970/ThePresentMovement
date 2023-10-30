package com.cjras.thepresentmovement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import com.cjras.thepresentmovement.databinding.FragmentAllProjectsBinding
import com.cjras.thepresentmovement.databinding.FragmentNoticesBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class all_projects : Fragment() {

    private var _binding: FragmentAllProjectsBinding? = null
    private val binding get() = _binding!!

    private val scrollViewUtils = ScrollViewTools()
    private val filterManager = FilterListFunctions()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentAllProjectsBinding.inflate(inflater, container, false)
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

        binding.ivRefresh.setOnClickListener()
        {
            GlobalClass.RefreshFragment(this)
        }

        binding.llHeader.setOnClickListener()
        {
            fragmentManager?.popBackStackImmediate()
            /*
            //create local fragment controller
            val fragmentControl = FragmentManager()

            //go back the the general contacts page
            fragmentControl.replaceFragment(home(), R.id.flContent, parentFragmentManager)

             */

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
            filterManager.LoadProjects(binding.etSearch.text.toString(), binding.llUpcomingProjects, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@all_projects, false)
        }

        binding.tvEndDate.doAfterTextChanged { char ->
            filterManager.LoadProjects(binding.etSearch.text.toString(), binding.llUpcomingProjects, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@all_projects, false)
        }


        binding.etSearch.addTextChangedListener { charSequence ->

            filterManager.LoadProjects(charSequence.toString(), binding.llUpcomingProjects, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@all_projects, false)
        }

        // Inflate the layout for this fragment
        return view
    }

    private fun UpdateUI()
    {
        filterManager.LoadProjects("", binding.llUpcomingProjects, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@all_projects, false)

        requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE
    }



/*
    private fun LoadProjects(searchTerm: String, displayLayout: LinearLayout, startDate: String, endDate: String)
    {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
        var databaseManager = DatabaseManager()
       // val scrollViewUtils = ScrollViewTools()
        val activityLayout = binding.llUpcomingProjects;

        displayLayout.removeAllViews()


        for (project in GlobalClass.Projects) {
            if (project.ProjectTitle.lowercase().contains(searchTerm.lowercase()) || project.ProjectCompanyName.lowercase().contains(searchTerm.lowercase()) || searchTerm == "") {

                var startDateFormatted : LocalDate? = null
                var endDateFormatted : LocalDate? = null

                if (startDate != getString(R.string.blankDate)) {
                    startDateFormatted = LocalDate.parse(startDate, formatter)
                }

                if (endDate != getString(R.string.blankDate)) {
                    endDateFormatted = LocalDate.parse(endDate, formatter)
                }

                if (startDate == getString(R.string.blankDate) || (startDateFormatted != null && (project.ProjectDate.isAfter(startDateFormatted!!)  || project.ProjectDate.isEqual(startDateFormatted!!)))) {

                    if (endDate == getString(R.string.blankDate) || (endDateFormatted != null && (project.ProjectDate.isBefore(endDateFormatted!!) || project.ProjectDate.isEqual(endDateFormatted!!)))) {


                        val newProjectCard = home_feed_card(activity)

                        newProjectCard.binding.tvEntryTitle.text = project.ProjectTitle
                        newProjectCard.binding.tvEntryText.text = project.ProjectCompanyName //project company name instead of uppcoming project header
                        newProjectCard.binding.tvEntryDate.text = project.ProjectDate.format(DateTimeFormatter.ofPattern("dd/MM/yy"));
                        newProjectCard.binding.ivEntryIcon.setImageBitmap(databaseManager.getProjectDefaultImage(requireActivity()))

                        newProjectCard.setOnClickListener()
                        {
                            //open project full view
                        }

                        //add the new view
                        activityLayout.addView(newProjectCard)

                        //add space between custom cards
                        scrollViewUtils.generateSpacer(activityLayout, requireActivity(), 14)

                    }
                }
            }
        }
    }

 */
}