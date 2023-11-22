package com.cjras.thepresentmovement

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import com.cjras.thepresentmovement.databinding.FragmentAdminBinding
import com.cjras.thepresentmovement.databinding.FragmentAdminContentListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class admin_content_list : Fragment() {

    private var _binding: FragmentAdminContentListBinding? = null
    private val binding get() = _binding!!

    private var selectedFunction: String? = ""
    private val filterManager = FilterListFunctions()
    private val scrollViewUtils = ScrollViewTools()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentAdminContentListBinding.inflate(inflater, container, false)
        val view = binding.root

        //the function the screen is performing
        selectedFunction = arguments?.getString("selectedFunction")

        //set the screen function header
        binding.tvScreenTitle.text = selectedFunction


        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //Data population
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        try {
            GlobalClass.checkUser(this)

            MainScope().launch {
                if (GlobalClass.UpdateDataBase == true) {

                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                        View.VISIBLE

                    withContext(Dispatchers.Default) {

                        var databaseManager = DatabaseManager()

                        databaseManager.updateFromDatabase()
                    }
                }
                UpdateUI()
            }
        } catch (e: Exception) {
            GlobalClass.InformUser(
                getString(R.string.errorText),
                "${e.toString()}",
                requireContext()
            )
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //When the refresh button is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.ivRefresh.setOnClickListener()
        {
            try {
                //call method to refresh the current fragment to pull new information from the database manually
                GlobalClass.RefreshFragment(this@admin_content_list)
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
        //When the filter menu arrow is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.llExpansionMenu.setOnClickListener()
        {

            //instance of animation manager
            val animationManager = AnimationHandler()

            //if the loaded screen contains user records
            if (selectedFunction == getString(R.string.accountsText))
            {
                //call method to animate the showing/hiding of the user filter menu
                animationManager.rotatingArrowMenu(binding.llExpansionContentContacts, binding.ivExpandArrow)
            }
            else
            {
                //if the loaded screen is not containing user records

                //call method to animate the showing/hiding of the general filter menu
                animationManager.rotatingArrowMenu(binding.llExpansionContent, binding.ivExpandArrow)
            }

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
           //call method to handle the filtering of content according to the start date
           DateChanged()
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //after the end date filter is selected
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tvEndDate.doAfterTextChanged { char ->
            //call method to handle the filtering of content according to the end date
           DateChanged()
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //when the general filter search text changes
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.etSearch.addTextChangedListener { charSequence ->
            when(selectedFunction)
            {
                getString(R.string.announcementsText) -> {
                    //call method to load announcement list, including the search filter
                    filterManager.LoadAnnouncements(charSequence.toString(), binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(),  this@admin_content_list, true)
                }
                getString(R.string.eventsText) -> {
                    //call method to load events list, including the search filter
                    filterManager.LoadEvents(charSequence.toString(), binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@admin_content_list, true)
                }
                getString(R.string.projectsText) -> {
                    //call method to load projects list, including the search filter
                    filterManager.LoadProjects(charSequence.toString(), binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@admin_content_list, true)
                }
            }
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //when the add data button is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tvAddContent.setOnClickListener()
        {
            //create local fragment controller
            val fragmentControl = FragmentManager()

            when(selectedFunction)
            {
                getString(R.string.announcementsText) -> {

                    //if the screen is displaying announcements
                    //new announcement

                    //fragment arguments
                    val args = Bundle()

                    //instance of create announcement fragment
                    val addAnnouncementView = create_announcement()

                    //set instance arguments
                    addAnnouncementView.arguments = args

                    //load fragment instance
                    fragmentControl.replaceFragment(
                        addAnnouncementView,
                        R.id.flContent,
                        parentFragmentManager
                    )


                }
                getString(R.string.accountsText) -> {
                    //if the screen is displaying user accounts
                    //new account

                    //call method to load create account fragment
                    fragmentControl.replaceFragment(create_account(), R.id.flContent, parentFragmentManager)
                }
                getString(R.string.eventsText) -> {
                    //if the screen is displaying events
                    //new event

                    //fragment arguments
                    val args = Bundle()

                    //instance of create event fragment
                    val addEventView = create_event()

                    //set instance arguments
                    addEventView.arguments = args

                    //load fragment instance
                    fragmentControl.replaceFragment(
                        addEventView,
                        R.id.flContent,
                        parentFragmentManager
                    )

                }
                getString(R.string.projectsText) -> {
                    //if the screen is displaying projects
                    //new project

                    //fragment arguments
                    val args = Bundle()

                    //instance of create project fragment
                    val addProjectView = add_project()

                    //set instance arguments
                    addProjectView.arguments = args

                    //load fragment instance
                    fragmentControl.replaceFragment(
                        addProjectView,
                        R.id.flContent,
                        parentFragmentManager
                    )

                }
            }
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


        // Inflate the layout for this fragment
        return view
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to update the screen data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    private fun UpdateUI()
    {
        try
        {

            when(selectedFunction)
            {
                getString(R.string.announcementsText) -> {
                    //call method to load filtered announcement list
                    filterManager.LoadAnnouncements("", binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(),  this@admin_content_list, true)
                }
                getString(R.string.accountsText) -> {
                   //call method to load filtered accounts/contacts list
                    filterManager.loadContacts("",  "All", binding.llListContent, this, true)

                    //call method to populate the member filter type spinner filter with member types
                    filterManager.populateMemberTypes(binding.spnMemberTypes, requireActivity(), true)


                    // Set an OnItemSelectedListener to handle item selection
                    binding.spnMemberTypes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                            //the selected item
                            val selectedText = binding.spnMemberTypes.selectedItem.toString()

                            //if selective user filtering must be applied
                            if (selectedText == "All")
                            {
                                //call method to load the list of contacts, ignoring the member type filter
                                filterManager.loadContacts(binding.etSearch.text.toString(), "All", binding.llListContent, this@admin_content_list, true)
                            }
                            else
                            {
                                //call method to load the list of contacts, including the member type filter
                                filterManager.loadContacts(binding.etSearch.text.toString(), selectedText, binding.llListContent, this@admin_content_list, true)
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            // Handle the case where nothing is selected
                        }
                    }



                    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                    //when the account search filter text is changed
                    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                    binding.etSearchContacts.addTextChangedListener { charSequence ->
                        //call method to filter the list of contacts according to the search bar text
                        filterManager.loadContacts(charSequence.toString(), binding.spnMemberTypes.selectedItem.toString(), binding.llListContent, this, true)
                    }
                    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

                }
                getString(R.string.eventsText) -> {
                    //call method to load filtered events list
                    filterManager.LoadEvents("", binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@admin_content_list, true)
                }
                getString(R.string.projectsText) -> {
                    //call method to load filtered projects list
                    filterManager.LoadProjects("", binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@admin_content_list, true)
                }

            }

            //hide the loading screen
            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE
        }
        catch (e: Exception) {
            GlobalClass.InformUser(getString(R.string.errorText), "$e", requireContext())
        }
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //when the date filters are changed
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    private fun DateChanged()
    {
        when(selectedFunction)
        {
            getString(R.string.announcementsText) -> {
                //call method to load announcement list according to date filters
                filterManager.LoadAnnouncements(binding.etSearch.text.toString(), binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(),  this@admin_content_list, true)
            }
            getString(R.string.eventsText) -> {
                //call method to load events list according to date filters
                filterManager.LoadEvents(binding.etSearch.text.toString(), binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@admin_content_list, true)
            }
            getString(R.string.projectsText) -> {
                //call method to load projects list according to date filters
                filterManager.LoadProjects(binding.etSearch.text.toString(), binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@admin_content_list, true)
            }
        }
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


}