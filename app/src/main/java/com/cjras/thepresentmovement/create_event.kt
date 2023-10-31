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

class create_event: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateEventBinding.inflate(inflater, container, false)
        val view = binding.root

        eventID = arguments?.getInt("selectedEventID", 0)
        editMode = arguments?.getBoolean("editMode", false)

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



        binding.tvStartDate.setOnClickListener(){
           val scrollViewTools = ScrollViewTools()
            scrollViewTools.datePicker(this, true, binding.tvStartDate)
        }

        binding.ivRefresh.setOnClickListener()
        {
            GlobalClass.RefreshFragment(this)
        }


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
            binding.btnCreateEvent.setOnClickListener() {
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
                                nextEventID = GlobalClass.Events.last().EventID + 1
                            }
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
                            dbManager.addNewEventToFirestore(tempEvent)

                            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                                View.GONE

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

            if (editMode == true)
            {
                binding.btnCreateEvent.setOnClickListener() {

                    if (binding.tvStartDate.text.toString() == getString(R.string.blankDate))
                    {
                        Toast.makeText(requireActivity(), "Please enter a date", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
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
            }
            else
            {
                binding.etEventTitle.isEnabled = false
                binding.etEventLink.isEnabled = false
                binding.tvStartDate.isEnabled = false
                binding.tvStartDate.isEnabled = false

                binding.btnCreateEvent.visibility = View.GONE
            }
        }

        requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
            View.GONE

        //------------
    }
}