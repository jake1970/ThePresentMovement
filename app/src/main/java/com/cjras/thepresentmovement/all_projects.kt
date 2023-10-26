package com.cjras.thepresentmovement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.cjras.thepresentmovement.databinding.FragmentAllProjectsBinding
import com.cjras.thepresentmovement.databinding.FragmentNoticesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter


class all_projects : Fragment() {

    private var _binding: FragmentAllProjectsBinding? = null
    private val binding get() = _binding!!

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

        binding.ivBackArrow.setOnClickListener()
        {
            //create local fragment controller
            val fragmentControl = FragmentManager()

            //go back the the general contacts page
            fragmentControl.replaceFragment(home(), R.id.flContent, parentFragmentManager)

        }


        // Inflate the layout for this fragment
        return view
    }

    private fun UpdateUI()
    {
        var databaseManager = DatabaseManager()
        val scrollViewUtils = ScrollViewTools()
        val activityLayout = binding.llUpcomingProjects;

        for (project in GlobalClass.Projects)
        {
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

        requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE
    }

}