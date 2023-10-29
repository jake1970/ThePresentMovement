package com.cjras.thepresentmovement

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.cjras.thepresentmovement.databinding.FragmentExpandedContactBinding
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*



class expanded_contact : Fragment() {

    private var _binding: FragmentExpandedContactBinding? = null
    private val binding get() = _binding!!

    private var currentUserID = ""

    private var currentEditMode = false

    private var modifiedPicture = false

    private var selectedUserID: String? = ""

    private lateinit var cameraManager: CameraHandler



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentExpandedContactBinding.inflate(inflater, container, false)
        val view = binding.root

        cameraManager = CameraHandler(this, binding.ivMyProfileImage, modifiedPicture)

        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        //initial data population
        //---------------------------------------------------------------------------------------------------------------------------------------------------------
       // var loadingCover = GlobalClass.addLoadingCover(layoutInflater, view)
        requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE

        try {

            selectedUserID = arguments?.getString("selectedUserID")

            if (selectedUserID == GlobalClass.currentUser.UserID) {

                requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE
            }


            //Read Data
            MainScope().launch {
                if (GlobalClass.UpdateDataBase == true) {
                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE
                withContext(Dispatchers.Default) {





                    var databaseManager = DatabaseManager()

                    databaseManager.updateFromDatabase()


                }
                }



                    UpdateUI()

            }
        } catch (e: Error) {
            GlobalClass.InformUser(getString(R.string.errorText), "$e", requireContext())
        }
        //---------------------------------------------------------------------------------------------------------------------------------------------------------



        binding.ivModifyContact.setOnClickListener()
        {


            currentEditMode = !currentEditMode

            setEditMode(currentEditMode)

            if (currentEditMode == false) {

                var givenUserData = UserDataClass(
                    UserID = GlobalClass.currentUser.UserID,
                    FirstName = GlobalClass.currentUser.FirstName,
                    LastName = GlobalClass.currentUser.LastName,
                    EmailAddress = binding.tfEmailAddress.text.toString(),
                    MemberTypeID = GlobalClass.currentUser.MemberTypeID,
                    Quote = binding.tfQuote.text.toString(),
                    ContactNumber = binding.tfContactNumber.text.toString(),
                    CompanyName = binding.tfCompanyName.text.toString(),
                    LinkedIn = binding.tfLinkedIn.text.toString(),
                    Website = binding.tfWebsite.text.toString(),
                    HasImage = GlobalClass.currentUser.HasImage
                )

                if (GlobalClass.currentUser.HasImage == false && cameraManager.getModifiedImageStatus() == true)
                {
                    givenUserData.HasImage = true
                }

                if (!givenUserData.equals(GlobalClass.currentUser) || cameraManager.getModifiedImageStatus() == true) {

                    val currentUserIndex = GlobalClass.Users.indexOf(GlobalClass.currentUser)
                    val currentUserDocumentIndex = GlobalClass.documents.allUserIDs[currentUserIndex]

                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE



                    MainScope().launch() {
                        withContext(Dispatchers.Default) {
                        var databaseManager = DatabaseManager()


                        databaseManager.updateUserInFirestore(
                            givenUserData,
                            currentUserDocumentIndex
                        )

                        if (cameraManager.getModifiedImageStatus() == true) {
                            databaseManager.setUserImage(
                                requireContext(),
                                currentUserID,
                                cameraManager.getSelectedUri()
                            )

                            GlobalClass.currentUserImage = databaseManager.getUserImage(
                                requireContext(),
                                GlobalClass.currentUser.UserID,
                                GlobalClass.currentUser.HasImage
                            )
                        }
                    }

                            GlobalClass.UpdateDataBase = true
                            Toast.makeText(context, "Changes Saved", Toast.LENGTH_SHORT).show()
                            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE

                    }
                }

            }
        }


        //----------------------------------------------------------------------------------------------------

        binding.ivRefresh.setOnClickListener()
        {
            GlobalClass.RefreshFragment(this)
        }

        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        //Select an image
        //---------------------------------------------------------------------------------------------------------------------------------------------------------


