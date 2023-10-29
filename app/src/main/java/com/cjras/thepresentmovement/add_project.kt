package com.cjras.thepresentmovement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
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

        var currentProject = ProjectDataClass()

        //-------------
        if(projectID == 0)
        {
            //add code here
        }
        else {
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

        //------------

        // Inflate the layout for this fragment
        return view
    }


}