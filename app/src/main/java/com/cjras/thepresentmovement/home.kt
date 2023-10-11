package com.cjras.thepresentmovement

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import com.cjras.thepresentmovement.databinding.FragmentHomeBinding
import com.cjras.thepresentmovement.databinding.FragmentNoticesBinding

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

        // Inflate the layout for this fragment
        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}