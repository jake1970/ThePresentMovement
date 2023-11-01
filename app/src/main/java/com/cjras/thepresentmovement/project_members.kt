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
            MainScope().launch {
                if (GlobalClass.UpdateDataBase == true) {

                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE

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
                "${e}",
                requireContext()
            )
        }
        //---------------------------------------------------------------------------------------------------------------------------------------------------------



        binding.ivRefresh.setOnClickListener()
        {
            try {
                GlobalClass.RefreshFragment(this@project_members)
            }
            catch (e: Exception) {
                GlobalClass.InformUser(
                    getString(R.string.errorText),
                    "${e}",
                    requireContext()
                )
            }
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




        // Inflate the layout for this fragment
        return view
    }


    private fun UpdateUI()
    {
        val projectID = arguments?.getInt("selectedProjectID", 0)
        val projectTitle = arguments?.getString("selectedProjectTitle", "Unknown")


        binding.tvProjectTitle.text = projectTitle

        var currentProjectMembers = arrayListOf<UserDataClass>()

        if (projectID != 0) {

            filterManager.populateMemberTypes(binding.spnMemberTypes, requireActivity(), true)


            // Set an OnItemSelectedListener to handle item selection
            binding.spnMemberTypes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                    val selectedText = binding.spnMemberTypes.selectedItem.toString()

                    if (selectedText == "All")
                    {
                        loadProjectMembers(currentProjectMembers, binding.etSearch.text.toString(), "All", binding.llParticipants)
                    }
                    else
                    {
                        //call method to filter list
                        loadProjectMembers(currentProjectMembers, binding.etSearch.text.toString(), binding.spnMemberTypes.selectedItem.toString(), binding.llParticipants)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle the case where nothing is selected
                }
            }


            binding.etSearch.addTextChangedListener { charSequence ->
                loadProjectMembers(currentProjectMembers, charSequence.toString(), binding.spnMemberTypes.selectedItem.toString(), binding.llParticipants)
            }

            binding.tvProjectTitle.setOnClickListener()
            {
                GlobalClass.InformUser("", projectTitle!!, requireActivity())
            }

            for (userProject in GlobalClass.UserProjects) {

                var selectedUserIndex = GlobalClass.Users.indexOfLast { it.UserID == userProject.UserID && userProject.ProjectID == projectID }

                if (selectedUserIndex != -1) {
                    //if the user is in the current project

                    currentProjectMembers.add(GlobalClass.Users[selectedUserIndex])
                }

                loadProjectMembers(currentProjectMembers, "", "All", binding.llParticipants)
            }
        }
    }


    private fun loadProjectMembers(
        memberList: ArrayList<UserDataClass>,
        searchTerm: String,
        memberTypeFilter: String,
        displayLayout: LinearLayout
    ) {
        displayLayout.removeAllViews()
        val scrollViewUtils = ScrollViewTools()


        for (user in memberList) {

            //if (user != GlobalClass.currentUser) {


                if (user.getFullName().lowercase()
                        .contains(searchTerm.lowercase()) || searchTerm == ""
                ) {


                    val currentMemberTypeString =
                        MemberTypeDataClass().getSingleMemberType(user.MemberTypeID)



                    if (currentMemberTypeString == memberTypeFilter || memberTypeFilter == "All") {


                        var newContact = contact_card(requireActivity())

                        newContact.binding.tvContactName.text =
                            user.getFullName()


                        var memberType =
                            MemberTypeDataClass().getSingleMemberType(user.MemberTypeID)
                        newContact.binding.tvContactRole.text = memberType


                        newContact.setOnClickListener()
                        {


                            filterManager.invokeExpandedContactsView(
                                user.UserID,
                                this@project_members
                            )

                        }
                        //add the new view
                        displayLayout.addView(newContact)

                        //add space between custom cards
                        scrollViewUtils.generateSpacer(displayLayout, requireActivity(), 14)

                    }
                }
                // }

        }
    }

}