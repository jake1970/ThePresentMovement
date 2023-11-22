package com.cjras.thepresentmovement

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DatabaseExtensionFunctions {

    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to ask the user for confirmation to delete data from the database
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun deleteConfirmation(tableEntryDocumentIndex: String, context: Fragment, table: String, userID: String, projectID: Int)
    {

        //new alert dialog
        val builder = AlertDialog.Builder(context.requireActivity())
        builder.setMessage(context.getString(R.string.confirmDeletionText))
            .setCancelable(false)
            .setPositiveButton(context.getString(R.string.yesText)) { dialog, id ->

                //holds the type of record that will be deleted
                var deletionType = ""

                MainScope().launch() {
                    withContext(Dispatchers.Default) {
                        var databaseManager = DatabaseManager()

                        when(table)
                        {
                            "Projects" -> {
                                //if deleting project

                                //check if the project ID is valid
                                if (projectID != 0)
                                {
                                    //call method to delete a project from the database
                                    databaseManager.deleteProjectFromFirestore(tableEntryDocumentIndex, projectID)

                                    //set record deletion type
                                    deletionType = context.getString(R.string.projectsTextSingle)
                                }

                            }
                            "Events" -> {
                                //if deleting event

                                //call method to delete an event from the database
                                databaseManager.deleteEventFromFirestore(tableEntryDocumentIndex)

                                //set record deletion type
                                deletionType = context.getString(R.string.eventsTextSingle)
                            }
                            "Announcements" -> {
                                //if deleting announcement

                                //call method to delete an announcement from the database
                                databaseManager.deleteAnnouncementFromFirestore(tableEntryDocumentIndex)

                                //set record deletion type
                                deletionType = context.getString(R.string.announcementText)
                            }
                            "Users" -> {
                                //if deleting a user

                                //check if the user ID is valid
                                if (userID != "")
                                {
                                    //call method to delete a user from the database
                                    databaseManager.deleteUserFromFirestore(tableEntryDocumentIndex, userID)

                                    //set record deletion type
                                    deletionType = context.getString(R.string.userText)
                                }

                            }
                        }
                    }

                    //inform the user on the deletion of data
                    Toast.makeText(context.requireActivity(), "${context.getString(R.string.deletedText)} $deletionType", Toast.LENGTH_SHORT).show()

                    //call method to reload the fragment after deletion of data
                    GlobalClass.RefreshFragment(context)
                }

            }
            .setNegativeButton(context.getString(R.string.noText)) { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()

        //show the deletion prompt
        alert.show()
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to open an expanded view of a selected record
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun ExpandEntryData(tableEntryIndex: Int, editMode: Boolean, table: String, context: Fragment,)
    {
        //create local fragment controller
        val fragmentControl = FragmentManager()
        val args = Bundle()

        when(table)
        {
            "Projects" -> {
                //if opening a project

                //instance of expanded projects fragment
                val expandedProjectView = add_project()

                //set record data and arguments
                args.putInt("selectedProjectID", tableEntryIndex)
                args.putBoolean("editMode", editMode)

                expandedProjectView.arguments = args

                //open the expanded view
                fragmentControl.replaceFragment(
                    expandedProjectView,
                    R.id.flContent,
                    context.parentFragmentManager
                )
            }
            "Events" -> {

                //if opening an event

                //instance of expanded event fragment
                val expandedEventView = create_event()

                //set record data and arguments
                args.putInt("selectedEventID", tableEntryIndex)
                args.putBoolean("editMode", editMode)

                expandedEventView.arguments = args

                //open the expanded view
                fragmentControl.replaceFragment(
                    expandedEventView,
                    R.id.flContent,
                    context.parentFragmentManager
                )

            }
            "Announcements" -> {
                //if opening an announcement

                //instance of expanded announcement fragment
                val expandedAnnouncementView = create_announcement()

                //set record data and arguments
                args.putInt("selectedAnnouncementIndex", tableEntryIndex)
                args.putBoolean("editMode", editMode)

                expandedAnnouncementView.arguments = args

                //open the expanded view
                fragmentControl.replaceFragment(
                    expandedAnnouncementView,
                    R.id.flContent,
                    context.parentFragmentManager
                )
            }
        }
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to show a popup menu to perform admin functions on a selected record
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun showAdminOptionMenu(tableEntry: String, tableEntryIndex: Int, context: Fragment, table: String)
    {
        //new dialog
        val builder = AlertDialog.Builder(context.requireActivity())

        //set the dialog title
        builder.setTitle(R.string.adminMenuTitleText)

        //array of admin functions
        var menuArray = context.resources.getStringArray(R.array.adminItemFunctions)

        //check if user type is not admin
        if (GlobalClass.currentUser.MemberTypeID != 3)
        {
            //remove the delete option from the admin menu for non admin users
            menuArray = menuArray.filter { it != menuArray[2].toString() }.toTypedArray()
        }


        //set the source options for the dialog
        builder.setItems(menuArray) { dialog, selectedItem ->

            when (selectedItem)
            {
                0 -> {
                    //if view record is selected


                    when(table)
                    {

                        "Projects" -> {

                            //if view project is selected

                            //call method to expand and view record data
                            ExpandEntryData(tableEntryIndex, false, "Projects", context)
                        }
                        "Events" -> {
                            //if view event is selected

                            //call method to expand and view record data
                            ExpandEntryData(tableEntryIndex, false, "Events", context)

                        }
                        "Announcements" -> {
                            //if view announcement is selected


                            //popup to show full announcement data
                            var fullNotice =
                                MaterialAlertDialogBuilder(context.requireActivity(), R.style.NoticeAlert)
                                    .setTitle(GlobalClass.Announcements[tableEntryIndex].AnnouncementTitle)   //needs to be the index od the id
                                    .setMessage(GlobalClass.Announcements[tableEntryIndex].AnnouncementMessage)
                                    .setIcon((R.drawable.notification_bell))
                                    .setNeutralButton(context.getString(R.string.okText)) { dialog, which ->
                                        // Respond to neutral button press
                                    }

                            //show popup
                            fullNotice.show()

                        }
                    }

                }
                1 -> {
                    //if edit record is selected



                    when(table)
                    {
                        "Projects" -> {
                            //if edit project is selected

                            //call method to expand and edit record data
                            ExpandEntryData(tableEntryIndex, true, "Projects", context)
                        }
                        "Events" -> {
                            //if edit event is selected

                            //call method to expand and edit record data
                            ExpandEntryData(tableEntryIndex, true, "Events", context)
                        }
                        "Announcements" -> {
                            //if edit announcement is selected

                            //call method to expand and edit record data
                            ExpandEntryData(tableEntryIndex, true, "Announcements", context)
                        }
                    }
                }
                2 -> {
                    //if delete record is selected

                    when(table)
                    {

                        "Projects" -> {
                            //if delete project is selected

                            //the index of the selected project
                            var selectedProjectIndex = GlobalClass.Projects.indexOfLast { it.ProjectID == tableEntryIndex }

                            //if the selected project exists
                            if (selectedProjectIndex != -1) {

                                //call method to show a deletion prompt for projects
                                deleteConfirmation(tableEntry, context, "Projects", "", GlobalClass.Projects[selectedProjectIndex].ProjectID)
                            }

                        }
                        "Events" -> {
                            //if delete event is selected

                            //call method to show a deletion prompt for events
                            deleteConfirmation(tableEntry, context, "Events", "", 0)
                        }
                        "Announcements" -> {
                            //if delete announcement is selected

                            //call method to show a deletion prompt for announcements
                            deleteConfirmation(tableEntry, context, "Announcements", "", 0)
                        }
                    }
                }
                else -> {

                }
            }
        }
        //show the dialog
        builder.show()
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



}