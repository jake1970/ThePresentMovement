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
            //create local fragment controller
            val fragmentControl = FragmentManager()

            //go back the the general contacts page
            fragmentControl.replaceFragment(home(), R.id.flContent, parentFragmentManager)

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
            filterManager.LoadEvents(binding.etSearch.text.toString(), binding.llUpcomingEvents, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), requireActivity())
        }

        binding.tvEndDate.doAfterTextChanged { char ->
            filterManager.LoadEvents(binding.etSearch.text.toString(), binding.llUpcomingEvents, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), requireActivity())
        }


        binding.etSearch.addTextChangedListener { charSequence ->

            filterManager.LoadEvents(charSequence.toString(), binding.llUpcomingEvents, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), requireActivity())
        }


        // Inflate the layout for this fragment
        return view
    }

    private fun UpdateUI()
    {

        filterManager.LoadEvents("", binding.llUpcomingEvents, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), requireActivity())

        requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE

    }

    /*
    private fun LoadEvents(searchTerm: String, displayLayout: LinearLayout, startDate: String, endDate: String)
    {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
        var databaseManager = DatabaseManager()
        val activityLayout = binding.llUpcomingEvents;

        displayLayout.removeAllViews()


        for (event in GlobalClass.Events) {
            if (event.EventTitle.lowercase().contains(searchTerm.lowercase()) || event.EventLink.lowercase().contains(searchTerm.lowercase()) || searchTerm == "") {

                var startDateFormatted : LocalDate? = null
                var endDateFormatted : LocalDate? = null

                if (startDate != getString(R.string.blankDate)) {
                    startDateFormatted = LocalDate.parse(startDate, formatter)
                }

                if (endDate != getString(R.string.blankDate)) {
                    endDateFormatted = LocalDate.parse(endDate, formatter)
                }

                if (startDate == getString(R.string.blankDate) || (startDateFormatted != null && (event.EventDate.isAfter(startDateFormatted!!)  || event.EventDate.isEqual(startDateFormatted!!)))) {

                    if (endDate == getString(R.string.blankDate) || (endDateFormatted != null && (event.EventDate.isBefore(endDateFormatted!!) || event.EventDate.isEqual(endDateFormatted!!)))) {


                        val newEventCard = home_feed_card(activity)

                        newEventCard.binding.tvEntryTitle.text = event.EventTitle
                        newEventCard.binding.tvEntryText.text = event.EventLink
                        newEventCard.binding.tvEntryDate.text = event.EventDate.format(DateTimeFormatter.ofPattern("dd/MM/yy"));
                        newEventCard.binding.ivEntryIcon.setImageBitmap(databaseManager.getEventDefaultImage(requireActivity()))

                        newEventCard.setOnClickListener()
                        {
                            //open event full view
                        }

                        //add the new view
                        activityLayout.addView(newEventCard)

                        //add space between custom cards
                        scrollViewUtils.generateSpacer(activityLayout, requireActivity(), 14)

                    }
                }
            }
        }
    }
     */

}