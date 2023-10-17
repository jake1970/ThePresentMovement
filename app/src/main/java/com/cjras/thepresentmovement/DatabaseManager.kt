package com.cjras.thepresentmovement

import android.R.attr.left
import android.content.Context
import android.graphics.*
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File


class DatabaseManager {

    val db = Firebase.firestore

    //READ DATA
    //---------------------------------------------------------------------------------------------
    //get all users from database
    suspend fun getAllUsersFromFirestore(): ArrayList<UserDataClass> {
        val allUsers = arrayListOf<UserDataClass>()
        val querySnapshot = db.collection("Users").get().await()
        //GlobalClass.documents = DocumentID()
        for (document in querySnapshot) {


            val newUserID: String = document.data.getValue("UserID").toString()
            val newFirstName: String = document.data.getValue("FirstName").toString()
            val newLastName: String = document.data.getValue("LastName").toString()
            val newEmailAddress: String = document.data.getValue("EmailAddress").toString()
            val newMemberTypeID: Int = document.data.getValue("MemberTypeID").toString().toInt()
            val newQuote: String = document.data.getValue("Quote").toString()
            val newContactNumber: String = document.data.getValue("ContactNumber").toString()
            val newCompanyName: String = document.data.getValue("CompanyName").toString()
            val newLinkedIn: String = document.data.getValue("LinkedIn").toString()
            val newWebsite: String = document.data.getValue("Website").toString()
            val newHasImage: Boolean = document.data.getValue("HasImage").toString().toBoolean()

            val tempUser = UserDataClass(
                UserID = newUserID,
                FirstName = newFirstName,
                LastName = newLastName,
                EmailAddress = newEmailAddress,
                MemberTypeID = newMemberTypeID,
                Quote = newQuote,
                ContactNumber = newContactNumber,
                CompanyName = newCompanyName,
                LinkedIn = newLinkedIn,
                Website = newWebsite,
                HasImage = newHasImage
            )



            if (GlobalClass.currentUser.EmailAddress == "")
            {
                if (document.data.getValue("UserID").toString() == GlobalClass.currentUser.UserID) {

                    GlobalClass.currentUser = tempUser

                }
            }



            allUsers.add(tempUser)
            GlobalClass.documents.allUserIDs.add(document.id)
            //}
        }


        return allUsers
    }



    suspend fun getUserImage(context: Context, userID : String, userHasImage: Boolean) : Bitmap?
    {

        var defaultUserImage = context.getDrawable(R.drawable.person_icon)
        if (defaultUserImage != null) {
            defaultUserImage = defaultUserImage.mutate()
            defaultUserImage.colorFilter = PorterDuffColorFilter(context.resources.getColor(R.color.sub_grey), PorterDuff.Mode.SRC_IN)
        }

        var bitmap = defaultUserImage?.toBitmap()
        bitmap = addPaddingToBitmap(bitmap!!, 10)


        if (userHasImage) {
            try {
                val storageReference = FirebaseStorage.getInstance().reference.child("ContactImages/$userID")
                val imgFile = File.createTempFile("tempImage", "jpg")
                storageReference.getFile(imgFile).await()
                bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            } catch (e: Exception) {
                GlobalClass.InformUser(
                    context.getString(R.string.errorText),
                    "${e.toString()}",
                    context
                )
            }
        }

        return bitmap
    }



    private fun addPaddingToBitmap(originalBitmap: Bitmap, padding: Int): Bitmap {
        val newWidth = originalBitmap.width + 2 * padding
        val newHeight = originalBitmap.height + 2 * padding

        // Create a new Bitmap with the increased size
        val paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, originalBitmap.config)

        val canvas = Canvas(paddedBitmap)

        // Calculate the position to center the original image with padding
        val left = padding
        val top = padding

        // Draw the original Bitmap on the new Bitmap with padding
        canvas.drawBitmap(originalBitmap, left.toFloat(), top.toFloat(), null)

        return paddedBitmap
    }









}