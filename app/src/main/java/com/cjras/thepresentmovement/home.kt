package com.cjras.thepresentmovement

import android.content.ClipData
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Space
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.children
import com.cjras.thepresentmovement.databinding.FragmentHomeBinding
import com.cjras.thepresentmovement.databinding.FragmentNoticesBinding
import com.google.android.gms.tasks.Tasks.await
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass.
 * Use the [home.newInstance] factory method to
 * create an instance of this fragment.
 */
class home : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

   //private lateinit var loadingCover : ViewGroup


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root


        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        //initial data population
        //---------------------------------------------------------------------------------------------------------------------------------------------------------


        try {



            //Read Data
            MainScope().launch{

                if (GlobalClass.UpdateDataBase == true) {
                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE
                withContext(Dispatchers.Default) {

                        var databaseManager = DatabaseManager()

                        databaseManager.updateFromDatabase()

                        //get the users image
                        GlobalClass.currentUserImage = databaseManager.getUserImage(
                            requireContext(),
                            GlobalClass.currentUser.UserID,
                            GlobalClass.currentUser.HasImage
                        )
                    }
                }


                    UpdateUI()

                    //hide admin menu
                    if (GlobalClass.currentUser.MemberTypeID != 2)
                    {
                        requireActivity().findViewById<BottomNavigationView>(R.id.bnvHomeNavigation).menu.removeItem(R.id.iAdmin)
                    }

            }
        }
        catch (e: Error)
        {
            GlobalClass.InformUser(getString(R.string.errorText), "${e.toString()}", requireContext())
        }
        //---------------------------------------------------------------------------------------------------------------------------------------------------------

        binding.ivRefresh.setOnClickListener()
        {
            GlobalClass.RefreshFragment(this)
        }

        // Inflate the layout for this fragment
        return view
    }

    fun UpdateUI ()
    {

        try {



            val eventCount = GlobalClass.Events.count()
            val projectCount = GlobalClass.Projects.count()

            var eventText = getString(R.string.eventsText)
            var projectText = getString(R.string.projectsText)

            if (eventCount == 1)
            {
                eventText = getString(R.string.eventsTextSingle)
            }

            if (projectCount == 1)
            {
                projectText = getString(R.string.projectsTextSingle)
            }

            binding.tvEvents.text = Html.fromHtml(eventCount.toString() + "<small>" + "<small>" + "<small>" + " " + getString(R.string.upcomingText) + "</small>" + "</small>" + "</small>" + "<br />" + eventText)
            binding.tvProjects.text = Html.fromHtml(projectCount.toString() + "<small>" + "<small>" + "<small>" + " " + getString(R.string.upcomingText) + "</small>" + "</small>" + "</small>" + "<br />" + projectText)





            var databaseManager = DatabaseManager()
            val scrollViewUtils = ScrollViewTools()
            val activityLayout = binding.llFeed;


                val feedSize = 10
                var recentEvents = GlobalClass.Events.take(feedSize).sortedBy { it.EventDate }
                var recentProjects = GlobalClass.Projects.take(feedSize).sortedBy { it.ProjectDate }

                var combinedFeed = recentEvents + recentProjects

                val sortedList = combinedFeed.sortedBy {
                    when(it) {
                        is EventDataClass -> it.EventDate
                        is ProjectDataClass -> it.ProjectDate
                        else -> throw IllegalArgumentException("Unknown type for sorting!")
                    }
                }

                for (i in sortedList.indices)
                {
                    val newFeedItem = home_feed_card(activity)

                    if (sortedList[i] is EventDataClass)
                    {
                        newFeedItem.binding.tvEntryTitle.text = (sortedList[i] as EventDataClass).EventTitle
                        newFeedItem.binding.tvEntryText.text = getString(R.string.newEventAddedText)
                        newFeedItem.binding.tvEntryDate.text = (sortedList[i] as EventDataClass).EventDate.format(DateTimeFormatter.ofPattern("dd/MM/yy"));
                        newFeedItem.binding.ivEntryIcon.setImageBitmap(databaseManager.getEventDefaultImage(requireActivity()))

                        newFeedItem.setOnClickListener()
                        {
                            //open event full view
                        }
                    }
                    else
                    {
                        newFeedItem.binding.tvEntryTitle.text = (sortedList[i] as ProjectDataClass).ProjectTitle
                        newFeedItem.binding.tvEntryText.text = getString(R.string.newProjectAddedText)
                        newFeedItem.binding.tvEntryDate.text = (sortedList[i] as ProjectDataClass).ProjectDate.format(DateTimeFormatter.ofPattern("dd/MM/yy"));
                        newFeedItem.binding.ivEntryIcon.setImageBitmap(databaseManager.getProjectDefaultImage(requireActivity()))

                        newFeedItem.setOnClickListener()
                        {
                            //open project full view
                        }
                    }

                    //add the new view
                    activityLayout.addView(newFeedItem)

                    //add space between custom cards
                    scrollViewUtils.generateSpacer(activityLayout, requireActivity(), 14)
                }


            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE

        }
        catch (e: Exception)
        {
            GlobalClass.InformUser(getString(R.string.errorText), "${e.toString()}", requireContext())
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}