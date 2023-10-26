package com.cjras.thepresentmovement

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
import com.cjras.thepresentmovement.databinding.FragmentHomeBinding
import com.cjras.thepresentmovement.databinding.FragmentNoticesBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*

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

        /*
        val loadingProgressBar = layoutInflater.inflate(R.layout.loading_cover, null) as ViewGroup
        view.addView(loadingProgressBar)
         */

        //loadingCover = GlobalClass.addLoadingCover(layoutInflater, view)
        //loadingCover = GlobalClass.addLoadingCover(layoutInflater, view)


        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        //initial data population
        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        //var loadingCover = GlobalClass.addLoadingCover(layoutInflater, view)
       // requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE

        try {

           // var loadingCover = View(activity) as ViewGroup
            //loadingCover.visibility = View.GONE
            //requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE

            //Read Data
            MainScope().launch{

                if (GlobalClass.UpdateDataBase == true) {
                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE
                withContext(Dispatchers.Default) {

                    //var loadingCover = GlobalClass.addLoadingCover(layoutInflater, view)
                    //loadingCover.visibility = View.VISIBLE



                        var databaseManager = DatabaseManager()

                        databaseManager.updateFromDatabase()
                        //GlobalClass.MemberTypes = databaseManager.getAllMemberTypesFromFirestore()
                        //GlobalClass.Users = databaseManager.getAllUsersFromFirestore()
                        //GlobalClass.UpdateDataBase = false

                        //********************************************************
                        //temp code to set current user
                        //GlobalClass.currentUser = GlobalClass.Users[0]
                        //********************************************************

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

    fun UpdateUI (/*loadingCover : ViewGroup*/)
    {

        //*****************************************************************************************
        //temp code to set current user
       // GlobalClass.setDefaultBitmap(requireContext())
        //GlobalClass.currentUser = GlobalClass.Users[1]


        //if (GlobalClass.currentUser.HasImage == true)
        //{
           // var DBManger = DatabaseManager()

            //var bm = ContextCompat.getDrawable(requireContext(), R.drawable.person_icon)?.toBitmap()
            //GlobalClass.currentUserImage = DBManger.getUserImage(requireContext(), GlobalClass.currentUser.UserID)
        //}



        //GlobalClass.Users.remove(GlobalClass.currentUser)
        //*****************************************************************************************

        try {
            //remove loading screen
            /*
            binding.ivLoadingLogo.visibility = View.GONE
            binding.pbLoadingBar.visibility = View.GONE

             */

            //loadingCover.visibility = View.GONE
            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE

            // binding.tvEvents.text = Html.fromHtml("<html><body><font size=5 color=red>Hello </font> World </body><html>"))
            binding.tvEvents.text = Html.fromHtml("5" + "<small>" + "<small>" + "<small>" + " " + getString(R.string.upcomingText) + "</small>" + "</small>" + "</small>" + "<br />" + getString(R.string.eventsText))
            binding.tvProjects.text = Html.fromHtml("5" + "<small>" + "<small>" + "<small>" + " " + getString(R.string.upcomingText) + "</small>" + "</small>" + "</small>" + "<br />" + getString(R.string.projectsText))
            //binding.tvEvents.textSize = 26F
            //binding.tvProjects.textSize = 26F

            //---------------------------------------------------------------------------------------------
            //test code for custom components
            //---------------------------------------------------------------------------------------------
            for (i in 1..8) {

                val activityLayout = binding.llFeed;
                var newFeedItem = home_feed_card(activity)

                newFeedItem.binding.tvEntryTitle.text = "Think For Good"
                newFeedItem.binding.tvEntryText.text = "New Event Added"
                newFeedItem.binding.tvEntryDate.text = "12 Oct 2023"

                //add the new view
                activityLayout.addView(newFeedItem)

                val scale = requireActivity().resources.displayMetrics.density
                val pixels = (14 * scale + 0.5f)

                val spacer = Space(activity)
                spacer.minimumHeight = pixels.toInt()
                activityLayout.addView(spacer)

            }
            //---------------------------------------------------------------------------------------------

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