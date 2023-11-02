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

        _binding = FragmentExpandedContactBinding.inflate(inflater, container, false)
        val view = binding.root

        cameraManager = CameraHandler(this, binding.ivMyProfileImage, modifiedPicture)

        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        //initial data population
        //---------------------------------------------------------------------------------------------------------------------------------------------------------

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
        } catch (e: Exception) {
            GlobalClass.InformUser(getString(R.string.errorText), "$e", requireContext())
        }
        //---------------------------------------------------------------------------------------------------------------------------------------------------------






        binding.ivModifyContact.setOnClickListener()
        {

            if (currentEditMode == true)
            {
                var validWebsite = false
                var validLinkedIn = false

                if (GlobalClass.isValidUrl(binding.tfWebsite.text.toString()) == false && binding.tfWebsite.text.toString() != "")
                {
                    binding.tfWebsite.error = getString(R.string.invalidUrlText)
                }
                else
                {
                    validWebsite = true
                }


                if (GlobalClass.isValidUrl(binding.tfLinkedIn.text.toString()) == false && binding.tfLinkedIn.text.toString() != "")
                {
                    binding.tfLinkedIn.error = getString(R.string.invalidUrlText)
                }
                else
                {
                    validLinkedIn = true
                }

                if (validWebsite == true && validLinkedIn == true)
                {
                    currentEditMode = !currentEditMode
                    setEditMode(currentEditMode)
                }

            }
            else
            {
                currentEditMode = !currentEditMode
                setEditMode(currentEditMode)
            }



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

                    if (GlobalClass.currentUser.HasImage == false && cameraManager.getModifiedImageStatus() == true) {
                        givenUserData.HasImage = true
                    }

                    if (!givenUserData.equals(GlobalClass.currentUser) || cameraManager.getModifiedImageStatus() == true) {

                        val currentUserIndex = GlobalClass.Users.indexOf(GlobalClass.currentUser)
                        val currentUserDocumentIndex =
                            GlobalClass.documents.allUserIDs[currentUserIndex]

                        requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                            View.VISIBLE



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
                            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                                View.GONE

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


//-----------------------------------------------------------------------------------------------------
        binding.tfWebsite.setOnClickListener()
        {
            if (GlobalClass.isValidUrl(binding.tfWebsite.text.toString()) && binding.tfWebsite.isFocusable == false)
            {
                GlobalClass.openBrowser(binding.tfWebsite.text.toString(), requireActivity())
            }
        }


        binding.tfLinkedIn.setOnClickListener()
        {
            if (GlobalClass.isValidUrl(binding.tfLinkedIn.text.toString())&& binding.tfLinkedIn.isFocusable == false)
            {
                GlobalClass.openBrowser(binding.tfLinkedIn.text.toString(), requireActivity())
            }
        }


        binding.tfEmailAddress.setOnClickListener()
        {
            val isValidEmail = UserDataClass().isValidEmail(binding.tfEmailAddress.text.toString())

            if (isValidEmail == true && binding.tfEmailAddress.isFocusable == false)
            {
                val recepientEmail = binding.tfEmailAddress.text.toString()

                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:$recepientEmail") //source - https://stackoverflow.com/a/6506999
                startActivity(intent)
            }

        }


        binding.tfContactNumber.setOnClickListener()
        {
            if (PhoneNumberUtils.isGlobalPhoneNumber(binding.tfContactNumber.text.toString()) == true && binding.tfContactNumber.isFocusable == false)
            {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${binding.tfContactNumber.text}")
                startActivity(intent)
            }
        }

        //------
//        binding.tfWebsite.isClickable = true
//        binding.tfLinkedIn.isClickable = true
//        binding.tfEmailAddress.isClickable = true
//        binding.tfContactNumber.isClickable = true
//
//        binding.tfWebsite.isFocusable = false
//        binding.tfLinkedIn.isFocusable = false
//        binding.tfEmailAddress.isFocusable = false
//        binding.tfContactNumber.isFocusable = false
        //------

//-----------------------------------------------------------------------------------------------------


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
        

        binding.tfQuote.isFocusable = false
        binding.tfContactNumber.isFocusable = false
        binding.tfEmailAddress.isFocusable = false
        binding.tfCompanyName.isFocusable = false
        binding.tfLinkedIn.isFocusable = false
        binding.tfWebsite.isFocusable = false


        binding.tfQuote.inputType = InputType.TYPE_NULL
        binding.tfContactNumber.inputType = InputType.TYPE_NULL
        binding.tfEmailAddress.inputType = InputType.TYPE_NULL
        binding.tfCompanyName.inputType = InputType.TYPE_NULL
        binding.tfLinkedIn.inputType = InputType.TYPE_NULL
        binding.tfWebsite.inputType = InputType.TYPE_NULL

    }

    private fun setEditMode(currentlyEditing: Boolean) {


        if (currentlyEditing == true) {



            binding.ivModifyContact.setImageDrawable(activity?.getDrawable(R.drawable.tick_icon))

            binding.ivMyProfileImageTint.visibility = View.VISIBLE
            binding.tvMyProfileImageEditText.visibility = View.VISIBLE



            binding.tfQuote.isFocusableInTouchMode = true
            binding.tfContactNumber.isFocusableInTouchMode = true
            binding.tfEmailAddress.isFocusableInTouchMode = true
            binding.tfCompanyName.isFocusableInTouchMode = true
            binding.tfLinkedIn.isFocusableInTouchMode = true
            binding.tfWebsite.isFocusableInTouchMode = true




            binding.tfQuote.inputType = InputType.TYPE_CLASS_TEXT
            binding.tfContactNumber.inputType = InputType.TYPE_CLASS_PHONE
            binding.tfEmailAddress.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            binding.tfCompanyName.inputType = InputType.TYPE_CLASS_TEXT
            binding.tfLinkedIn.inputType = InputType.TYPE_CLASS_TEXT
            binding.tfWebsite.inputType = InputType.TYPE_CLASS_TEXT


        } else {




            binding.ivModifyContact.setImageDrawable(activity?.getDrawable(R.drawable.edit_icon))

            binding.ivMyProfileImageTint.visibility = View.GONE
            binding.tvMyProfileImageEditText.visibility = View.GONE


            binding.tfQuote.isFocusable = false
            binding.tfContactNumber.isFocusable = false
            binding.tfEmailAddress.isFocusable = false
            binding.tfCompanyName.isFocusable = false
            binding.tfLinkedIn.isFocusable = false
            binding.tfWebsite.isFocusable = false

            binding.tfQuote.inputType = InputType.TYPE_NULL
            binding.tfContactNumber.inputType = InputType.TYPE_NULL
            binding.tfEmailAddress.inputType = InputType.TYPE_NULL
            binding.tfCompanyName.inputType = InputType.TYPE_NULL
            binding.tfLinkedIn.inputType = InputType.TYPE_NULL
            binding.tfWebsite.inputType = InputType.TYPE_NULL

        }

    }



}