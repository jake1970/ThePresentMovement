package com.cjras.thepresentmovement

import android.R.attr.left
import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class DatabaseManager {

    val db = Firebase.firestore

    //READ DATA
    //---------------------------------------------------------------------------------------------
    //get all users from database
    suspend fun getAllUsersFromFirestore(): ArrayList<UserDataClass> {
        val allUsers = arrayListOf<UserDataClass>()
        GlobalClass.documents.allUserIDs.clear()

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



            //if (GlobalClass.currentUser.EmailAddress == "")
            //{
                if (document.data.getValue("UserID").toString() == GlobalClass.currentUser.UserID) {
                    GlobalClass.currentUser = tempUser
                    GlobalClass.currentUserMemberType = MemberTypeDataClass().getSingleMemberType(tempUser.MemberTypeID)
                }
           // }



            allUsers.add(tempUser)
            GlobalClass.documents.allUserIDs.add(document.id)
            //}
        }

        //GlobalClass.Users = allUsers
        return allUsers
    }


    //get all member types
    suspend fun getAllMemberTypesFromFirestore(): ArrayList<MemberTypeDataClass> {
        val allMemberTypes = arrayListOf<MemberTypeDataClass>()
        GlobalClass.documents.allUserIDs.clear()

        val querySnapshot = db.collection("MemberType").get().await()
        //GlobalClass.documents = DocumentID()
        for (document in querySnapshot) {


            val newMemberTypeID: Int = document.data.getValue("MemberTypeID").toString().toInt()
            val newMemberType: String = document.data.getValue("MemberType").toString()


            val tempMemberType = MemberTypeDataClass(
                MemberTypeID = newMemberTypeID,
                MemberType = newMemberType
            )


            allMemberTypes.add(tempMemberType)
            GlobalClass.documents.allMemberTypeIDs.add(document.id)

        }

        return allMemberTypes
    }

   //get all announcements
    suspend fun getAllAnnouncementsFromFirestore(): ArrayList<AnnouncementDataClass> {
        val allAnnouncements = arrayListOf<AnnouncementDataClass>()
        GlobalClass.documents.allAnnouncmentIds.clear()

        val querySnapshot = db.collection("Announcements").get().await()

        for (document in querySnapshot) {

            val newAnnouncementID: Int = document.data.getValue("AnnouncementID").toString().toInt()
            val newAnnouncementTitle: String = document.data.getValue("AnnouncementTitle").toString()
            val newAnnouncementMessage: String = document.data.getValue("AnnouncementMessage").toString()
            val newAnnouncementDate: LocalDate = LocalDate.parse(document.data.getValue("AnnouncementDate").toString())
            val newUserID: String = document.data.getValue("UserID").toString()



            val tempAnnouncement = AnnouncementDataClass(
                 AnnouncementID = newAnnouncementID,
                 AnnouncementTitle = newAnnouncementTitle,
                 AnnouncementMessage  = newAnnouncementMessage,
                 AnnouncementDate = newAnnouncementDate,
                 UserID  = newUserID
            )


            allAnnouncements.add(tempAnnouncement)
            GlobalClass.documents.allAnnouncmentIds.add(document.id)

        }

        return allAnnouncements
    }


    //get all events
    suspend fun getAllEventsFromFirestore(): ArrayList<EventDataClass> {
        val allEvents = arrayListOf<EventDataClass>()
        GlobalClass.documents.allEventIDs.clear()

        val querySnapshot = db.collection("Events").get().await()

        for (document in querySnapshot) {

            val newEventID: Int = document.data.getValue("EventID").toString().toInt()
            val newEventTitle: String = document.data.getValue("EventTitle").toString()
            val newEventDate : LocalDate = LocalDate.parse(document.data.getValue("EventDate").toString())
            val newEventLink: String = document.data.getValue("EventLink").toString()
            val newUserID : String = document.data.getValue("UserID").toString()
            val newHasImage: Boolean = document.data.getValue("HasImage").toString().toBoolean()


            val tempEvent = EventDataClass(
                EventID = newEventID,
                EventTitle = newEventTitle,
                EventDate = newEventDate,
                EventLink = newEventLink,
                UserID  = newUserID,
                HasImage = newHasImage
            )


            allEvents.add(tempEvent)
            GlobalClass.documents.allEventIDs.add(document.id)

        }

        return allEvents
    }

    //get all projects
    suspend fun getAllProjectsFromFirestore(): ArrayList<ProjectDataClass> {
        val allProjects = arrayListOf<ProjectDataClass>()
        GlobalClass.documents.allProjectIds.clear()

        val querySnapshot = db.collection("Projects").get().await()

        for (document in querySnapshot) {

            val newProjectID: Int = document.data.getValue("ProjectID").toString().toInt()
            val newProjectTitle: String = document.data.getValue("ProjectTitle").toString()
            val newProjectDate : LocalDate = LocalDate.parse(document.data.getValue("ProjectDate").toString())
            val newProjectOverview: String = document.data.getValue("ProjectOverview").toString()
            val newProjectCompanyName : String = document.data.getValue("ProjectCompanyName").toString()
            val newProjectCompanyAbout: String = document.data.getValue("ProjectCompanyAbout").toString()
            val newUserID : String = document.data.getValue("UserID").toString()
            val newHasImage: Boolean = document.data.getValue("HasImage").toString().toBoolean()

            val tempProject = ProjectDataClass(
                ProjectID = newProjectID,
                ProjectTitle = newProjectTitle,
                ProjectDate  = newProjectDate,
                ProjectOverview = newProjectOverview,
                ProjectCompanyName = newProjectCompanyName,
                ProjectCompanyAbout = newProjectCompanyAbout,
                UserID  = newUserID,
                HasImage = newHasImage
            )


            allProjects.add(tempProject)
            GlobalClass.documents.allProjectIds.add(document.id)

        }

        return allProjects
    }

    suspend fun updateFromDatabase()
    {
        GlobalClass.MemberTypes = getAllMemberTypesFromFirestore()
        GlobalClass.Users = getAllUsersFromFirestore()
        GlobalClass.Announcements = getAllAnnouncementsFromFirestore()
        GlobalClass.Events = getAllEventsFromFirestore()
        GlobalClass.Projects = getAllProjectsFromFirestore()

        GlobalClass.UpdateDataBase = false
    }


