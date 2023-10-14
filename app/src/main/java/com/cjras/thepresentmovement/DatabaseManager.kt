package com.cjras.thepresentmovement

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await


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
            //if (document.data.getValue("UserID").toString().toInt() == userID) {


            val newUserID : String = document.data.getValue("UserID").toString()
            val newFirstName: String = document.data.getValue("FirstName").toString()
            val newLastName: String = document.data.getValue("LastName").toString()
            val newEmailAddress : String = document.data.getValue("EmailAddress").toString()
            val newMemberTypeID: Int = document.data.getValue("MemberTypeID").toString().toInt()
            val newQuote: String = document.data.getValue("Quote").toString()
            val newContactNumber : String = document.data.getValue("ContactNumber").toString()
            val newCompanyName: String = document.data.getValue("CompanyName").toString()
            val newLinkedIn: String = document.data.getValue("LinkedIn").toString()
            val newWebsite : String = document.data.getValue("Website").toString()
            val newUserImageURI: String = document.data.getValue("UserImageURI").toString()

            val tempUser = UserDataClass(
                UserID  = newUserID,
                FirstName = newFirstName,
                LastName = newLastName,
                EmailAddress  = newEmailAddress,
                MemberTypeID = newMemberTypeID,
                Quote = newQuote,
                ContactNumber  = newContactNumber,
                CompanyName = newCompanyName,
                LinkedIn = newLinkedIn,
                Website  = newWebsite,
                UserImageURI = newUserImageURI
            )

            allUsers.add(tempUser)
            GlobalClass.documents.allUserIDs.add(document.id)
            //}
        }


        return allUsers
    }

}