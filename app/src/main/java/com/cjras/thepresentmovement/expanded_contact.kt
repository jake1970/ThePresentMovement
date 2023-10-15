package com.cjras.thepresentmovement

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.cjras.thepresentmovement.databinding.FragmentExpandedContactBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [expanded_contact.newInstance] factory method to
 * create an instance of this fragment.
 */
class expanded_contact : Fragment() { //R.layout.fragment_expanded_contact

    private var _binding: FragmentExpandedContactBinding? = null
    private val binding get() = _binding!!

    //private var selectedImageUri  = R.drawable.person_icon
    private lateinit var selectedImageUri : Uri
    private var currentUserID = ""
    //private var cameraManager = CameraHandler()

    //private lateinit var storageRef : StorageRe

    //----------------------------------------------------------------------------------------------------
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val CAMERA_REQUEST_CODE = 200
        private const val PICK_FROM_GALLERY = 1
    }
    //----------------------------------------------------------------------------------------------------

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentExpandedContactBinding.inflate(inflater, container, false)
        val view = binding.root


/*
        //----------------------------------------------------------------------------------------------------

        /*
         args.putBoolean("myProfile", false)
                 args.putBoolean("myProfile", false)
                args.putString("firstName", user.FirstName)
                args.putString("lastName", user.LastName)
                args.putString("emailAddress", user.EmailAddress)
                args.putString("memberType", userType)
                args.putString("quote", user.Quote)
                args.putString("contactNumber", user.ContactNumber)
                args.putString("companyName", user.CompanyName)
                args.putString("linkedIn", user.LinkedIn)
                args.putString("website", user.Website)
                args.putString("userImageURI", user.UserImageURI)
         */

        /*
        val myProfile = arguments?.getBoolean("myProfile")

        val firstName = arguments?.getString("firstName")
        val lastName = arguments?.getString("lastName")
        val emailAddress = arguments?.getString("emailAddress")
        val memberType = arguments?.getString("memberType")
        val quote = arguments?.getString("quote")
        val contactNumber = arguments?.getString("contactNumber")
        val companyName = arguments?.getString("companyName")
        val linkedIn = arguments?.getString("linkedIn")
        val website = arguments?.getString("website")
        val userImageURI = arguments?.getString("userImageURI")

         */



        //binding.tvContactName.setText(myPhoneNumber) // = getString("Your text")//myPhoneNumber.toString()

        //binding.tfContactNumber.text  = getString("Your text")//myPhoneNumber.toString()

        /*
        if (myProfile == true)
        {

        }

         */

 */

        //********************************************************
        //upload image to firestore
        //********************************************************

        selectedImageUri = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + resources.getResourcePackageName(R.drawable.person_icon)
                    + '/' + resources.getResourceTypeName(R.drawable.person_icon) + '/' + resources.getResourceEntryName(
                R.drawable.person_icon
            )
        )

        /*
        binding.ivModifyContact.setOnClickListener()
        {


            val storageReference = FirebaseStorage.getInstance().getReference("ContactImages/${currentUserID}")

            // binding.ivMyProfileImage.image

            storageReference.putFile(selectedImageUri).
                addOnSuccessListener {
                    Toast.makeText(activity, "Imaged Uploaded To Cloud Firestore", Toast.LENGTH_SHORT)
                }
                .addOnFailureListener{
                    Toast.makeText(activity, "Imaged Failed To Upload", Toast.LENGTH_SHORT)
                }
        }

         */

        val selectedUserID = arguments?.getString("selectedUserID")


        if (selectedUserID == GlobalClass.currentUser.UserID)
        {
            binding.ivMyProfileImage.setImageBitmap(GlobalClass.currentUserImage)
        }
        else
        {
          //new db manager r and get the users photo
        }



        if (!selectedUserID.isNullOrEmpty())
        {
            for (user in GlobalClass.Users)
            {
                if (user.UserID == selectedUserID)
                {
                    currentUserID = user.UserID
                    binding.tvContactName.text = "${user.FirstName} ${user.LastName}"

                    var userType = getString(R.string.memberText)
                    if (user.MemberTypeID == 2)
                    {
                        userType = getString(R.string.seniorMemberText)
                    }
                    binding.tvRole.text = userType

                    binding.tfQuote.setText(user.Quote)
                    binding.tfContactNumber.setText(user.ContactNumber)
                    binding.tfEmailAddress.setText(user.EmailAddress)
                    binding.tfCompanyName.setText(user.CompanyName)
                    binding.tfLinkedIn.setText(user.LinkedIn)
                    binding.tfWebsite.setText(user.Website)

                }
            }

        }

        binding.tvContactName.setOnClickListener()
        {

            val imageLocation = "ContactImages/$currentUserID"
            val storageReference = FirebaseStorage.getInstance().getReference(imageLocation)

            // binding.ivMyProfileImage.image

            storageReference.putFile(selectedImageUri).
            addOnSuccessListener {
                Toast.makeText(activity, "Imaged Uploaded To Cloud Firestore", Toast.LENGTH_SHORT)
            }
                .addOnFailureListener{
                    Toast.makeText(activity, "Imaged Failed To Upload", Toast.LENGTH_SHORT)
                }
        }


        binding.ivModifyContact.setOnClickListener()
        {


            val storageReference = FirebaseStorage.getInstance().reference.child("ContactImages/${currentUserID}")

            val imgFile = File.createTempFile("temptImage", "jpg")
            storageReference.getFile(imgFile)
                .addOnSuccessListener {
                    Toast.makeText(activity, "Image Retrieved From Cloud Firestore", Toast.LENGTH_SHORT)

                    val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                    binding.ivMyProfileImage.setImageBitmap(bitmap)

                }
                .addOnFailureListener{
                    Toast.makeText(activity, "Image Failed To Be Retrieved From Cloud Firestore", Toast.LENGTH_SHORT)
                }


        }

        //********************************************************

        //----------------------------------------------------------------------------------------------------

        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        //Select an image
        //---------------------------------------------------------------------------------------------------------------------------------------------------------



        binding.ivMyProfileImageTint.setOnClickListener()
        {
            //method to add an image
            handlePhoto()
            //cameraManager.handlePhoto()
        }


        binding.tvMyProfileImageEditText.setOnClickListener()
        {
            //method to add an image
            handlePhoto()
           // cameraManager.handlePhoto()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    //Camera Functions
    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to handle selecting and starting an image source for the contact photo
    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    private fun handlePhoto() {

        //new dialog
        val builder = AlertDialog.Builder(activity)

        //set the dialog title
        builder.setTitle(R.string.imageSourcePrompt)

        //set the source options for the dialog
        builder.setItems(R.array.imageSources) { dialog, which ->

            if (which == 0)
            {
                //if 0 then the user wants to take a new photo
                //call camera
                //check permissions
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                    //call open camera method
                    startCamera()
                } else {
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
                }

            }
            else
            {
                if (which == 1)
                {

                    //if 1 then the user wants to select an existing photo
                    //call photo library
                    //check permissions
                    if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        //call image selector method
                        startImageSelector()
                    } else {
                        ActivityCompat.requestPermissions(
                            requireActivity(), arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), PICK_FROM_GALLERY
                        )
                    }


                }
            }

        }

        //show the dialog
        builder.show()
    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to open image picker
    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    private fun startImageSelector() {
        //val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI) //EXTERNAL_CONTENT_URI
        //@Suppress("DEPRECATION")
        //startActivityForResult(gallery, PICK_FROM_GALLERY)

        if (galleryIntent.resolveActivity(requireActivity().packageManager) != null) {
            @Suppress("DEPRECATION")
            startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
        } else {
            Toast.makeText(requireContext(), "Photo Library is not available", Toast.LENGTH_SHORT).show()
        }

    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------



    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to start camera
    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    private fun startCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
            @Suppress("DEPRECATION")
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        } else {
            Toast.makeText(requireContext(), "Camera is not available", Toast.LENGTH_SHORT).show()
        }
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------


    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    //Catch Finished Activity
    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

       // cameraManager.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) || (requestCode == PICK_FROM_GALLERY && resultCode == Activity.RESULT_OK)) {


            if ((requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) || (requestCode == PICK_FROM_GALLERY && resultCode == Activity.RESULT_OK)) {


                var imageBitmap = data?.extras?.get("data") as Bitmap?

                if (imageBitmap == null) {
                    val imageUri = data?.data
                    imageBitmap = MediaStore.Images.Media.getBitmap(
                        activity?.contentResolver,
                        Uri.parse(imageUri.toString())
                    )
                }


                binding.ivMyProfileImage.setImageBitmap(imageBitmap)
                saveImageLocally(imageBitmap)



            }
        }

    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method Save Captured Image On Device Storage
    //---------------------------------------------------------------------------------------------------------------------------------------------------------


    private fun saveImageLocally(imageBitmap: Bitmap?) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_$timeStamp.jpg"

        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(storageDir, imageFileName)

       selectedImageUri = imageFile.toUri()

        //return view
        try {
            val fileOutputStream = FileOutputStream(imageFile)
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.close()

            Toast.makeText(requireContext(), "Image saved successfully", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------------------------------------------------------------------------

}