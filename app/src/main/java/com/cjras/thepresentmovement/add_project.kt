package com.cjras.thepresentmovement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.children
import com.cjras.thepresentmovement.databinding.FragmentAddProjectBinding
import com.cjras.thepresentmovement.databinding.FragmentAllEventsBinding
import java.time.LocalDate
import com.google.firebase.auth.FirebaseAuth



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


        binding.btnCreateProj.setOnClickListener() {
            //-------------
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
            if (allFilled == true)
            {
                var nextProjectID = GlobalClass.Projects.last().ProjectID+1
                val tempProject = ProjectDataClass(
                    ProjectID = nextProjectID,
                    ProjectTitle = binding.etTitle.text.toString(),
                    //ProjectDate = LocalDate.now(),
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

        // Inflate the layout for this fragment
        return view
    }

}
