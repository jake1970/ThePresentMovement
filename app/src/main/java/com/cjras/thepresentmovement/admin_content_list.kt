package com.cjras.thepresentmovement

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RelativeLayout
import android.widget.TextView
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentAdminContentListBinding.inflate(inflater, container, false)
        val view = binding.root

        selectedFunction = arguments?.getString("selectedFunction")
        binding.tvScreenTitle.text = selectedFunction

        try {
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
        } catch (e: Error) {
            GlobalClass.InformUser(
                getString(R.string.errorText),
                "${e.toString()}",
                requireContext()
            )
        }

        binding.llHeader.setOnClickListener()
        {
            fragmentManager?.popBackStackImmediate()
            /*
            //create local fragment controller
            val fragmentControl = FragmentManager()

            //go back the the general contacts page
            fragmentControl.replaceFragment(admin(), R.id.flContent, parentFragmentManager)
             */

        }

        binding.ivRefresh.setOnClickListener()
        {
            GlobalClass.RefreshFragment(this)
        }

        binding.llExpansionMenu.setOnClickListener()
        {

            val animationManager = AnimationHandler()

            if (selectedFunction == getString(R.string.accountsText))
            {
                animationManager.rotatingArrowMenu(binding.llExpansionContentContacts, binding.ivExpandArrow)
            }
            else
            {
                animationManager.rotatingArrowMenu(binding.llExpansionContent, binding.ivExpandArrow)
            }

        }


        binding.tvStartDate.setOnClickListener(){
            scrollViewUtils.datePicker(this, true, binding.tvStartDate)
        }

        binding.tvEndDate.setOnClickListener(){
            scrollViewUtils.datePicker(this, false, binding.tvEndDate)

        }

        binding.tvStartDate.doAfterTextChanged { char ->
           DateChanged()
        }

        binding.tvEndDate.doAfterTextChanged { char ->
           DateChanged()
        }


        binding.etSearch.addTextChangedListener { charSequence ->
            when(selectedFunction)
            {
                getString(R.string.announcementsText) -> {
                    //load announcement list
                    filterManager.LoadAnnouncements(charSequence.toString(), binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(),  this@admin_content_list, true)
                }
                getString(R.string.eventsText) -> {
                    //load events/text
                    filterManager.LoadEvents(charSequence.toString(), binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), requireActivity())
                }
                getString(R.string.projectsText) -> {
                    //load projects text
                    filterManager.LoadProjects(charSequence.toString(), binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@admin_content_list, true)
                }
            }
        }






        binding.tvAddContent.setOnClickListener()
        {
            //create local fragment controller
            val fragmentControl = FragmentManager()

            when(selectedFunction)
            {
                getString(R.string.announcementsText) -> {
                    //new announcement

                    //uncomment this line and replace "register_user()" with the new announcement fragment
                    //fragmentControl.replaceFragment(register_user(), R.id.flContent, parentFragmentManager)

                }
                getString(R.string.accountsText) -> {
                    //new account

                    fragmentControl.replaceFragment(register_user(), R.id.flContent, parentFragmentManager)
                }
                getString(R.string.eventsText) -> {
                    //new event

                    //uncomment this line and replace "register_user()" with the new event fragment
                    //fragmentControl.replaceFragment(register_user(), R.id.flContent, parentFragmentManager)

                }
                getString(R.string.projectsText) -> {
                    //new project

                    //create local fragment controller
                    val fragmentControl = FragmentManager()
                    val args = Bundle()


                    val addProjectView = add_project()

                    //args.putBoolean("editMode", false)

                    addProjectView.arguments = args

                    fragmentControl.replaceFragment(
                        addProjectView,
                        R.id.flContent,
                        parentFragmentManager
                    )

                }
            }
        }


        // Inflate the layout for this fragment
        return view
    }

    private fun UpdateUI()
    {
        try
        {

            when(selectedFunction)
            {
                getString(R.string.announcementsText) -> {
                    //load announcement list
                    filterManager.LoadAnnouncements("", binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(),  this@admin_content_list, true)
                }
                getString(R.string.accountsText) -> {
                   //load accounts/contacts list



                    filterManager.loadContacts("",  "All", binding.llListContent, this, true)

                    filterManager.populateMemberTypes(binding.spnMemberTypes, requireActivity())


                    // Set an OnItemSelectedListener to handle item selection
                    binding.spnMemberTypes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                            val selectedText = binding.spnMemberTypes.selectedItem.toString()

                            if (selectedText == "All")
                            {
                                filterManager.loadContacts(binding.etSearch.text.toString(), "All", binding.llListContent, this@admin_content_list, true)
                            }
                            else
                            {
                                //call method to filter list
                                filterManager.loadContacts(binding.etSearch.text.toString(), selectedText, binding.llListContent, this@admin_content_list, true)
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            // Handle the case where nothing is selected
                        }
                    }

                    binding.etSearchContacts.addTextChangedListener { charSequence ->
                        filterManager.loadContacts(charSequence.toString(), binding.spnMemberTypes.selectedItem.toString(), binding.llListContent, this, true)
                    }

                }
                getString(R.string.eventsText) -> {
                    //load events/text
                    filterManager.LoadEvents("", binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), requireActivity())
                }
                getString(R.string.projectsText) -> {
                    //load projects text
                    filterManager.LoadProjects("", binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@admin_content_list, true)
                }
            }


            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE
        }
        catch (e: Exception) {
            GlobalClass.InformUser(getString(R.string.errorText), "$e", requireContext())
        }
    }

    private fun DateChanged()
    {
        when(selectedFunction)
        {
            getString(R.string.announcementsText) -> {
                //load announcement list
                filterManager.LoadAnnouncements(binding.etSearch.text.toString(), binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(),  this@admin_content_list, true)
            }
            getString(R.string.eventsText) -> {
                //load events/text
                filterManager.LoadEvents(binding.etSearch.text.toString(), binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), requireActivity())
            }
            getString(R.string.projectsText) -> {
                //load projects text
                filterManager.LoadProjects(binding.etSearch.text.toString(), binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), this@admin_content_list, true)
            }
        }
    }



}