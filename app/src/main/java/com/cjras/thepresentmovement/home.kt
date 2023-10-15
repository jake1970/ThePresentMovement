package com.cjras.thepresentmovement

import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.cjras.thepresentmovement.databinding.FragmentHomeBinding
import com.cjras.thepresentmovement.databinding.FragmentNoticesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass.
 * Use the [home.newInstance] factory method to
 * create an instance of this fragment.
 */
class home : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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
            GlobalScope.launch{
                if (GlobalClass.UpdateDataBase == true)
                {
                    var DBManger = DatabaseManager()

                    GlobalClass.Users = DBManger.getAllUsersFromFirestore()
                    GlobalClass.UpdateDataBase = false

                    //********************************************************
                    //temp code to set current user
                    //GlobalClass.currentUser = GlobalClass.Users[0]
                    //********************************************************

                    //get the users image
                    GlobalClass.currentUserImage = DBManger.getUserImage(requireContext(), GlobalClass.currentUser.UserID, GlobalClass.currentUser.HasImage)
                }
                withContext(Dispatchers.Main) {


                    UpdateUI()
                }
            }
        }
        catch (e: Error)
        {
            GlobalClass.InformUser(getString(R.string.errorText), "${e.toString()}", requireContext())
        }
        //---------------------------------------------------------------------------------------------------------------------------------------------------------

        // Inflate the layout for this fragment
        return view
    }

    fun UpdateUI ()
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
            binding.ivLoadingLogo.visibility = View.GONE
            binding.pbLoadingBar.visibility = View.GONE


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
        catch (e: Error)
        {
            GlobalClass.InformUser(getString(R.string.errorText), "${e.toString()}", requireContext())
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}