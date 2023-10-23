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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

/*
    //----------------------------------------------------------------------------------------------------
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val CAMERA_REQUEST_CODE = 200
        private const val PICK_FROM_GALLERY = 1
    }
    //----------------------------------------------------------------------------------------------------
 */

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
                //loadingCover.visibility = View.GONE
                requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE
            }


            //Read Data
            GlobalScope.launch {
                if (GlobalClass.UpdateDataBase == true) {

                    //loadingCover.visibility = View.VISIBLE
                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE

                    var databaseManager = DatabaseManager()

                    GlobalClass.MemberTypes = databaseManager.getAllMemberTypesFromFirestore()
                    GlobalClass.Users = databaseManager.getAllUsersFromFirestore()

                    GlobalClass.UpdateDataBase = false

                }
                withContext(Dispatchers.Main) {


                    UpdateUI()
                }
            }
        } catch (e: Error) {
            GlobalClass.InformUser(getString(R.string.errorText), "$e", requireContext())
        }
        //---------------------------------------------------------------------------------------------------------------------------------------------------------


        //----------------------------------------------------------------------------------------------------


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
                    HasImage = !GlobalClass.currentUser.HasImage
                )

                if (!givenUserData.equals(GlobalClass.currentUser) || cameraManager.getModifiedImageStatus() == true) {

                    val currentUserIndex = GlobalClass.Users.indexOf(GlobalClass.currentUser)
                    val currentUserDocumentIndex =
                        GlobalClass.documents.allUserIDs[currentUserIndex]


                    //loading screen on parent base view
                    //val parentView = requireActivity().findViewById<FrameLayout>(R.id.flBase)
                    //var loadingCover = GlobalClass.addLoadingCover(layoutInflater, parentView)
                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE



                    GlobalScope.launch() {
                        var databaseManager = DatabaseManager()


                        databaseManager.updateUserInFirestore(
                            givenUserData,
                            currentUserDocumentIndex
                        )

                        //if (modifiedPicture == true) {
                        if (cameraManager.getModifiedImageStatus() == true) {
                            //GlobalClass.InformUser("", "", requireActivity())
                            databaseManager.setUserImage(
                                requireContext(),
                                currentUserID,
                                cameraManager.getSelectedUri()
                                //selectedImageUri
                            )

                            GlobalClass.currentUserImage = databaseManager.getUserImage(
                                requireContext(),
                                GlobalClass.currentUser.UserID,
                                GlobalClass.currentUser.HasImage
                            )
                        }


                        //val currentUserIndex = GlobalClass.documents.allUserIDs[currentUserID]


                        withContext(Dispatchers.Main) {
                            GlobalClass.UpdateDataBase = true
                            Toast.makeText(context, "Changes Saved", Toast.LENGTH_SHORT).show()
                            //loadingCover.visibility = View.GONE
                            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE
                        }
                    }
                }

            }
        }


        //----------------------------------------------------------------------------------------------------



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


        binding.ivBackArrow.setOnClickListener()
        {
            //create local fragment controller
            val fragmentControl = FragmentManager()

            //go back the the general contacts page
            fragmentControl.replaceFragment(contacts(), R.id.flContent, parentFragmentManager)

        }


        // Inflate the layout for this fragment
        return view
    }

    suspend fun UpdateUI(/*loadingCover: ViewGroup*/) {

        try {


            if (!selectedUserID.isNullOrEmpty()) {


                for (user in GlobalClass.Users) {
                    if (user.UserID == selectedUserID) {
                        currentUserID = user.UserID
                        binding.tvContactName.text = "${user.FirstName} ${user.LastName}"

                        binding.tvCardHeader.text = user.FirstName + getString(R.string.cardOwnerHeading)
                        /*
                        var userType = getString(R.string.memberText)
                        if (user.MemberTypeID == 2) {
                            userType = getString(R.string.seniorMemberText)
                        }
                        binding.tvRole.text = userType
                         */
                        /*
                        var databaseManager = DatabaseManager()
                        binding.tvRole.text = databaseManager.getSingleMemberType(user.MemberTypeID)
                         */
                        //binding.tvRole.text

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

            //loadingCover.visibility = View.GONE
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
        binding.ivMyProfileImageTint.isVisible = false
        binding.tvMyProfileImageEditText.isVisible = false

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

            binding.ivMyProfileImageTint.isVisible = true
            binding.tvMyProfileImageEditText.isVisible = true

            binding.tfQuote.isEnabled = true
            binding.tfContactNumber.isEnabled = true
            binding.tfEmailAddress.isEnabled = true
            binding.tfCompanyName.isEnabled = true
            binding.tfLinkedIn.isEnabled = true
            binding.tfWebsite.isEnabled = true

        } else {

            binding.ivModifyContact.setImageDrawable(activity?.getDrawable(R.drawable.edit_icon))

            binding.ivMyProfileImageTint.isVisible = false
            binding.tvMyProfileImageEditText.isVisible = false

            binding.tfQuote.isEnabled = false
            binding.tfContactNumber.isEnabled = false
            binding.tfEmailAddress.isEnabled = false
            binding.tfCompanyName.isEnabled = false
            binding.tfLinkedIn.isEnabled = false
            binding.tfWebsite.isEnabled = false

        }

    }


}