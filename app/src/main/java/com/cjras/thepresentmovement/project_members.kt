package com.cjras.thepresentmovement

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.widget.addTextChangedListener
import com.cjras.thepresentmovement.databinding.FragmentAddProjectBinding
import com.cjras.thepresentmovement.databinding.FragmentProjectMembersBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class project_members : Fragment() {

    private var _binding: FragmentProjectMembersBinding? = null
    private val binding get() = _binding!!

    private val filterManager = FilterListFunctions()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        //view binding
        _binding = FragmentProjectMembersBinding.inflate(inflater, container, false)
        val view = binding.root


        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        //initial data population
        //---------------------------------------------------------------------------------------------------------------------------------------------------------

        try {

            GlobalClass.checkUser(this)

            MainScope().launch {

                //if new information has been added pull new data from the database
                if (GlobalClass.UpdateDataBase == true) {

                    //show loading screen
                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                        View.VISIBLE

                    withContext(Dispatchers.Default) {

                        var databaseManager = DatabaseManager()

                        //call method to retrieve all data from the database
                        databaseManager.updateFromDatabase()
                    }


                }

                //call method to update the ui when the new database information has been loaded (if required)
                UpdateUI()
            }
        } catch (e: Exception) {
            GlobalClass.InformUser(
                getString(R.string.errorText),
                "${e}",
                requireContext()
            )
        }
        //---------------------------------------------------------------------------------------------------------------------------------------------------------


        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        //When the refresh button is clicked
        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.ivRefresh.setOnClickListener()
        {
            try {
                //call method to reload the fragment
                GlobalClass.RefreshFragment(this@project_members)
            } catch (e: Exception) {
                GlobalClass.InformUser(
                    getString(R.string.errorText),
                    "${e}",
                    requireContext()
                )
            }
        }
        //---------------------------------------------------------------------------------------------------------------------------------------------------------


        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        //When the page header (title and back button) is clicked
        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.llHeader.setOnClickListener()
        {
            //navigate one fragment backwards in the stack
            fragmentManager?.popBackStackImmediate()
        }
        //---------------------------------------------------------------------------------------------------------------------------------------------------------


        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        //When the page header (title and back button) is clicked
        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.llExpansionMenu.setOnClickListener()
        {
            val animationManager = AnimationHandler()
            animationManager.rotatingArrowMenu(binding.llExpansionContent, binding.ivExpandArrow)
        }
        //---------------------------------------------------------------------------------------------------------------------------------------------------------


        // Inflate the layout for this fragment
        return view
    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to update the screen data
    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    private fun UpdateUI() {
        //get the intent values for the given project ID and project Title
        val projectID = arguments?.getInt("selectedProjectID", 0)
        val projectTitle = arguments?.getString("selectedProjectTitle", "Unknown")

        //set page heading to the project title
        binding.tvProjectTitle.text = projectTitle

        //array list of members in the given project
        var currentProjectMembers = arrayListOf<UserDataClass>()


        //if a project was passed (if project ID was passed)
        if (projectID != 0) {

            //call method to populate the spinner with the member types
            filterManager.populateMemberTypes(
                binding.spnMemberTypes,
                requireActivity(),
                true
            ) //specify include all is true to add the prefixing "All" filter option


            //---------------------------------------------------------------------------------------------------------------------------------------------------------
            //Set an OnItemSelectedListener to handle item selection
            //When the spinner value is changed
            //---------------------------------------------------------------------------------------------------------------------------------------------------------
            binding.spnMemberTypes.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long,
                    ) {

                        //the selected item
                        val selectedText = binding.spnMemberTypes.selectedItem.toString()

                        //check if filtering must be applied based on member type
                        if (selectedText == "All") {
                            //call method to populate the list of members without filtering by member type
                            loadProjectMembers(
                                currentProjectMembers,
                                binding.etSearch.text.toString(),
                                "All",
                                binding.llParticipants
                            )
                        } else {
                            //call method to populate the list of members and include filtering by member type
                            loadProjectMembers(
                                currentProjectMembers,
                                binding.etSearch.text.toString(),
                                binding.spnMemberTypes.selectedItem.toString(),
                                binding.llParticipants
                            )
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Handle the case where nothing is selected
                    }
                }
            //---------------------------------------------------------------------------------------------------------------------------------------------------------


            //---------------------------------------------------------------------------------------------------------------------------------------------------------
            //When the search filter is changed
            //---------------------------------------------------------------------------------------------------------------------------------------------------------
            binding.etSearch.addTextChangedListener { charSequence ->

                //call method to filter the list of members including the search bar criteria
                loadProjectMembers(
                    currentProjectMembers,
                    charSequence.toString(),
                    binding.spnMemberTypes.selectedItem.toString(),
                    binding.llParticipants
                )
            }
            //---------------------------------------------------------------------------------------------------------------------------------------------------------


            //---------------------------------------------------------------------------------------------------------------------------------------------------------
            //When the project title is clicked
            //---------------------------------------------------------------------------------------------------------------------------------------------------------
            binding.tvProjectTitle.setOnClickListener()
            {

                //if the project title is larger than the allowed area a marquee is used and indicated by ..., allow the user to click on the project tile to view it fully if this is the case
                //call method to show a popup of the current project title
                GlobalClass.InformUser("", projectTitle!!, requireActivity())
            }
            //---------------------------------------------------------------------------------------------------------------------------------------------------------


            //loop through users in project table entries
            for (userProject in GlobalClass.UserProjects) {

                //look for a record of a user being in the current project
                var selectedUserIndex =
                    GlobalClass.Users.indexOfLast { it.UserID == userProject.UserID && userProject.ProjectID == projectID }

                //if a matching user is found
                if (selectedUserIndex != -1) {

                    //if the user is in the current project
                    //add the found user to the list of members in the project
                    currentProjectMembers.add(GlobalClass.Users[selectedUserIndex])
                }

                //call method to populate the list of members in a project, include the default filtering to show all values, unfiltered
                loadProjectMembers(currentProjectMembers, "", "All", binding.llParticipants)
            }
        }

        //hide the loading screen
        requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------


    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to populate the list of users in a project according to the given filter parameters: search term and what type of member is being looked for
    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    private fun loadProjectMembers(
        memberList: ArrayList<UserDataClass>,
        searchTerm: String,
        memberTypeFilter: String,
        displayLayout: LinearLayout,
    ) {

        //clear the current view containing the list of filters
        displayLayout.removeAllViews()

        //new scroll view handler to create visual spaces between list items
        val scrollViewUtils = ScrollViewTools()


        //loop through all members in he current project
        for (user in memberList) {


            //check if the search text criteria is within the members full name or if the search text criteria was not specified
            if (user.getFullName().lowercase()
                    .contains(searchTerm.lowercase()) || searchTerm == ""
            ) {


                //the current members role type as a string value. eg: 2 = Senior Member
                //call method to get the current member type as a string, pass the member type id
                val currentMemberTypeString =
                    MemberTypeDataClass().getSingleMemberType(user.MemberTypeID)


                //check if the member type criteria matches the current users role, or if the member type criteria is not being used as a search factor
                //if the member type filter is "All" the member type is not being considered when searching
                if (currentMemberTypeString == memberTypeFilter || memberTypeFilter == "All") {

                    //new custom component card to show the matching users
                    var newContact = contact_card(requireActivity())

                    //set the card contact name to the users full name
                    newContact.binding.tvContactName.text =
                        user.getFullName()


                    //set the member type to the text value of the corresponding member type
                    //call method to get the matching member type text value based on the member type id
                    var memberType =
                        MemberTypeDataClass().getSingleMemberType(user.MemberTypeID)

                    //set the member type on the custom card component
                    newContact.binding.tvContactRole.text = memberType


                    //---------------------------------------------------------------------------------------------------------------------------------------------------------
                    //When the custom contact card is clicked
                    //---------------------------------------------------------------------------------------------------------------------------------------------------------
                    newContact.setOnClickListener()
                    {

                        //call method to open the contacts information in the expanded contact fragment
                        //pass the selected cards user id and the current fragment as context
                        filterManager.invokeExpandedContactsView(
                            user.UserID,
                            this@project_members
                        )
                    }
                    //---------------------------------------------------------------------------------------------------------------------------------------------------------


                    //add the new view
                    displayLayout.addView(newContact)

                    //add space between custom cards
                    scrollViewUtils.generateSpacer(displayLayout, requireActivity(), 14)

                }
            }

        }
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------

}