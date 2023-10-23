package com.cjras.thepresentmovement

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


 class CameraHandler(
    //var currentActivity: FragmentActivity,
     private val currentFragment: Fragment,
     private var imageContainer: ImageView,
     private var modifiedPicture: Boolean,
) {

     private lateinit var selectedImageUri : Uri
     private var currentActivity = currentFragment.requireActivity()



  //  lateinit var currentActivity: FragmentActivity
    //lateinit var imageContainer: ImageView


    //----------------------------------------------------------------------------------------------------
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val CAMERA_REQUEST_CODE = 200
        private const val PICK_FROM_GALLERY = 1
    }
    //----------------------------------------------------------------------------------------------------


     fun getModifiedImageStatus() : Boolean
     {
         return modifiedPicture
     }

     fun getSelectedUri() : Uri
     {
         return selectedImageUri
     }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    //Camera Functions
    //---------------------------------------------------------------------------------------------------------------------------------------------------------
/*
     selectedImageUri = Uri.parse(
     ContentResolver.SCHEME_ANDROID_RESOURCE +
     "://" + resources.getResourcePackageName(R.drawable.person_icon)
     + '/' + resources.getResourceTypeName(R.drawable.person_icon) + '/' + resources.getResourceEntryName(
     R.drawable.person_icon
     )
     )

 */

    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to handle selecting and starting an image source for the contact photo
    //---------------------------------------------------------------------------------------------------------------------------------------------------------
     fun handlePhoto() {

        modifiedPicture = false

        //new dialog
        val builder = AlertDialog.Builder(currentActivity)

        //set the dialog title
        builder.setTitle(R.string.imageSourcePrompt)

        //set the source options for the dialog
        builder.setItems(R.array.imageSources) { dialog, which ->

            if (which == 0)
            {
                //if 0 then the user wants to take a new photo
                //call camera
                //check permissions
                if (ContextCompat.checkSelfPermission(currentActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                    //call open camera method
                    startCamera()
                } else {
                    ActivityCompat.requestPermissions(currentActivity, arrayOf(Manifest.permission.CAMERA),
                        CAMERA_PERMISSION_CODE
                    )
                }

            }
            else
            {
                if (which == 1)
                {

                    //if 1 then the user wants to select an existing photo
                    //call photo library
                    //check permissions
                    if (ContextCompat.checkSelfPermission(currentActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        //call image selector method
                        startImageSelector()
                    } else {
                        ActivityCompat.requestPermissions(
                            currentActivity, arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            PICK_FROM_GALLERY
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
        /*
        //val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI) //EXTERNAL_CONTENT_URI
        //@Suppress("DEPRECATION")
        //startActivityForResult(gallery, PICK_FROM_GALLERY)

        if (galleryIntent.resolveActivity(currentActivity.packageManager) != null) {
            @Suppress("DEPRECATION")
            currentActivity.startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
        } else {
            Toast.makeText(currentActivity, "Photo Library is not available", Toast.LENGTH_SHORT).show()
        }

         */

        //val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI) //EXTERNAL_CONTENT_URI
        //@Suppress("DEPRECATION")
        //startActivityForResult(gallery, PICK_FROM_GALLERY)

        if (galleryIntent.resolveActivity(currentActivity.packageManager) != null) {
            @Suppress("DEPRECATION")
            pickImageFromGalleryForResult.launch(galleryIntent)
        } else {
            Toast.makeText(currentActivity, "Photo Library is not available", Toast.LENGTH_SHORT).show()
        }

    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------



    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to start camera
    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    private fun startCamera() {
       /*
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(currentActivity.packageManager) != null) {
            @Suppress("DEPRECATION")
            currentActivity.startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        } else {
            Toast.makeText(currentActivity, "Camera is not available", Toast.LENGTH_SHORT).show()
        }
        */

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(currentActivity.packageManager) != null) {
            @Suppress("DEPRECATION")
            takePhotoForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        } else {
            Toast.makeText(currentActivity, "Camera is not available", Toast.LENGTH_SHORT).show()
        }

    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------


    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    //Catch Finished Activity
    //---------------------------------------------------------------------------------------------------------------------------------------------------------



    //override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //@Suppress("DEPRECATION")
        //super.onActivityResult(requestCode, resultCode, data)

        //cameraManager.currentActivity = requireActivity()
        //cameraManager.imageContainer = binding.ivMyProfileImage
        //cameraManager.onActivityResult(requestCode, resultCode, data)

        /*
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

                modifiedPicture = true
                binding.ivMyProfileImage.setImageBitmap(imageBitmap)
                saveImageLocally(imageBitmap)



            }
        }

         */

   // }

     private val takePhotoForResult = currentFragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
         if (result.resultCode == Activity.RESULT_OK) {
             val intent = result.data
             onActivityResult(intent)
         }
     }

     val pickImageFromGalleryForResult = currentFragment.registerForActivityResult(
         ActivityResultContracts.StartActivityForResult()
     ) { result: ActivityResult ->
         if (result.resultCode == Activity.RESULT_OK) {
             val intent = result.data
             // handle image from gallery
             onActivityResult(intent)
         }
     }


    fun onActivityResult(data: Intent?) {
        @Suppress("DEPRECATION")


                var imageBitmap = data?.extras?.get("data") as Bitmap?

                if (imageBitmap == null) {
                    val imageUri = data?.data
                    imageBitmap = MediaStore.Images.Media.getBitmap(
                        currentActivity.contentResolver,
                        Uri.parse(imageUri.toString())
                    )
                }


                modifiedPicture = true
                imageContainer.setImageBitmap(imageBitmap)
                saveImageLocally(imageBitmap)



           // }
        //}

    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method Save Captured Image On Device Storage
    //---------------------------------------------------------------------------------------------------------------------------------------------------------


    private fun saveImageLocally(imageBitmap: Bitmap?) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_$timeStamp.jpg"

        val storageDir = currentActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(storageDir, imageFileName)

        selectedImageUri = imageFile.toUri()

        //return view
        try {
            val fileOutputStream = FileOutputStream(imageFile)
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.close()

            Toast.makeText(currentActivity, "Image saved successfully", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(currentActivity, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------------------------------------------------------------------------------

}