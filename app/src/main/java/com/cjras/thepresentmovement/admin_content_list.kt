package com.cjras.thepresentmovement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
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

            animationManager.rotatingArrowMenu(binding.llExpansionContent, binding.ivExpandArrow)

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
                    filterManager.LoadAnnouncements("", binding.llListContent, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString(),  requireActivity())
                }
                getString(R.string.accountsText) -> {
                   //load accounts/contacts list
                }
                getString(R.string.eventsText) -> {
                    //load events/text
                }
                getString(R.string.projectsText) -> {
                    //load projects text
                }
            }

            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE
        }
        catch (e: Exception) {
            GlobalClass.InformUser(getString(R.string.errorText), "$e", requireContext())
        }
    }



}