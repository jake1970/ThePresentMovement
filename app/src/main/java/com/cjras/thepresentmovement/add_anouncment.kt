package com.cjras.thepresentmovement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.children
import com.cjras.thepresentmovement.databinding.FragmentAddAnnouncmentBinding
import java.time.LocalDate


class add_anouncment : Fragment() {

    private var _binding: FragmentAddAnnouncmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {


        //view binding
        _binding = FragmentAddAnnouncmentBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnCreateAnno.setOnClickListener() {
            //boolean to determine if all fields are filled in
            var allFilled = true

            //the container where the input fields are
            val container = binding.rlContent


            //loop through the inputs
            for (component in container.children) {
                //check that the current component is a text edit and that it doesn't contain a value
                if (component is EditText && component.text.isNullOrEmpty()) {
                    //set the components error text
                    component.error = "missing"

                    //set the filled status to false
                    allFilled = false
                }
            }


            //if all components are filled in
            if (allFilled == true) {

                var nextAnnouncementID = GlobalClass.Announcements.last().AnnouncementID+1
                //register the user with the given inputs
                val tempAnnoucement = AnnouncementDataClass(
                    AnnouncementID = nextAnnouncementID,
                    AnnouncementTitle = binding.edAnnouncementTitle.text.toString(),
                    AnnouncementMessage = binding.edAnnouncmentMessage.text.toString(),
                    AnnouncementDate = LocalDate.now(),
                    UserID = GlobalClass.currentUser.UserID
                )
                val dbManager = DatabaseManager()
                dbManager.addNewAccouncementToFirestore(tempAnnoucement)


            }


        }
        // Inflate the layout for this fragment
        return view
    }
}