        binding.ivMyProfileImageTint.setOnClickListener()
        {
            //method to add an image
            cameraManager.handlePhoto()
        }


        binding.tvMyProfileImageEditText.setOnClickListener()
        {
            //method to add an image
            cameraManager.handlePhoto()
        }

        //----------------------------------------------------------------------------------------------------


        binding.llHeader.setOnClickListener()
        {
            fragmentManager?.popBackStackImmediate()
            /*
            //create local fragment controller
            val fragmentControl = FragmentManager()

            //go back the the general contacts page
            fragmentControl.replaceFragment(contacts(), R.id.flContent, parentFragmentManager)
             */

        }


        // Inflate the layout for this fragment
        return view
    }

    suspend fun UpdateUI() {

        try {



            if (!selectedUserID.isNullOrEmpty()) {


                for (user in GlobalClass.Users) {
                    if (user.UserID == selectedUserID) {
                        currentUserID = user.UserID
                        binding.tvContactName.text = "${user.FirstName} ${user.LastName}"

                        binding.tvCardHeader.text = user.FirstName + getString(R.string.cardOwnerHeading)

                        binding.tfQuote.setText(user.Quote)
                        binding.tfContactNumber.setText(user.ContactNumber)
                        binding.tfEmailAddress.setText(user.EmailAddress)
                        binding.tfCompanyName.setText(user.CompanyName)
                        binding.tfLinkedIn.setText(user.LinkedIn)
                        binding.tfWebsite.setText(user.Website)

                        if (selectedUserID == GlobalClass.currentUser.UserID) {
                            binding.ivMyProfileImage.setImageBitmap(GlobalClass.currentUserImage)
                            setEditMode(false)
                            binding.tvRole.text = GlobalClass.currentUserMemberType
                        } else {
                            setGeneralView()

                            var databaseManager = DatabaseManager()
                            binding.tvRole.text = MemberTypeDataClass().getSingleMemberType(user.MemberTypeID)

                           // loadingCover.visibility = View.VISIBLE
                            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE


                            var bitmap = databaseManager.getUserImage(
                                requireContext(),
                                user.UserID,
                                user.HasImage
                            )


                            binding.ivMyProfileImage.setImageBitmap(bitmap)

                        }

                        //exit loop once user is found
                        break

                    }
                }

            }


            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE

        } catch (e: Exception) {
            GlobalClass.InformUser(getString(R.string.errorText), "$e", requireContext())
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setGeneralView() {
        binding.ivModifyContact.isVisible = false

        binding.ivMyProfileImageTint.visibility = View.GONE
        binding.tvMyProfileImageEditText.visibility = View.GONE

        binding.tfQuote.isEnabled = false
        binding.tfContactNumber.isEnabled = false
        binding.tfEmailAddress.isEnabled = false
        binding.tfCompanyName.isEnabled = false
        binding.tfLinkedIn.isEnabled = false
        binding.tfWebsite.isEnabled = false
    }

    private fun setEditMode(currentlyEditing: Boolean) {


        if (currentlyEditing == true) {

            binding.ivModifyContact.setImageDrawable(activity?.getDrawable(R.drawable.tick_icon))

            binding.ivMyProfileImageTint.visibility = View.VISIBLE
            binding.tvMyProfileImageEditText.visibility = View.VISIBLE

            binding.tfQuote.isEnabled = true
            binding.tfContactNumber.isEnabled = true
            binding.tfEmailAddress.isEnabled = true
            binding.tfCompanyName.isEnabled = true
            binding.tfLinkedIn.isEnabled = true
            binding.tfWebsite.isEnabled = true

        } else {

            binding.ivModifyContact.setImageDrawable(activity?.getDrawable(R.drawable.edit_icon))

            binding.ivMyProfileImageTint.visibility = View.GONE
            binding.tvMyProfileImageEditText.visibility = View.GONE

            binding.tfQuote.isEnabled = false
            binding.tfContactNumber.isEnabled = false
            binding.tfEmailAddress.isEnabled = false
            binding.tfCompanyName.isEnabled = false
            binding.tfLinkedIn.isEnabled = false
            binding.tfWebsite.isEnabled = false

        }

    }


}