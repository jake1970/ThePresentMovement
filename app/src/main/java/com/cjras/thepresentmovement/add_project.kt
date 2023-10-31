package com.cjras.thepresentmovement

import android.graphics.Bitmap
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class add_project : Fragment() {

    private var _binding: FragmentAddProjectBinding? = null
    private val binding get() = _binding!!

    private var projectID :Int? = 0
    private var editMode :Boolean? = false

    private var modifiedPicture = false
    private lateinit var cameraManager: CameraHandler


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {


        //view binding
        _binding = FragmentAddProjectBinding.inflate(inflater, container, false)
        val view = binding.root

        cameraManager = CameraHandler(this, binding.ivMyProfileImage, modifiedPicture)

        projectID = arguments?.getInt("selectedProjectID", 0)
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
        var currentProject = ProjectDataClass()

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")



        if (projectID == 0) {


            //-------------
            binding.btnCreateAccount.setOnClickListener() {
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

                    if (binding.tvStartDate.text.toString() == getString(R.string.blankDate))
                    {
                        Toast.makeText(requireActivity(), "Please enter a date", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        MainScope().launch() {

                            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE

                            withContext(Dispatchers.Default) {
                                var databaseManager = DatabaseManager()

                                GlobalClass.Projects = databaseManager.getAllProjectsFromFirestore()
                            }

                            var nextProjectID =  1
                            if (GlobalClass.Projects.count() > 0)
                            {
                                nextProjectID = GlobalClass.Projects.last().ProjectID + 1
                            }


                            var formattedDate = LocalDate.parse(binding.tvStartDate.text.toString(), formatter)

                            val tempProject = ProjectDataClass(
                                ProjectID = nextProjectID,
                                ProjectTitle = binding.etTitle.text.toString(),
                                ProjectDate = formattedDate, //change to the date picker selection
                                ProjectOverview = binding.etOverview.text.toString(),
                                ProjectCompanyName = binding.etCompanyName.text.toString(),
                                ProjectCompanyAbout = binding.etAboutnCompany.text.toString(),
                                UserID = GlobalClass.currentUser.UserID,
                                HasImage = false
                            )
                            val dbManager = DatabaseManager()
                            dbManager.addNewProjectToFirestore(tempProject)

                            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE
                            Toast.makeText(context, "Added Project", Toast.LENGTH_SHORT)
                                .show()
                            binding.llHeader.callOnClick()
                        }


                    }
                }
            }
            //------------


        } else {
            for (project in GlobalClass.Projects) {
                if (project.ProjectID == projectID) {
                    binding.etTitle.setText(project.ProjectTitle)
                    binding.etAboutnCompany.setText(project.ProjectCompanyAbout)
                    binding.etOverview.setText(project.ProjectOverview)
                    binding.etCompanyName.setText(project.ProjectCompanyName)



                    binding.tvStartDate.text = project.ProjectDate.format(DateTimeFormatter.ofPattern("dd/MM/yy"))

                    currentProject = project
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
                             bitmap = databaseManager.getProjectImage(
                                requireContext(),
                                currentProject.ProjectID,
                                currentProject.HasImage
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



            if (editMode == true) {


                binding.ivMyProfileImageTint.visibility = View.VISIBLE
                binding.tvMyProfileImageEditText.visibility = View.VISIBLE

                binding.rlImageContainer.setOnClickListener()
                {
                    cameraManager.handlePhoto()
                }

                binding.btnCreateAccount.setOnClickListener() {

                    if (binding.tvStartDate.text.toString() == getString(R.string.blankDate)) {
                        Toast.makeText(requireActivity(), "Please enter a date", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        var formattedDate =
                            LocalDate.parse(binding.tvStartDate.text.toString(), formatter)

                        val tempProject = ProjectDataClass(
                            ProjectID = currentProject.ProjectID,
                            ProjectTitle = binding.etTitle.text.toString(),
                            ProjectDate = formattedDate,
                            ProjectOverview = binding.etOverview.text.toString(),
                            ProjectCompanyName = binding.etCompanyName.text.toString(),
                            ProjectCompanyAbout = binding.etAboutnCompany.text.toString(),
                            UserID = currentProject.UserID,
                            HasImage = currentProject.HasImage
                        )

                        if (currentProject.HasImage == false && cameraManager.getModifiedImageStatus() == true) {
                            tempProject.HasImage = true
                        }


                        if (!currentProject.equals(tempProject) || cameraManager.getModifiedImageStatus() == true) {
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

                                    if (cameraManager.getModifiedImageStatus() == true) {
                                        databaseManager.setProjectImage(
                                            requireContext(),
                                            currentProject.ProjectID,
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
                    }
                }
            }
            else
            {
                binding.etTitle.isEnabled = false
                binding.etAboutnCompany.isEnabled = false
                binding.etOverview.isEnabled = false
                binding.etCompanyName.isEnabled = false
                binding.tvStartDate.isEnabled = false

                binding.btnCreateAccount.visibility = View.GONE
            }
        }

        requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
            View.GONE
        //------------
    }

}