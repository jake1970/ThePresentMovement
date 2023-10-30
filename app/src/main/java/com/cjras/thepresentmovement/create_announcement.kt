package com.cjras.thepresentmovement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.cjras.thepresentmovement.databinding.FragmentAdminBinding
import com.cjras.thepresentmovement.databinding.FragmentCreateAnnouncementBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class create_announcement : Fragment() {

    private var _binding: FragmentCreateAnnouncementBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentCreateAnnouncementBinding.inflate(inflater, container, false)
        val view = binding.root


        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //Data population
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        try {

            //Read Data
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
        } catch (e: Exception) {
            GlobalClass.InformUser(
                getString(R.string.errorText),
                "${e.toString()}",
                requireContext()
            )
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


        binding.llHeader.setOnClickListener()
        {
            fragmentManager?.popBackStackImmediate()
        }

        binding.ivRefresh.setOnClickListener()
        {
            GlobalClass.RefreshFragment(this)
        }

        return view;
    }



    private fun UpdateUI()
    {

        // edit announcement
        var announcementID = arguments?.getInt("selectedAnnouncementID", 0)


        var editMode = arguments?.getBoolean("editMode", false)

        var currentAnnouncement = AnnouncementDataClass()


        //-------------
        if (announcementID == 0) {
            //add code here

            //-------------
            binding.btnCreateAnnounce.setOnClickListener() {
                //boolean to determine if all fields are filled in
                var allFilled = true

                //the container where the input fields are
                val container = binding.rlContent


                //loop through the inputs
                for (component in container.children) {
                    //check that the current component is a text edit and that it doesn't contain a value
                    if (component is EditText && component.text.isNullOrEmpty()) {
                        //set the components error text
                        component.error = getString(R.string.missingText)

                        //set the filled status to false
                        allFilled = false
                    }
                }


                //if all components are filled in
                if (allFilled == true) {

                    MainScope().launch() {

                        requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE

                        withContext(Dispatchers.Default) {
                            var databaseManager = DatabaseManager()

                            GlobalClass.Announcements = databaseManager.getAllAnnouncementsFromFirestore()
                        }


                        var nextAnnouncementID = 1
                        if (GlobalClass.Announcements.count() > 0) {
                            nextAnnouncementID = GlobalClass.Announcements.last().AnnouncementID + 1
                        }

                        val tempAnnouncement = AnnouncementDataClass(
                            AnnouncementID = nextAnnouncementID,
                            AnnouncementTitle = binding.etAnnounceTitle.text.toString(),
                            AnnouncementDate = LocalDate.now(),
                            AnnouncementMessage = binding.etAnnounceBody.text.toString(),
                            UserID = GlobalClass.currentUser.UserID

                        )
                        val dbManager = DatabaseManager()
                        dbManager.addNewAnnouncementToFirestore(tempAnnouncement)

                        Toast.makeText(requireActivity(), "Announcement Added", Toast.LENGTH_SHORT).show()
                        GlobalClass.UpdateDataBase = true
                        requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE
                        binding.llHeader.callOnClick()

                    }
                }
            }
            //------------


        } else {
            for (announcement in GlobalClass.Announcements) {
                if (announcement.AnnouncementID == announcementID) {
                    binding.etAnnounceTitle.setText(announcement.AnnouncementTitle)
                    binding.etAnnounceBody.setText(announcement.AnnouncementMessage)
                    currentAnnouncement = announcement
                    break
                }
            }

            if (editMode == true)
            {
                binding.btnCreateAnnounce.setOnClickListener() {
                    val tempAnnouncement = AnnouncementDataClass(
                        AnnouncementID = currentAnnouncement.AnnouncementID,
                        AnnouncementTitle = binding.etAnnounceTitle.text.toString(),
                        AnnouncementDate = LocalDate.now(),
                        AnnouncementMessage = binding.etAnnounceBody.text.toString(),
                        UserID = GlobalClass.currentUser.UserID
                    )

                    if (!currentAnnouncement.equals(tempAnnouncement)) {
                        val currentAnnouncementIndex = GlobalClass.Announcements.indexOf(currentAnnouncement)
                        val currentAnnouncementDocumentIndex =
                            GlobalClass.documents.allAnnouncmentIds[currentAnnouncementIndex]

                        requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                            View.VISIBLE


                        MainScope().launch() {
                            withContext(Dispatchers.Default) {
                                var databaseManager = DatabaseManager()


                                databaseManager.updateAnnouncementInFirestore(
                                    tempAnnouncement,
                                    currentAnnouncementDocumentIndex
                                )

                            }


                            GlobalClass.UpdateDataBase = true
                            Toast.makeText(context, "Changes Saved", Toast.LENGTH_SHORT).show()
                            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                                View.GONE
                            binding.llHeader.callOnClick()

                        }
                    }
                }
            }
            else
            {
                binding.etAnnounceTitle.isEnabled = false
                binding.etAnnounceBody.isEnabled = false

                binding.btnCreateAnnounce.visibility = View.GONE
            }
        }

        requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE
    }
}