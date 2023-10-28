package com.cjras.thepresentmovement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.cjras.thepresentmovement.databinding.FragmentAdminBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class admin : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAdminBinding.inflate(inflater, container, false)
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
                requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE
            }
        }
        catch (e: Error)
        {
            GlobalClass.InformUser(getString(R.string.errorText), "${e.toString()}", requireContext())
        }


        binding.tvManageAccounts.setOnClickListener()
        {
            LaunchAdminContentList(getString(R.string.accountsText))
        }

        binding.tvManageEvents.setOnClickListener()
        {
            LaunchAdminContentList(getString(R.string.eventsText))
        }

        binding.tvManageProjects.setOnClickListener()
        {
            LaunchAdminContentList(getString(R.string.projectsText))
        }

        binding.tvManageAnnouncements.setOnClickListener()
        {
            LaunchAdminContentList(getString(R.string.announcementsText))
        }

        binding.ivRefresh.setOnClickListener()
        {
            GlobalClass.RefreshFragment(this)
        }



        // Inflate the layout for this fragment
        return view
    }

    private fun LaunchAdminContentList(screenTitle: String)
    {

        when(screenTitle)
        {
            getString(R.string.announcementsText) -> {
                val screenTitle = screenTitle
            }
            getString(R.string.accountsText) -> {
                val screenTitle = screenTitle
            }
            getString(R.string.eventsText) -> {
                val screenTitle = screenTitle
            }
            getString(R.string.projectsText) -> {
                val screenTitle = screenTitle
            }
            else -> {
                val screenTitle = ""
            }
        }

        //create local fragment controller
        val fragmentControl = FragmentManager()

        val adminContentList = admin_content_list()
        val args = Bundle()

        args.putString("selectedFunction", screenTitle)

        adminContentList.arguments = args

        fragmentControl.replaceFragment(adminContentList, R.id.flContent, parentFragmentManager)

    }


}