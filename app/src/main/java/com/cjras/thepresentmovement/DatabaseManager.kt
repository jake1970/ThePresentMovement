package com.cjras.thepresentmovement

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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


            if (GlobalClass.currentUser.FirstName == "")
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
        val storageReference = FirebaseStorage.getInstance().reference.child("ContactImages/$userID")

        var bitmap = ContextCompat.getDrawable(context, R.drawable.person_icon)?.toBitmap()//: Bitmap?  =  BitmapFactory.decodeResource(context.resources, R.drawable.person_icon)

        val imgFile = File.createTempFile("temptImage", "jpg")

        if (userHasImage) {
            try {
                storageReference.getFile(imgFile).await()
                bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            } catch (e: Error) {
                GlobalClass.InformUser(
                    context.getString(R.string.errorText),
                    "${e.toString()}",
                    context
                )
            }
        }

        return bitmap
    }




}