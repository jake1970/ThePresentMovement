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
     private val currentFragment: Fragment,
     private var imageContainer: ImageView,
     private var modifiedPicture: Boolean,
) {

     //the uri of the saved changed image
     private lateinit var selectedImageUri : Uri

     //the activity associated with the passed fragment
     private var currentActivity = currentFragment.requireActivity()

     //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val CAMERA_REQUEST_CODE = 200
        private const val PICK_FROM_GALLERY = 1
    }
     //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




     //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     //method to return the status of if the image was changed
     //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     fun getModifiedImageStatus() : Boolean
     {
         return modifiedPicture
     }
     //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



     //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     //method to return the changed image uri
     //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     fun getSelectedUri() : Uri
     {
         return selectedImageUri
     }
     //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to handle selecting and starting an image source for the image
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
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
                    //ask user for permissions
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
                        //ask user for permissions
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
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to open image picker
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private fun startImageSelector() {

        //intent to show the picture selector
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI) //EXTERNAL_CONTENT_URI

        //check if the image library is available
        if (galleryIntent.resolveActivity(currentActivity.packageManager) != null) {
            @Suppress("DEPRECATION")
            //start image selector intent
            pickImageFromGalleryForResult.launch(galleryIntent)
        } else {
            //inform the user that the photo library is unavailable
            Toast.makeText(currentActivity, "Photo Library is not available", Toast.LENGTH_SHORT).show()
        }
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to start camera
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private fun startCamera() {

        //intent to show the camera
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        //check if the camera is available
        if (cameraIntent.resolveActivity(currentActivity.packageManager) != null) {
            @Suppress("DEPRECATION")

            //start the camera intent
            takePhotoForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        } else {
            //inform the user that the camera is unavailable
            Toast.makeText(currentActivity, "Camera is not available", Toast.LENGTH_SHORT).show()
        }
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Catch Finished Activity
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

     //register the camera intent handling
     private val takePhotoForResult = currentFragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
         if (result.resultCode == Activity.RESULT_OK) {
             val intent = result.data
             onActivityResult(intent)
         }
     }

     //register the image selector handling
     val pickImageFromGalleryForResult = currentFragment.registerForActivityResult(
         ActivityResultContracts.StartActivityForResult()
     ) { result: ActivityResult ->
         if (result.resultCode == Activity.RESULT_OK) {
             val intent = result.data
             // handle image from gallery
             onActivityResult(intent)
         }
     }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to handle the image once it is selected or taken
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private fun onActivityResult(data: Intent?) {
        @Suppress("DEPRECATION")

        //the image taken/chosen
        var imageBitmap = data?.extras?.get("data") as Bitmap?

        //if the image is invalid
        if (imageBitmap == null) {

            //set the image uri
            val imageUri = data?.data

            //get the image bitmap
            imageBitmap = MediaStore.Images.Media.getBitmap(
                currentActivity.contentResolver,
                Uri.parse(imageUri.toString())
            )
        }

        //set the image modifier tracker to true
        modifiedPicture = true

        //set the image container component to the taken/chosen image
        imageContainer.setImageBitmap(imageBitmap)

        //call method to save the given image
        saveImageLocally(imageBitmap)

    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to save the image on the device
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private fun saveImageLocally(imageBitmap: Bitmap?) {

        //the time when the image was taken
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        //the file name of the given image
        val imageFileName = "IMG_$timeStamp.jpg"

        //the storage location to set the image
        val storageDir = currentActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        //the image file
        val imageFile = File(storageDir, imageFileName)

        //the uri of the selected image
        selectedImageUri = imageFile.toUri()


        try {

            //save the image
            val fileOutputStream = FileOutputStream(imageFile)
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.close()

            //inform the user of the successful image save
            Toast.makeText(currentActivity, "Image saved successfully", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()

            //inform the user of the failed image save
            Toast.makeText(currentActivity, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

}