//    suspend fun getSingleMemberType(givenMemberTypeID : Int): String {
//        val allMemberTypes = arrayListOf<MemberTypeDataClass>()
//        GlobalClass.documents.allUserIDs.clear()
//
//        var memberTypeString = ""
//
//        val querySnapshot = db.collection("MemberType").get().await()
//        //GlobalClass.documents = DocumentID()
//        for (document in querySnapshot) {
//
//
//            val indexMemberTypeID: Int = document.data.getValue("MemberTypeID").toString().toInt()
//            val indexnewMemberType: String = document.data.getValue("MemberType").toString()
//
//
//            if (givenMemberTypeID == indexMemberTypeID)
//            {
//                memberTypeString = indexnewMemberType
//                break
//            }
//
//        }
//
//        return memberTypeString
//    }

    //add new user to the users table
    fun addNewUserToFirestore(newUser: UserDataClass)
    {
        db.collection("Users")
            .add(newUser)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${it.id}")
                GlobalClass.UpdateDataBase = true
            }
    }

    suspend fun updateUserInFirestore(currentUser: UserDataClass, ID: String) {
        val userRef = db.collection("Users").document(ID)
        userRef.update(
            mapOf(
                "UserID" to currentUser.UserID,
                "FirstName" to currentUser.FirstName,
                "LastName" to currentUser.LastName,
                "EmailAddress" to currentUser.EmailAddress,
                "MemberTypeID" to currentUser.MemberTypeID,
                "Quote" to currentUser.Quote,
                "ContactNumber" to currentUser.ContactNumber,
                "CompanyName" to currentUser.CompanyName,
                "LinkedIn" to currentUser.LinkedIn,
                "Website" to currentUser.Website,
                "HasImage" to currentUser.HasImage,
            )
        ).await()
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


    suspend fun getEventImage(context: Context, eventID : Int, eventHasImage: Boolean) : Bitmap?
    {

        var bitmap = getEventDefaultImage(context)

        if (eventHasImage) {
            try {
                val storageReference = FirebaseStorage.getInstance().reference.child("EventImages/$eventID.jpg") //until add images from the ui is in, add .jpg extension
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


    fun getEventDefaultImage(context: Context) : Bitmap?
    {
        var defaultUserImage = context.getDrawable(R.drawable.e_icon)
        if (defaultUserImage != null) {
            defaultUserImage = defaultUserImage.mutate()
            defaultUserImage.colorFilter = PorterDuffColorFilter(context.resources.getColor(R.color.sub_grey), PorterDuff.Mode.SRC_IN)
        }

        var bitmap = defaultUserImage?.toBitmap()
        bitmap = addPaddingToBitmap(bitmap!!, 10)

        return bitmap
    }

    fun getProjectDefaultImage(context: Context) : Bitmap?
    {
        var defaultUserImage = context.getDrawable(R.drawable.p_icon)
        if (defaultUserImage != null) {
            defaultUserImage = defaultUserImage.mutate()
            defaultUserImage.colorFilter = PorterDuffColorFilter(context.resources.getColor(R.color.sub_grey), PorterDuff.Mode.SRC_IN)
        }

        var bitmap = defaultUserImage?.toBitmap()
        bitmap = addPaddingToBitmap(bitmap!!, 10)

        return bitmap
    }

    suspend fun setUserImage(context: Context, userID : String, selectedImageUri : Uri)
    {

        val imageLocation = "ContactImages/$userID"
        val storageReference = FirebaseStorage.getInstance().getReference(imageLocation)

        // binding.ivMyProfileImage.image

        storageReference.putFile(selectedImageUri)
            .addOnFailureListener{
                Toast.makeText(context, "Imaged Failed To Upload", Toast.LENGTH_SHORT).show()
            }
            .addOnSuccessListener {
                GlobalClass.currentUser.HasImage = true
        }.await()

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