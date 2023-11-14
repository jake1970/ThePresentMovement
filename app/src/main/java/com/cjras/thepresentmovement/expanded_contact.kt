package com.cjras.thepresentmovement

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.cjras.thepresentmovement.databinding.FragmentExpandedContactBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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

        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //View Binding
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        _binding = FragmentExpandedContactBinding.inflate(inflater, container, false)
        val view = binding.root
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        //set the camera management instance
        cameraManager = CameraHandler(this, binding.ivMyProfileImage, modifiedPicture)


        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //initial data population
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        //show loading screen
        requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
            View.VISIBLE

        try {

            //get the selected user ID from the passed argument
            selectedUserID = arguments?.getString("selectedUserID")

            //check if the user is the currently signed in user
            if (selectedUserID == GlobalClass.currentUser.UserID) {

                //hide loading screen
                requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                    View.GONE
            }

            GlobalClass.checkUser(this)

            //read data
            MainScope().launch {



                //if new information has been added pull new data from the database
                if (GlobalClass.UpdateDataBase == true) {

                    //show loading cover
                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                        View.VISIBLE


                    withContext(Dispatchers.Default) {

                        //new database manager
                        var databaseManager = DatabaseManager()

                        //call method to retrieve all data from the database
                        databaseManager.updateFromDatabase()


                    }
                }

                //call method to update the ui when the new database information has been loaded (if required)
                UpdateUI()

            }
        } catch (e: Exception) {
            GlobalClass.InformUser(getString(R.string.errorText), "$e", requireContext())
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //When the contact edit/modify button is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.ivModifyContact.setOnClickListener()
        {

            //check the screen is in edit mode
            if (currentEditMode == true) {

                //define variables to hold the status of the link validity
                var validWebsite = false
                var validLinkedIn = false


                //check if the website is not a valid link and is populated
                if (GlobalClass.isValidUrl(binding.tfWebsite.text.toString()) == false && binding.tfWebsite.text.toString() != "") {

                    //set the website error text
                    binding.tfWebsite.error = getString(R.string.invalidUrlText)
                } else {

                    //set the website validity status to true
                    validWebsite = true
                }


                //check if the linkedin link is not a valid link and is populated
                if (GlobalClass.isValidUrl(binding.tfLinkedIn.text.toString()) == false && binding.tfLinkedIn.text.toString() != "") {

                    //set the linkedin link error text
                    binding.tfLinkedIn.error = getString(R.string.invalidUrlText)
                } else {

                    //set the linkedin link validity status to true
                    validLinkedIn = true
                }


                //if both links are valid
                if (validWebsite == true && validLinkedIn == true) {

                    //invert the edit mode status
                    currentEditMode = !currentEditMode

                    //set the edit mode to the inverted status
                    setEditMode(currentEditMode)
                }

            } else {

                //invert the edit mode status
                currentEditMode = !currentEditMode

                //set the edit mode to the inverted status
                setEditMode(currentEditMode)
            }


            //if the screen is not in edit mode
            if (currentEditMode == false) {

                //new user with the updated values from the expanded contacts screen
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

                //if the user adds an image for the first time
                if (GlobalClass.currentUser.HasImage == false && cameraManager.getModifiedImageStatus() == true) {

                    //set the has image status to true
                    givenUserData.HasImage = true
                }

                //if the current user data is changed or the image is changed
                if (!givenUserData.equals(GlobalClass.currentUser) || cameraManager.getModifiedImageStatus() == true) {

                    //get the current user index
                    val currentUserIndex = GlobalClass.Users.indexOf(GlobalClass.currentUser)

                    //get the current user document ID index
                    val currentUserDocumentIndex =
                        GlobalClass.documents.allUserIDs[currentUserIndex]

                    //show the loading cover
                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                        View.VISIBLE



                    MainScope().launch() {

                        withContext(Dispatchers.Default) {

                            //new database manager
                            var databaseManager = DatabaseManager()

                            //call method to update the user data in the database
                            databaseManager.updateUserInFirestore(
                                givenUserData,
                                currentUserDocumentIndex
                            )

                            //check if the users image has been changed
                            if (cameraManager.getModifiedImageStatus() == true) {

                                //call method to set the new user image
                                databaseManager.setUserImage(
                                    requireContext(),
                                    currentUserID,
                                    cameraManager.getSelectedUri()
                                )

                                //cache the updated image
                                GlobalClass.currentUserImage = databaseManager.getUserImage(
                                    requireContext(),
                                    GlobalClass.currentUser.UserID,
                                    GlobalClass.currentUser.HasImage
                                ) //call method to get the new user image


                            }
                        }

                        //set the database update controller variable to true
                        GlobalClass.UpdateDataBase = true

                        //inform the user on the save status
                        Toast.makeText(context, "Changes Saved", Toast.LENGTH_SHORT).show()

                        //hide the loading cover
                        requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                            View.GONE

                    }
                }


            }
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //When the refresh button is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.ivRefresh.setOnClickListener()
        {
            try {
                //call method to refresh the current fragment to pull new information from the database manually
                GlobalClass.RefreshFragment(this@expanded_contact)
            }
            catch (e: Exception) {
                //call method to show the error
                GlobalClass.InformUser(
                    getString(R.string.errorText),
                    "$e",
                    requireContext()
                )
            }
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //When the edit profile picture components are clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.ivMyProfileImageTint.setOnClickListener()
        {
            //call method to handle modifying image
            cameraManager.handlePhoto()
        }

        binding.tvMyProfileImageEditText.setOnClickListener()
        {
            //call method to handle modifying image
            cameraManager.handlePhoto()
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //When the page header (title and back button) is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.llHeader.setOnClickListener()
        {
            //navigate one fragment backwards in the stack
            fragmentManager?.popBackStackImmediate()
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //when the website field is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tfWebsite.setOnClickListener()
        {

            //verify the link is valid and editing the link is disabled
            if (GlobalClass.isValidUrl(binding.tfWebsite.text.toString()) && binding.tfWebsite.isFocusable == false) {

                //call method open the valid link
                GlobalClass.openBrowser(binding.tfWebsite.text.toString(), requireActivity())
            }
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //when the linkedin link field is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tfLinkedIn.setOnClickListener()
        {

            //verify the link is valid and editing the link is disabled
            if (GlobalClass.isValidUrl(binding.tfLinkedIn.text.toString()) && binding.tfLinkedIn.isFocusable == false) {

                //call method open the valid link
                GlobalClass.openBrowser(binding.tfLinkedIn.text.toString(), requireActivity())
            }
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //when the email address field is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tfEmailAddress.setOnClickListener()
        {

            //check the validity of the email
            val isValidEmail = UserDataClass().isValidEmail(binding.tfEmailAddress.text.toString())

            //if the email is valid and the editing of the email is disabled
            if (isValidEmail == true && binding.tfEmailAddress.isFocusable == false) {

                //set the email recipient to the email fields value
                val recipientEmail = binding.tfEmailAddress.text.toString()

                //new referrer intent
                val intent = Intent(Intent.ACTION_SENDTO)

                //set the intent data
                intent.data =
                    Uri.parse("mailto:$recipientEmail") //source - https://stackoverflow.com/a/6506999

                //start the email intent
                startActivity(intent)
            }

        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //when the contact number field is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tfContactNumber.setOnClickListener()
        {

            //verify the phone number is valid and the editing of the phone number is disabled
            if (PhoneNumberUtils.isGlobalPhoneNumber(binding.tfContactNumber.text.toString()) == true && binding.tfContactNumber.isFocusable == false) {

                //new phone number referer intent
                val intent = Intent(Intent.ACTION_DIAL)

                //set the intent data
                intent.data = Uri.parse("tel:${binding.tfContactNumber.text}") //pass the specified phone number

                //start the intent
                startActivity(intent)
            }
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




        // Inflate the layout for this fragment
        return view
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to update the screen data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun UpdateUI() {

        try {

            //if the user id is passed and is not null
            if (!selectedUserID.isNullOrEmpty()) {

                //loop through the users
                for (user in GlobalClass.Users) {

                    //if the user matches the specified user
                    if (user.UserID == selectedUserID) {

                        //set the matching user id variable
                        currentUserID = user.UserID

                        //set the users contact name to the users fullname
                        binding.tvContactName.text = "${user.FirstName} ${user.LastName}"

                        //set the user contact card header
                        binding.tvCardHeader.text =
                            user.FirstName + getString(R.string.cardOwnerHeading)


                        //populate the fields with the matching users data
                        binding.tfQuote.setText(user.Quote)
                        binding.tfContactNumber.setText(user.ContactNumber)
                        binding.tfEmailAddress.setText(user.EmailAddress)
                        binding.tfCompanyName.setText(user.CompanyName)
                        binding.tfLinkedIn.setText(user.LinkedIn)
                        binding.tfWebsite.setText(user.Website)

                        //if the user is the currently signed in user
                        if (selectedUserID == GlobalClass.currentUser.UserID) {

                            //set the contacts profile image to the cached current user image
                            binding.ivMyProfileImage.setImageBitmap(GlobalClass.currentUserImage)

                            //set the edit mode to false
                            setEditMode(false)

                            //set the current users role/member type to the cached value
                            binding.tvRole.text = GlobalClass.currentUserMemberType
                        } else {

                            //if the user is not the currently signed in user

                            //call method to set the general (non-privileged) view
                            setGeneralView()

                            //new database manager
                            var databaseManager = DatabaseManager()

                            //get the contacts role/member type
                            binding.tvRole.text =
                                MemberTypeDataClass().getSingleMemberType(user.MemberTypeID) //call method to get the users member type text based on the member type ID

                            //show loading cover
                            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                                View.VISIBLE

                            //variable to hold the user contact image
                            var bitmap = databaseManager.getUserImage(
                                requireContext(),
                                user.UserID,
                                user.HasImage
                            ) //call method to get the users profile picture

                            //set the users profile image
                            binding.ivMyProfileImage.setImageBitmap(bitmap)

                        }

                        //exit loop once user is found
                        break

                    }
                }

            }

            //hide loading cover
            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                View.GONE

        } catch (e: Exception) {
            GlobalClass.InformUser(getString(R.string.errorText), "$e", requireContext())
        }


    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to set the general user view when the expanded contact is not the currently signed in user
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private fun setGeneralView() {

        //hide the edit button
        binding.ivModifyContact.isVisible = false

        //hide the image editing components
        binding.ivMyProfileImageTint.visibility = View.GONE
        binding.tvMyProfileImageEditText.visibility = View.GONE

        //set the fields to not focusable to prevent interaction
        binding.tfQuote.isFocusable = false
        binding.tfContactNumber.isFocusable = false
        binding.tfEmailAddress.isFocusable = false
        binding.tfCompanyName.isFocusable = false
        binding.tfLinkedIn.isFocusable = false
        binding.tfWebsite.isFocusable = false

        //set the fields input type to none to prevent editing
        binding.tfQuote.inputType = InputType.TYPE_NULL
        binding.tfContactNumber.inputType = InputType.TYPE_NULL
        binding.tfEmailAddress.inputType = InputType.TYPE_NULL
        binding.tfCompanyName.inputType = InputType.TYPE_NULL
        binding.tfLinkedIn.inputType = InputType.TYPE_NULL
        binding.tfWebsite.inputType = InputType.TYPE_NULL

    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to control the status of the editing of a users own information
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private fun setEditMode(currentlyEditing: Boolean) {

        //if the screen must be set to edit mode
        if (currentlyEditing == true) {

            //set the edit buttons image to indicate confirm changes
            binding.ivModifyContact.setImageDrawable(activity?.getDrawable(R.drawable.tick_icon))

            //show the image editing components
            binding.ivMyProfileImageTint.visibility = View.VISIBLE
            binding.tvMyProfileImageEditText.visibility = View.VISIBLE


            //allow the fields to be focused and interacted with
            binding.tfQuote.isFocusableInTouchMode = true
            binding.tfContactNumber.isFocusableInTouchMode = true
            binding.tfEmailAddress.isFocusableInTouchMode = true
            binding.tfCompanyName.isFocusableInTouchMode = true
            binding.tfLinkedIn.isFocusableInTouchMode = true
            binding.tfWebsite.isFocusableInTouchMode = true


            //set the fields input type to allow editing
            binding.tfQuote.inputType = InputType.TYPE_CLASS_TEXT
            binding.tfContactNumber.inputType = InputType.TYPE_CLASS_PHONE
            binding.tfEmailAddress.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            binding.tfCompanyName.inputType = InputType.TYPE_CLASS_TEXT
            binding.tfLinkedIn.inputType = InputType.TYPE_CLASS_TEXT
            binding.tfWebsite.inputType = InputType.TYPE_CLASS_TEXT


        } else {

            //if the screen must not be set to edit mode

            //set (reset) the edit buttons image to indicate edit information
            binding.ivModifyContact.setImageDrawable(activity?.getDrawable(R.drawable.edit_icon))

            //hide the image editing components
            binding.ivMyProfileImageTint.visibility = View.GONE
            binding.tvMyProfileImageEditText.visibility = View.GONE

            //block the fields from being focused and interacted with
            binding.tfQuote.isFocusable = false
            binding.tfContactNumber.isFocusable = false
            binding.tfEmailAddress.isFocusable = false
            binding.tfCompanyName.isFocusable = false
            binding.tfLinkedIn.isFocusable = false
            binding.tfWebsite.isFocusable = false

            //set the fields input type to block editing
            binding.tfQuote.inputType = InputType.TYPE_NULL
            binding.tfContactNumber.inputType = InputType.TYPE_NULL
            binding.tfEmailAddress.inputType = InputType.TYPE_NULL
            binding.tfCompanyName.inputType = InputType.TYPE_NULL
            binding.tfLinkedIn.inputType = InputType.TYPE_NULL
            binding.tfWebsite.inputType = InputType.TYPE_NULL

        }

    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


}