package com.cjras.thepresentmovement

import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Space
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
import com.google.firebase.auth.FirebaseAuth

class home : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //View Binding
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //Data population
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        try {

            GlobalClass.checkUser(this)

            //Read Data
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


                        //set the current users image
                        //call method to get the current users image from the contact image directory
                        //pass the context, current user ID, and the status of if the user has a set image
                        GlobalClass.currentUserImage = databaseManager.getUserImage(
                            requireContext(),
                            GlobalClass.currentUser.UserID,
                            GlobalClass.currentUser.HasImage
                        )
                    }
                }

                var selectedUserIndex = GlobalClass.Users.indexOfLast { it.UserID == GlobalClass.currentUser.UserID }

                //check if the user doesn't exist
                if (selectedUserIndex == -1) {

                    //call method to log user out of app
                    GlobalClass.logout(requireActivity())

                    //inform user that their account is invalid (has been deleted)
                    Toast.makeText(requireContext(), getString(R.string.accountNoLongerValidText), Toast.LENGTH_SHORT).show()

                }
                else
                {
                    //call method to update the ui when the new database information has been loaded (if required)
                    UpdateUI()

                    //hide admin menu
                    if (GlobalClass.currentUser.MemberTypeID != 2 && GlobalClass.currentUser.MemberTypeID != 3) {
                        requireActivity().findViewById<BottomNavigationView>(R.id.bnvHomeNavigation).menu.removeItem(
                            R.id.iAdmin
                        )
                    }
                }


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
                GlobalClass.RefreshFragment(this@home)
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
        //When the events "card" is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tvEvents.setOnClickListener()
        {
            //create local fragment controller
            val fragmentControl = FragmentManager()

            //call method to open the events page (load the events fragment)
            fragmentControl.replaceFragment(
                all_events(),
                R.id.flContent,
                parentFragmentManager
            )
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //When the projects "card" is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tvProjects.setOnClickListener()
        {
            //create local fragment controller
            val fragmentControl = FragmentManager()

            //call method to open the projects page (load the projects fragment)
            fragmentControl.replaceFragment(
                all_projects(),
                R.id.flContent,
                parentFragmentManager
            )
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



        // Inflate the layout for this fragment
        return view
    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to update the screen data
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    fun UpdateUI() {

        try {

            //get the amount of projects and events
            val eventCount = GlobalClass.Events.count()
            val projectCount = GlobalClass.Projects.count()

            //evaluate whether to use singular or plural for the initiative types
            //set the default text values to plural
            var eventText = getString(R.string.eventsText)
            var projectText = getString(R.string.projectsText)

            //check whether single event text must be used
            if (eventCount == 1) {
                eventText = getString(R.string.eventsTextSingle)
            }

            //check whether single project text must be used
            if (projectCount == 1) {
                projectText = getString(R.string.projectsTextSingle)
            }


            //set the events text value
            binding.tvEvents.text = "$eventCount\n$eventText"

            //set the projects text value
            binding.tvProjects.text = "$projectCount\n$projectText"



            //define utility instances
            var databaseManager = DatabaseManager()
            val scrollViewUtils = ScrollViewTools()

            //set the component to populate with event and project custom component cards
            val activityLayout = binding.llFeed;

            //the maximum amount of recent feed items to show
            val feedSize = 10

            //get the events and projects, sorted by date and filtered by if they are occurring today or have not occurred yet
            var recentEvents = GlobalClass.Events.sortedBy { it.EventDate }.filter { it.EventDate.isAfter(LocalDate.now()) || it.EventDate.isEqual(LocalDate.now()) }
            var recentProjects = GlobalClass.Projects.sortedBy { it.ProjectDate }.filter { it.ProjectDate.isAfter(LocalDate.now()) || it.ProjectDate.isEqual(LocalDate.now()) }


            //combine the lists of projects and events
            var combinedFeed = recentEvents + recentProjects

            //new list of combined feed items sorted by the date of both events and projects
            //source - https://stackoverflow.com/a/58305030
            val sortedList = combinedFeed.sortedBy {
                when (it) {

                    //if the list item is an event, use the event date to sort
                    is EventDataClass -> it.EventDate

                    //if the list item is a project, use the project date to sort
                    is ProjectDataClass -> it.ProjectDate

                    //if the item is neither a project nor event, throw an exception
                    else -> throw IllegalArgumentException(getString(R.string.unknownFeedSortingType))
                }
            }


            //loop through the indices of the combined sorted list
            for (i in sortedList.indices) {

                //check that the current iteration count is less than the max feed size
                if (i < feedSize) {

                    //new feed item custom component
                    val newFeedItem = home_feed_card(activity)

                    //check the type of the list item
                    if (sortedList[i] is EventDataClass) {

                        //if the list item is an event

                        //set the custom component title to the title of the event
                        newFeedItem.binding.tvEntryTitle.text =
                            (sortedList[i] as EventDataClass).EventTitle

                        //set the entry type text to the event entry type text
                        newFeedItem.binding.tvEntryText.text = getString(R.string.newEventAddedText)

                        //set the entry date to the events date formatted according to the given pattern
                        newFeedItem.binding.tvEntryDate.text =
                            (sortedList[i] as EventDataClass).EventDate.format(
                                DateTimeFormatter.ofPattern("dd/MM/yy")
                            )

                        //set the feed item icon to the default event icon
                        //call method to get the default event icon
                        newFeedItem.binding.ivEntryIcon.setImageBitmap(
                            databaseManager.getEventDefaultImage(
                                requireActivity()
                            )
                        )



                        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                        //When the custom event card is clicked
                        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                        newFeedItem.setOnClickListener()
                        {
                            //open event full view

                            //new database extension utility
                            var databaseExtension = DatabaseExtensionFunctions()


                            //call method to open the event expanded view, passing the event id, state of edit mode, event table name, and current fragment
                            databaseExtension.ExpandEntryData(
                                (sortedList[i] as EventDataClass).EventID,
                                false,
                                "Events",
                                this@home
                            )
                        }
                        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

                    } else {

                        //if the feed item is a project

                        //set the custom component title to the title of the project
                        newFeedItem.binding.tvEntryTitle.text =
                            (sortedList[i] as ProjectDataClass).ProjectTitle

                        //set the entry type text to the project entry type text
                        newFeedItem.binding.tvEntryText.text =
                            getString(R.string.newProjectAddedText)

                        //set the entry date to the project date formatted according to the given pattern
                        newFeedItem.binding.tvEntryDate.text =
                            (sortedList[i] as ProjectDataClass).ProjectDate.format(
                                DateTimeFormatter.ofPattern("dd/MM/yy")
                            )

                        //set the feed item icon to the default project icon
                        //call method to get the default project icon
                        newFeedItem.binding.ivEntryIcon.setImageBitmap(
                            databaseManager.getProjectDefaultImage(
                                requireActivity()
                            )
                        )


                        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                        //When the custom event card is clicked
                        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                        newFeedItem.setOnClickListener()
                        {
                            //open project full view

                            //new database extension utility
                            var databaseExtension = DatabaseExtensionFunctions()

                            //call method to open the project expanded view, passing the project id, state of edit mode, project table name, and current fragment
                            databaseExtension.ExpandEntryData(
                                (sortedList[i] as ProjectDataClass).ProjectID,
                                false,
                                "Projects",
                                this@home
                            )
                        }
                        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

                    }

                    //add the new view
                    activityLayout.addView(newFeedItem)

                    //add space between custom cards
                    scrollViewUtils.generateSpacer(activityLayout, requireActivity(), 14)
                }
                else
                {
                    //exit the loop if the current amount of feed items is greater than the max feed items
                    break
                }

            }


            //hide the loading screen
            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                View.GONE


        } catch (e: Exception) {
            GlobalClass.InformUser(getString(R.string.errorText), "${e}", requireContext())
        }


    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}