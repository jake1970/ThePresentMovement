package com.cjras.thepresentmovement

import android.graphics.Bitmap
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.cjras.thepresentmovement.databinding.FragmentCreateAccountBinding
import com.cjras.thepresentmovement.databinding.FragmentCreateAnnouncementBinding
import com.cjras.thepresentmovement.databinding.FragmentCreateEventBinding
import com.cjras.thepresentmovement.databinding.FragmentSettingsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private var _binding: FragmentCreateEventBinding? = null
private val binding get() = _binding!!

private var eventID :Int? = 0
private var editMode :Boolean? = false

private var modifiedPicture = false
private lateinit var cameraManager: CameraHandler

class create_event: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateEventBinding.inflate(inflater, container, false)
        val view = binding.root

        cameraManager = CameraHandler(this, binding.ivMyProfileImage, modifiedPicture)

        eventID = arguments?.getInt("selectedEventID", 0)
        editMode = arguments?.getBoolean("editMode", false)



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //Data population
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        try {

            GlobalClass.checkUser(this)

            //Read Data
            MainScope().launch {

                if (GlobalClass.UpdateDataBase == true) {
                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                        View.VISIBLE
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
                if (GlobalClass.currentUser.MemberTypeID != 2 && GlobalClass.currentUser.MemberTypeID != 3) {
                    requireActivity().findViewById<BottomNavigationView>(R.id.bnvHomeNavigation).menu.removeItem(
                        R.id.iAdmin
                    )
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


        //If the startDate is clicked, brings up date picker
        binding.tvStartDate.setOnClickListener(){
           val scrollViewTools = ScrollViewTools()
            scrollViewTools.datePicker(this, true, binding.tvStartDate)
        }

        //If the refresh button is clicked, to ensure the most up to date data
        binding.ivRefresh.setOnClickListener()
        {
            GlobalClass.RefreshFragment(this)
        }

        //If the header is clicked
        binding.llHeader.setOnClickListener()
        {
            fragmentManager?.popBackStackImmediate()
        }

        // Inflate the layout for this fragment
        return view

    }

    private fun UpdateUI()
    {
        //-------------
        var currentEvent = EventDataClass()
        if (eventID == 0) {
            //add code here

            //-------------


            binding.ivMyProfileImageTint.visibility = View.VISIBLE
            binding.tvMyProfileImageEditText.visibility = View.VISIBLE

            //Get Image
            binding.rlImageContainer.setOnClickListener()
            {
                cameraManager.handlePhoto()
            }

            binding.btnCreateEvent.visibility = View.VISIBLE
            binding.btnCreateEvent.setOnClickListener() {
                //boolean to determine if all fields are filled in
                var allFilled = true

                //the container where the input fields are
                val container = binding.rlContent


                //loop through the inputs
                for (component in container.children) {
                    //check that the current component is a text edit and that it doesn't contain a value
                    if (component is EditText && component.text.isNullOrEmpty() && component != binding.etEventLink) {
                        //set the components error text
                        component.error = getString(R.string.missingText)

                        //set the filled status to false
                        allFilled = false
                    }
                }

                //check if URL provided is a valid URL
                if (GlobalClass.isValidUrl(binding.etEventLink.text.toString()) == false && binding.etEventLink.text.toString() != "")
                {
                    binding.etEventLink.error = getString(R.string.invalidUrlText)
                    allFilled = false
                }


                //if all components are filled in
                if (allFilled == true) {

                    if (binding.tvStartDate.text.toString() == getString(R.string.blankDate)) {
                        Toast.makeText(requireActivity(), "Please enter a date", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        MainScope().launch() {

                            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                                View.VISIBLE

                            withContext(Dispatchers.Default) {
                                var databaseManager = DatabaseManager()

                                GlobalClass.Events = databaseManager.getAllEventsFromFirestore()
                            }



                            var nextEventID = 1
                            if (GlobalClass.Events.count() > 0) {
                                //nextEventID = GlobalClass.Events.last().EventID //+ 1

                                var existingID = true

                                while (existingID == true)
                                {
                                    nextEventID = GlobalClass.Events.sortedBy { it.EventID }.last().EventID + 1

                                    var selectedEventIndex = GlobalClass.Events.indexOfLast { it.EventID == nextEventID }

                                    if (selectedEventIndex != -1) {
                                        //if the event id is in use
                                        nextEventID = GlobalClass.Events.sortedBy { it.EventID }.last().EventID + 1
                                    }
                                    else
                                    {
                                        //if the event id is not in use yet
                                        existingID = false
                                    }
                                }


                            }

                            //Formats date, parses formatted date into Event Data Class called tempEvent
                            val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
                            var formattedDate =
                                LocalDate.parse(binding.tvStartDate.text.toString(), formatter)
                            val tempEvent = EventDataClass(
                                EventID = nextEventID,
                                EventTitle = binding.etEventTitle.text.toString(),
                                EventDate = formattedDate,
                                EventLink = binding.etEventLink.text.toString(),
                                UserID = GlobalClass.currentUser.UserID,
                                HasImage = false
                            )
                            val dbManager = DatabaseManager()

                            if (cameraManager.getModifiedImageStatus() == true)
                            {
                                tempEvent.HasImage = true
                                dbManager.setProjectImage(requireActivity(), nextEventID, cameraManager.getSelectedUri())
                            }

                            //Adds tempEvent to Firebase
                            dbManager.addNewEventToFirestore(tempEvent)


                            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE
                            Toast.makeText(context, "Added Event", Toast.LENGTH_SHORT)
                                .show()
                            GlobalClass.UpdateDataBase = true
                            binding.llHeader.callOnClick()

                        }
                    }
                }
            }
            //------------


        } else {
            for (event in GlobalClass.Events) {
                if (event.EventID == eventID) {
                    binding.etEventTitle.setText(event.EventTitle)


                    binding.tvStartDate.text = event.EventDate.format(DateTimeFormatter.ofPattern("dd/MM/yy"))
                    binding.etEventLink.setText(event.EventLink)

                    currentEvent = event
                    break
                }
            }



            try {
                //Read Data
                MainScope().launch {

                    var bitmap: Bitmap? = null
                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE
                    withContext(Dispatchers.Default) {

                        var databaseManager = DatabaseManager()
                        bitmap = databaseManager.getEventImage(
                            requireContext(),
                            currentEvent.EventID,
                            currentEvent.HasImage
                        )


                    }

                    binding.ivMyProfileImage.setImageBitmap(bitmap)
                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE
                }



            }
            catch (e: Exception) {
                GlobalClass.InformUser(
                    getString(R.string.errorText),
                    "${e.toString()}",
                    requireContext()
                )
            }


            //check if the page is in edit mode or create mode
            if (editMode == true)
            {

                binding.ivMyProfileImageTint.visibility = View.VISIBLE
                binding.tvMyProfileImageEditText.visibility = View.VISIBLE

                //handle image if clicked
                binding.rlImageContainer.setOnClickListener()
                {
                    cameraManager.handlePhoto()
                }

                binding.btnCreateEvent.visibility = View.VISIBLE
                binding.btnCreateEvent.text = "Save"

                //if create event button is clicked
                binding.btnCreateEvent.setOnClickListener() {

                    if (binding.tvStartDate.text.toString() == getString(R.string.blankDate))
                    {
                        Toast.makeText(requireActivity(), "Please enter a date", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        //formats date and parses to User Data Class called tempEvent
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
                        var formattedDate = LocalDate.parse(binding.tvStartDate.text.toString(), formatter)

                        val tempEvent = EventDataClass(
                            EventID = currentEvent.EventID,
                            EventTitle = binding.etEventTitle.text.toString(),
                            EventDate = formattedDate,
                            EventLink = binding.etEventLink.text.toString(),
                            UserID  = GlobalClass.currentUser.UserID,
                            HasImage = currentEvent.HasImage
                        )

                        //check if event has a picture
                        if (currentEvent.HasImage == false && cameraManager.getModifiedImageStatus() == true) {
                            tempEvent.HasImage = true
                        }

                        if (!currentEvent.equals(tempEvent) || cameraManager.getModifiedImageStatus() == true) {
                            val currentEventIndex = GlobalClass.Events.indexOf(currentEvent)
                            val currentEventDocumentIndex =
                                GlobalClass.documents.allEventIDs[currentEventIndex]

                            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                                View.VISIBLE


                            MainScope().launch() {
                                withContext(Dispatchers.Default) {
                                    var databaseManager = DatabaseManager()

                                    //call updateEventInFirestore function, pass it the tempEvent to update in the Firestore
                                    databaseManager.updateEventInFirestore(
                                        tempEvent,
                                        currentEventDocumentIndex
                                    )


                                    if (cameraManager.getModifiedImageStatus() == true) {
                                        databaseManager.setEventImage(
                                            requireContext(),
                                            currentEvent.EventID,
                                            cameraManager.getSelectedUri()
                                        )

                                    }

                                }


                                GlobalClass.UpdateDataBase = true
                                requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                                    View.GONE
                                Toast.makeText(context, "Changes Saved", Toast.LENGTH_SHORT)
                                    .show()
                                binding.llHeader.callOnClick()

                            }
                        }
                        else
                        {
                            Toast.makeText(context, "No changes were made", Toast.LENGTH_SHORT).show()
                            binding.llHeader.callOnClick()
                        }
                    }
                }
            }
            else
            {
                binding.etEventTitle.isEnabled = false

                binding.etEventLink.isFocusable = false


                binding.etEventLink.setOnClickListener()
                {
                    if (GlobalClass.isValidUrl(binding.etEventLink.text.toString()) && binding.etEventLink.isFocusable == false)
                    {
                        GlobalClass.openBrowser(binding.etEventLink.text.toString(), requireActivity())
                    }
                }


                binding.tvStartDate.isEnabled = false
                binding.tvStartDate.isEnabled = false


            }
        }

        requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
            View.GONE

        //------------
    }
}