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

        binding.ivBackArrow.setOnClickListener()
        {
            //create local fragment controller
            val fragmentControl = FragmentManager()

            //go back the the general contacts page
            fragmentControl.replaceFragment(admin(), R.id.flContent, parentFragmentManager)

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


        //-----------------------------------------------------------------------------------------------------------------------------------
        //test nav code for adam create user
        //-----------------------------------------------------------------------------------------------------------------------------------
            binding.ivLogo.setOnClickListener()
            {

                //create local fragment controller
                val fragmentControl = FragmentManager()

                fragmentControl.replaceFragment(
                    register_user(),
                    R.id.flContent,
                    parentFragmentManager
                )

            }
        //-----------------------------------------------------------------------------------------------------------------------------------

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
                    filterManager.LoadAnnouncements("", binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(),  requireActivity())
                }
                getString(R.string.accountsText) -> {
                   //load accounts/contacts list
                    filterManager.loadContacts("",  "All", binding.llListContent, this)

                    binding.spnMemberTypes.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View,
                            position: Int,
                            id: Long
                        ) {
                            (view as TextView).setTextColor(Color.BLACK) //Change selected text color


                            val selectedText = binding.spnMemberTypes.selectedItem.toString()
                            if (selectedText == "All")
                            {
                                filterManager.loadContacts(binding.etSearchContacts.text.toString(), "All", binding.llListContent, this@admin_content_list)
                            }
                            else
                            {
                                //call method to filter list
                                filterManager.loadContacts(binding.etSearchContacts.text.toString(), selectedText, binding.llListContent, this@admin_content_list)
                            }

                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    })
                }
                getString(R.string.eventsText) -> {
                    //load events/text
                    filterManager.LoadEvents("", binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), requireActivity())
                }
                getString(R.string.projectsText) -> {
                    //load projects text
                    filterManager.LoadProjects("", binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(), requireActivity())
                }
            }


            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE
        }
        catch (e: Exception) {
            GlobalClass.InformUser(getString(R.string.errorText), "$e", requireContext())
        }
    }

/*
code to delete an annoucement
 //8888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888
                                        val currentAnnouncementIndex =
                                            GlobalClass.Announcements.indexOf(announcement)
                                        val currentAnnouncementDocumentIndex =
                                            GlobalClass.documents.allAnnouncmentIds[currentAnnouncementIndex]

                                        MainScope().launch() {
                                            withContext(Dispatchers.Default) {
                                                var databaseManager = DatabaseManager()
                                                databaseManager.deleteAnnouncementFromFirestore(currentAnnouncementDocumentIndex)
                                            }
                                            Toast.makeText(context, "Deleted ${announcement.AnnouncementTitle}", Toast.LENGTH_SHORT).show()
                                        }
                                        //8888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888
 */

}