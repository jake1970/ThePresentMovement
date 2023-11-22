package com.cjras.thepresentmovement

import android.R.attr.left
import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.firestore.WriteBatch
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

    //the database instance
    val db = Firebase.firestore


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to get all users from the database
    //source - https://firebase.google.com/docs/firestore/manage-data/add-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun getAllUsersFromFirestore(): ArrayList<UserDataClass> {


        val allUsers = arrayListOf<UserDataClass>()
        GlobalClass.documents.allUserIDs.clear()

        val querySnapshot = db.collection("Users").get().await()
        //GlobalClass.documents = DocumentID()
        for (document in querySnapshot) {


            val newUserID: String = document.data.getValue("userID").toString()
            val newFirstName: String = document.data.getValue("firstName").toString()
            val newLastName: String = document.data.getValue("lastName").toString()
            val newEmailAddress: String = document.data.getValue("emailAddress").toString()
            val newMemberTypeID: Int = document.data.getValue("memberTypeID").toString().toInt()
            val newQuote: String = document.data.getValue("quote").toString()
            val newContactNumber: String = document.data.getValue("contactNumber").toString()
            val newCompanyName: String = document.data.getValue("companyName").toString()
            val newLinkedIn: String = document.data.getValue("linkedIn").toString()
            val newWebsite: String = document.data.getValue("website").toString()
            val newHasImage: Boolean = document.data.getValue("hasImage").toString().toBoolean()

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
            if (newUserID == GlobalClass.currentUser.UserID) {
                GlobalClass.currentUser = tempUser
                GlobalClass.currentUserMemberType =
                    MemberTypeDataClass().getSingleMemberType(tempUser.MemberTypeID)
            }
            // }


            allUsers.add(tempUser)
            GlobalClass.documents.allUserIDs.add(document.id)
            //}
        }

        //GlobalClass.Users = allUsers
        return allUsers
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to get all member types from the database
    //source - https://firebase.google.com/docs/firestore/manage-data/add-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun getAllMemberTypesFromFirestore(): ArrayList<MemberTypeDataClass> {
        val allMemberTypes = arrayListOf<MemberTypeDataClass>()
        GlobalClass.documents.allUserIDs.clear()

        val querySnapshot = db.collection("MemberType").get().await()
        //GlobalClass.documents = DocumentID()
        for (document in querySnapshot) {


            val newMemberTypeID: Int = document.data.getValue("memberTypeID").toString().toInt()
            val newMemberType: String = document.data.getValue("memberType").toString()


            val tempMemberType = MemberTypeDataClass(
                MemberTypeID = newMemberTypeID,
                MemberType = newMemberType
            )


            allMemberTypes.add(tempMemberType)
            GlobalClass.documents.allMemberTypeIDs.add(document.id)

        }

        return allMemberTypes
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to get all announcements from the database
    //source - https://firebase.google.com/docs/firestore/manage-data/add-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllAnnouncementsFromFirestore(): ArrayList<AnnouncementDataClass> {
        val allAnnouncements = arrayListOf<AnnouncementDataClass>()
        GlobalClass.documents.allAnnouncmentIds.clear()

        val querySnapshot = db.collection("Announcements").get().await()

        for (document in querySnapshot) {

            //val newAnnouncementID: Int = document.data.getValue("announcementID").toString().toInt()
            val newAnnouncementTitle: String =
                document.data.getValue("announcementTitle").toString()
            val newAnnouncementMessage: String =
                document.data.getValue("announcementMessage").toString()
            val newAnnouncementDate: LocalDate =
                LocalDate.parse(document.data.getValue("announcementDate").toString())
            val newUserID: String = document.data.getValue("userID").toString()


            val tempAnnouncement = AnnouncementDataClass(
                AnnouncementID = document.id,
                AnnouncementTitle = newAnnouncementTitle,
                AnnouncementMessage = newAnnouncementMessage,
                AnnouncementDate = newAnnouncementDate,
                UserID = newUserID
            )


            allAnnouncements.add(tempAnnouncement)
            GlobalClass.documents.allAnnouncmentIds.add(document.id)

        }

        return allAnnouncements
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to get all events from the database
    //source - https://firebase.google.com/docs/firestore/manage-data/add-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllEventsFromFirestore(): ArrayList<EventDataClass> {
        val allEvents = arrayListOf<EventDataClass>()
        GlobalClass.documents.allEventIDs.clear()

        val querySnapshot = db.collection("Events").get().await()

        for (document in querySnapshot) {

            val newEventID: Int = document.data.getValue("eventID").toString().toInt()
            val newEventTitle: String = document.data.getValue("eventTitle").toString()
            val newEventDate: LocalDate =
                LocalDate.parse(document.data.getValue("eventDate").toString())
            val newEventLink: String = document.data.getValue("eventLink").toString()
            val newUserID: String = document.data.getValue("userID").toString()
            val newHasImage: Boolean = document.data.getValue("hasImage").toString().toBoolean()


            val tempEvent = EventDataClass(
                EventID = newEventID,
                EventTitle = newEventTitle,
                EventDate = newEventDate,
                EventLink = newEventLink,
                UserID = newUserID,
                HasImage = newHasImage
            )


            allEvents.add(tempEvent)
            GlobalClass.documents.allEventIDs.add(document.id)

        }

        return allEvents
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to get all projects from the database
    //source - https://firebase.google.com/docs/firestore/manage-data/add-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllProjectsFromFirestore(): ArrayList<ProjectDataClass> {
        val allProjects = arrayListOf<ProjectDataClass>()
        GlobalClass.documents.allProjectIds.clear()

        val querySnapshot = db.collection("Projects").get().await()

        for (document in querySnapshot) {

            val newProjectID: Int = document.data.getValue("projectID").toString().toInt()
            val newProjectTitle: String = document.data.getValue("projectTitle").toString()
            val newProjectDate: LocalDate =
                LocalDate.parse(document.data.getValue("projectDate").toString())
            val newProjectOverview: String = document.data.getValue("projectOverview").toString()
            val newProjectCompanyName: String =
                document.data.getValue("projectCompanyName").toString()
            val newProjectCompanyAbout: String =
                document.data.getValue("projectCompanyAbout").toString()
            val newUserID: String = document.data.getValue("userID").toString()
            val newHasImage: Boolean = document.data.getValue("hasImage").toString().toBoolean()

            val tempProject = ProjectDataClass(
                ProjectID = newProjectID,
                ProjectTitle = newProjectTitle,
                ProjectDate = newProjectDate,
                ProjectOverview = newProjectOverview,
                ProjectCompanyName = newProjectCompanyName,
                ProjectCompanyAbout = newProjectCompanyAbout,
                UserID = newUserID,
                HasImage = newHasImage
            )


            allProjects.add(tempProject)
            GlobalClass.documents.allProjectIds.add(document.id)

        }

        return allProjects
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to get all user projects from the database
    //source - https://firebase.google.com/docs/firestore/manage-data/add-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun getAllUserProjectsFromFirestore(): ArrayList<UserProjectDataClass> {
        val allUserProjects = arrayListOf<UserProjectDataClass>()
        GlobalClass.documents.allUserProjectIds.clear()

        val querySnapshot = db.collection("UserProjects").get().await()

        for (document in querySnapshot) {

            val newUserProjectID: Int = document.data.getValue("userProjectID").toString().toInt()
            val newUserID: String = document.data.getValue("userID").toString()
            val newProjectID: Int = document.data.getValue("projectID").toString().toInt()


            val tempUserProject = UserProjectDataClass(
                UserProjectID = newUserProjectID,
                UserID = newUserID,
                ProjectID = newProjectID,
            )

            allUserProjects.add(tempUserProject)
            GlobalClass.documents.allUserProjectIds.add(document.id)

        }

        return allUserProjects
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to get all data from the database
    //source - https://firebase.google.com/docs/firestore/manage-data/add-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateFromDatabase() {
        GlobalClass.MemberTypes = getAllMemberTypesFromFirestore()
        GlobalClass.Users = getAllUsersFromFirestore()
        GlobalClass.Announcements = getAllAnnouncementsFromFirestore()
        GlobalClass.Events = getAllEventsFromFirestore()
        GlobalClass.Projects = getAllProjectsFromFirestore()
        GlobalClass.UserProjects = getAllUserProjectsFromFirestore()

        GlobalClass.UpdateDataBase = false
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to add a new user to the database
    //source - https://firebase.google.com/docs/firestore/manage-data/add-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun addNewUserToFirestore(newUser: UserDataClass) {
        db.collection("Users")
            .add(newUser)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${it.id}")
                GlobalClass.UpdateDataBase = true
            }
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to add a new project to the database
    //source - https://firebase.google.com/docs/firestore/manage-data/add-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun addNewProjectToFirestore(newProject: ProjectDataClass) {
        db.collection("Projects")
            .add(
                mapOf(
                    "projectID" to newProject.ProjectID,
                    "projectTitle" to newProject.ProjectTitle,
                    "projectDate" to newProject.ProjectDate.toString(),
                    "projectOverview" to newProject.ProjectOverview,
                    "projectCompanyName" to newProject.ProjectCompanyName,
                    "projectCompanyAbout" to newProject.ProjectCompanyAbout,
                    "userID" to newProject.UserID,
                    "hasImage" to newProject.HasImage
                )
            )
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${it.id}")
                GlobalClass.UpdateDataBase = true
            }
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to add a new announcement to the database
    //source - https://firebase.google.com/docs/firestore/manage-data/add-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun addNewAnnouncementToFirestore(newAnnouncement: AnnouncementDataClass) {
        db.collection("Announcements")
            .add(
                mapOf(
                    //"announcementID" to newAnnouncement.AnnouncementID,
                    "announcementTitle" to newAnnouncement.AnnouncementTitle,
                    "announcementMessage" to newAnnouncement.AnnouncementMessage,
                    "announcementDate" to newAnnouncement.AnnouncementDate.toString(),
                    "userID" to newAnnouncement.UserID
                )
            )
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${it.id}")
                GlobalClass.UpdateDataBase = true
            }
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to add a new event to the database
    //source - https://firebase.google.com/docs/firestore/manage-data/add-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun addNewEventToFirestore(newEvent: EventDataClass) {
        db.collection("Events")
            .add(
                mapOf(
                    "eventID" to newEvent.EventID,
                    "eventTitle" to newEvent.EventTitle,
                    "eventDate" to newEvent.EventDate.toString(),
                    "eventLink" to newEvent.EventLink,
                    "userID" to newEvent.UserID,
                    "hasImage" to newEvent.HasImage
                )
            )
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${it.id}")
                GlobalClass.UpdateDataBase = true
            }
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to add a new user project to the database
    //source - https://firebase.google.com/docs/firestore/manage-data/add-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun addNewUserProjectToFirestore(newUserProject: UserProjectDataClass) {
        db.collection("UserProjects")
            .add(
                mapOf(
                    "userProjectID" to newUserProject.UserProjectID,
                    "userID" to newUserProject.UserID,
                    "projectID" to newUserProject.ProjectID
                )
            )
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${it.id}")
                GlobalClass.UpdateDataBase = true
            }
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to update an existing user in the database
    //source - https://firebase.google.com/docs/firestore/manage-data/delete-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun updateUserInFirestore(currentUser: UserDataClass, ID: String) {
        val userRef = db.collection("Users").document(ID)
        userRef.update(
            mapOf(
                "userID" to currentUser.UserID,
                "firstName" to currentUser.FirstName,
                "lastName" to currentUser.LastName,
                "emailAddress" to currentUser.EmailAddress,
                "memberTypeID" to currentUser.MemberTypeID,
                "quote" to currentUser.Quote,
                "contactNumber" to currentUser.ContactNumber,
                "companyName" to currentUser.CompanyName,
                "linkedIn" to currentUser.LinkedIn,
                "website" to currentUser.Website,
                "hasImage" to currentUser.HasImage,
            )
        ).await()
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to update an existing project in the database
    //source - https://firebase.google.com/docs/firestore/manage-data/delete-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun updateProjectInFirestore(currentProject: ProjectDataClass, ID: String) {
        val projectRef = db.collection("Projects").document(ID)
        projectRef.update(
            mapOf(
                "projectID" to currentProject.ProjectID,
                "projectTitle" to currentProject.ProjectTitle,
                "projectDate" to currentProject.ProjectDate.toString(),
                "projectOverview" to currentProject.ProjectOverview,
                "projectCompanyName" to currentProject.ProjectCompanyName,
                "projectCompanyAbout" to currentProject.ProjectCompanyAbout,
                "userID" to currentProject.UserID,
                "hasImage" to currentProject.HasImage
            )
        ).await()
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to update an existing event in the database
    //source - https://firebase.google.com/docs/firestore/manage-data/delete-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun updateEventInFirestore(currentEvent: EventDataClass, ID: String) {
        val eventRef = db.collection("Events").document(ID)
        eventRef.update(
            mapOf(
                "eventID" to currentEvent.EventID,
                "eventTitle" to currentEvent.EventTitle,
                "eventDate" to currentEvent.EventDate.toString(),
                "eventLink" to currentEvent.EventLink,
                "userID" to currentEvent.UserID,
                "hasImage" to currentEvent.HasImage
            )
        ).await()
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to update an existing announcement in the database
    //source - https://firebase.google.com/docs/firestore/manage-data/delete-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun updateAnnouncementInFirestore(
        currentAnnouncement: AnnouncementDataClass,
        ID: String,
    ) {
        val announcementRef = db.collection("Announcements").document(ID)
        announcementRef.update(
            mapOf(
                "announcementTitle" to currentAnnouncement.AnnouncementTitle,
                "announcementMessage" to currentAnnouncement.AnnouncementMessage,
                "announcementDate" to currentAnnouncement.AnnouncementDate.toString(),
                "userID" to currentAnnouncement.UserID
            )
        ).await()
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to remove a user from the database
    //source - https://firebase.google.com/docs/firestore/manage-data/delete-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun deleteUserFromFirestore(ID: String, userID: String) {

        batchDeleteUserProjectFromFirestoreUserID(userID)

        val userRef = db.collection("Users").document(ID)
        userRef.delete().await()
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to remove a series of user projects from the database when a user is deleted
    //source - https://firebase.google.com/docs/firestore/manage-data/delete-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun batchDeleteUserProjectFromFirestoreUserID(userID: String) {

        db.collection("UserProjects")
            .whereEqualTo("userID", userID)
            .get()
            .addOnSuccessListener { result ->
                var batch = db.batch();

                for (doc in result) {
                    batch.delete(doc.reference)
                }

                batch.commit()

            }.await()
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to remove a series of user projects from the database when a project is deleted
    //source - https://firebase.google.com/docs/firestore/manage-data/delete-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun batchDeleteUserProjectFromFirestoreProjectID(projectID: Int) {

        db.collection("UserProjects")
            .whereEqualTo("projectID", projectID)
            .get()
            .addOnSuccessListener { result ->
                var batch = db.batch();

                for (doc in result) {
                    batch.delete(doc.reference)
                }

                batch.commit()

            }.await()
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to remove a project from the database
    //source - https://firebase.google.com/docs/firestore/manage-data/delete-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun deleteProjectFromFirestore(ID: String, projectID: Int) {

        batchDeleteUserProjectFromFirestoreProjectID(projectID)

        val projectRef = db.collection("Projects").document(ID)
        projectRef.delete().await()
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to remove an event from the database
    //source - https://firebase.google.com/docs/firestore/manage-data/delete-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun deleteEventFromFirestore(ID: String) {
        val projectRef = db.collection("Events").document(ID)
        projectRef.delete().await()
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to remove an announcement from the database
    //source - https://firebase.google.com/docs/firestore/manage-data/delete-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun deleteAnnouncementFromFirestore(ID: String) {
        val announcementRef = db.collection("Announcements").document(ID)
        announcementRef.delete().await()
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to remove a user project from the database
    //source - https://firebase.google.com/docs/firestore/manage-data/delete-data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun deleteUserProjectFromFirestore(ID: String) {
        val userProjectRef = db.collection("UserProjects").document(ID)
        userProjectRef.delete().await()
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to get a users profile picture from the firestore storage
    //source - https://stackoverflow.com/a/62193604
    //source - https://firebase.google.com/docs/storage/android/start
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun getUserImage(context: Context, userID: String, userHasImage: Boolean): Bitmap? {

        var defaultUserImage = context.getDrawable(R.drawable.person_icon)
        if (defaultUserImage != null) {
            defaultUserImage = defaultUserImage.mutate()
            defaultUserImage.colorFilter = PorterDuffColorFilter(
                context.resources.getColor(R.color.sub_grey),
                PorterDuff.Mode.SRC_IN
            )
        }

        var bitmap = defaultUserImage?.toBitmap()
        bitmap = addPaddingToBitmap(bitmap!!, 10)


        if (userHasImage) {
            try {
                val storageReference =
                    FirebaseStorage.getInstance().reference.child("ContactImages/$userID")
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
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to get an events picture from the firestore storage
    //source - https://firebase.google.com/docs/storage/android/start
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun getEventImage(context: Context, eventID: Int, eventHasImage: Boolean): Bitmap? {

        var bitmap = getEventDefaultImage(context)

        if (eventHasImage) {
            try {
                val storageReference =
                    FirebaseStorage.getInstance().reference.child("EventImages/$eventID")
                val imgFile = File.createTempFile("tempImage", "jpg")
                storageReference.getFile(imgFile).await()
                bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            } catch (e: Exception) {
                bitmap = getEventDefaultImage(context)
            }
        }

        return bitmap
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to get a projects picture from the firestore storage
    //source - https://firebase.google.com/docs/storage/android/start
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun getProjectImage(context: Context, projectID: Int, eventHasImage: Boolean): Bitmap? {

        var bitmap = getProjectDefaultImage(context)

        if (eventHasImage) {
            try {
                val storageReference =
                    FirebaseStorage.getInstance().reference.child("ProjectImages/$projectID")
                val imgFile = File.createTempFile("tempImage", "jpg")
                storageReference.getFile(imgFile).await()
                bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            } catch (e: Exception) {
                bitmap = getProjectDefaultImage(context)
            }
        }

        return bitmap
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to get the default (monogram) event image
    //source - https://stackoverflow.com/a/62193604
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun getEventDefaultImage(context: Context): Bitmap? {
        var defaultUserImage = context.getDrawable(R.drawable.e_icon)
        if (defaultUserImage != null) {
            defaultUserImage = defaultUserImage.mutate()
            defaultUserImage.colorFilter = PorterDuffColorFilter(
                context.resources.getColor(R.color.sub_grey),
                PorterDuff.Mode.SRC_IN
            )
        }

        var bitmap = defaultUserImage?.toBitmap()
        bitmap = addPaddingToBitmap(bitmap!!, 10)

        return bitmap
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to get the default (monogram) project image
    //source - https://stackoverflow.com/a/62193604
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun getProjectDefaultImage(context: Context): Bitmap? {
        var defaultUserImage = context.getDrawable(R.drawable.p_icon)
        if (defaultUserImage != null) {
            defaultUserImage = defaultUserImage.mutate()
            defaultUserImage.colorFilter = PorterDuffColorFilter(
                context.resources.getColor(R.color.sub_grey),
                PorterDuff.Mode.SRC_IN
            )
        }

        var bitmap = defaultUserImage?.toBitmap()
        bitmap = addPaddingToBitmap(bitmap!!, 10)

        return bitmap
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to set a users profile picture in firestore storage
    //source - https://firebase.google.com/docs/storage/android/start
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun setUserImage(context: Context, userID: String, selectedImageUri: Uri) {

        val imageLocation = "ContactImages/$userID"
        val storageReference = FirebaseStorage.getInstance().getReference(imageLocation)

        // binding.ivMyProfileImage.image

        storageReference.putFile(selectedImageUri)
            .addOnFailureListener {
                Toast.makeText(context, context.getString(R.string.imageUploadFailed), Toast.LENGTH_SHORT).show()
            }
            .addOnSuccessListener {
                GlobalClass.currentUser.HasImage = true
            }.await()

    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to set a projects picture in firestore storage
    //source - https://firebase.google.com/docs/storage/android/start
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun setProjectImage(context: Context, projectID: Int, selectedImageUri: Uri) {

        val imageLocation = "ProjectImages/$projectID"
        val storageReference = FirebaseStorage.getInstance().getReference(imageLocation)

        // binding.ivMyProfileImage.image

        storageReference.putFile(selectedImageUri)
            .addOnFailureListener {
                Toast.makeText(context, context.getString(R.string.imageUploadFailed), Toast.LENGTH_SHORT).show()
            }
            .addOnSuccessListener {
                //set current project has image to true
                var selectedProjectIndex =
                    GlobalClass.Projects.indexOfLast { it.ProjectID == projectID }
                GlobalClass.Projects[selectedProjectIndex].HasImage = true
            }.await()

    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to set an events picture in firestore storage
    //source - https://firebase.google.com/docs/storage/android/start
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    suspend fun setEventImage(context: Context, eventID: Int, selectedImageUri: Uri) {

        val imageLocation = "EventImages/$eventID"
        val storageReference = FirebaseStorage.getInstance().getReference(imageLocation)


        storageReference.putFile(selectedImageUri)
            .addOnFailureListener {
                Toast.makeText(context, context.getString(R.string.imageUploadFailed), Toast.LENGTH_SHORT).show()
            }
            .addOnSuccessListener {
                //set current event has image to true
                var selectedEventIndex = GlobalClass.Events.indexOfLast { it.EventID == eventID }
                GlobalClass.Events[selectedEventIndex].HasImage = true
            }.await()

    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to add padding to an image to better fit UI components
    //source - https://stackoverflow.com/a/59771560
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private fun addPaddingToBitmap(originalBitmap: Bitmap, padding: Int): Bitmap {

        //calculate image with padding
        val newWidth = originalBitmap.width + 2 * padding
        val newHeight = originalBitmap.height + 2 * padding

        //new image with padding
        val paddedBitmap = Bitmap.createBitmap(newWidth, newHeight, originalBitmap.config)

        //set bitmap canvas
        val canvas = Canvas(paddedBitmap)

        //draw updated bitmap
        canvas.drawBitmap(originalBitmap, padding.toFloat(), padding.toFloat(), null)

        return paddedBitmap
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


}