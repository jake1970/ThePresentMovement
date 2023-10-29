package com.cjras.thepresentmovement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.children
import com.cjras.thepresentmovement.databinding.FragmentAddProjectBinding
import com.cjras.thepresentmovement.databinding.FragmentAllEventsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate


class add_project : Fragment() {

    private var _binding: FragmentAddProjectBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {


        //view binding
        _binding = FragmentAddProjectBinding.inflate(inflater, container, false)
        val view = binding.root

        var projectID = arguments?.getInt("selectedProjectID", 0)
        var editMode = arguments?.getBoolean("editMode", false)

        var currentProject = ProjectDataClass()


        //-------------
        if (projectID == 0) {
            //add code here

            //-------------
            binding.btnCreateProj.setOnClickListener() {
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
                    var nextProjectID = GlobalClass.Projects.last().ProjectID + 1
                    val tempProject = ProjectDataClass(
                        ProjectID = nextProjectID,
                        ProjectTitle = binding.etTitle.text.toString(),
                        ProjectDate = LocalDate.now(),
                        ProjectOverview = binding.etOverview.text.toString(),
                        ProjectCompanyName = binding.etCompanyName.text.toString(),
                        ProjectCompanyAbout = binding.etAboutCompany.text.toString(),
                        UserID = GlobalClass.currentUser.UserID,
                        HasImage = false
                    )
                    val dbManager = DatabaseManager()
                    dbManager.addNewProjectToFirestore(tempProject)
                }
            }
        //------------


        } else {
            for (project in GlobalClass.Projects) {
                if (project.ProjectID == projectID) {
                    binding.etTitle.setText(project.ProjectTitle)
                    binding.etAboutCompany.setText(project.ProjectCompanyAbout)
                    binding.etOverview.setText(project.ProjectOverview)
                    binding.etCompanyName.setText(project.ProjectCompanyName)

                    currentProject = project
                    break
                }
            }

            if (editMode == true)
            {
                binding.btnCreateProj.setOnClickListener() {
                    val tempProject = ProjectDataClass(
                        ProjectID = currentProject.ProjectID,
                        ProjectTitle = binding.etTitle.text.toString(),
                        ProjectDate = LocalDate.now(),
                        ProjectOverview = binding.etOverview.text.toString(),
                        ProjectCompanyName = binding.etCompanyName.text.toString(),
                        ProjectCompanyAbout = binding.etAboutCompany.text.toString(),
                        UserID = currentProject.UserID,
                        HasImage = currentProject.HasImage
                    )

                    if (!currentProject.equals(tempProject)) {
                        val currentProjectIndex = GlobalClass.Projects.indexOf(currentProject)
                        val currentProjectDocumentIndex =
                            GlobalClass.documents.allProjectIds[currentProjectIndex]

                        requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                            View.VISIBLE


                        MainScope().launch() {
                            withContext(Dispatchers.Default) {
                                var databaseManager = DatabaseManager()


                                databaseManager.updateProjectInFirestore(
                                    tempProject,
                                    currentProjectDocumentIndex
                                )

                            }


                            GlobalClass.UpdateDataBase = true
                            Toast.makeText(context, "Changes Saved", Toast.LENGTH_SHORT).show()
                            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                                View.GONE

                        }
                    }
                }
            }
            else
            {
                binding.etTitle.isEnabled = false
                binding.etAboutCompany.isEnabled = false
                binding.etOverview.isEnabled = false
                binding.etCompanyName.isEnabled = false

                binding.btnCreateProj.visibility = View.GONE
            }
        }

            //------------

            // Inflate the layout for this fragment
            return view
    }
}





    /*

    //edit events

            var eventID = arguments?.getInt("selectedEventID", 0)

            var currentEvent = EventDataClass()

            //-------------
            if(eventID == 0)
            {
                //add code here
            }
            else {
                for (event in GlobalClass.Events) {
                    if (event.EventID == eventID) {
                        binding.etLink.setText(event.EventLink)
                        binding.etTitle.setText(event.EventTitle)

                        currentEvent = event
                        break
                    }
                }
                binding.btnCreateEvent.setOnClickListener() {
                    val tempEvent = EventDataClass(
                        EventID = currentEvent.EventID,
                        EventTitle = binding.etTitle.text.toString(),
                        EventDate = LocalDate.now(),
                        EventLink = binding.etLink.text.toString(),
                        UserID  = currentEvent.UserID,
                        HasImage = currentEvent.HasImage
                    )


                    if (!currentEvent.equals(tempEvent)) {
                        val currentEventIndex = GlobalClass.Events.indexOf(currentEvent)
                        val currentEventDocumentIndex =
                            GlobalClass.documents.allEventIDs[currentEventIndex]

                        requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                            View.VISIBLE


                        MainScope().launch() {
                            withContext(Dispatchers.Default) {
                                var databaseManager = DatabaseManager()


                                databaseManager.updateEventInFirestore(
                                    tempEvent,
                                    currentEventDocumentIndex
                                )

                            }


                            GlobalClass.UpdateDataBase = true
                            Toast.makeText(context, "Changes Saved", Toast.LENGTH_SHORT).show()
                            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                                View.GONE

                        }
                    }
                }
        }
     */

    /*
        // edit announcement
        var announcementID = arguments?.getInt("selectedAnnouncementID", 0)

        var currentAnnouncement = AnnouncementDataClass()

        //-------------
        if (announcementID == 0) {
            //add code here
        } else {
            for (announcement in GlobalClass.Announcements) {
                if (announcement.AnnouncementID == announcementID) {
                    binding.etMessage.setText(announcement.AnnouncementMessage)
                    binding.etTitle.setText(announcement.AnnouncementTitle)

                    currentAnnouncement = announcement
                    break
                }
            }
            binding.btnCreateAnnouncement.setOnClickListener() {
                val tempAnnouncement = AnnouncementDataClass(
                    AnnouncementID = currentAnnouncement.AnnouncementID,
                    AnnouncementTitle = binding.etTitle.text.toString(),
                    AnnouncementMessage  = binding.etMessage.text.toString(),
                    AnnouncementDate = LocalDate.now(),
                    UserID  = currentAnnouncement.UserID
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

                    }
                }
            }
        }


    */