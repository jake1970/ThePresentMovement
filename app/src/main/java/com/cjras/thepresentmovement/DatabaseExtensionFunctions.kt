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
        val builder = AlertDialog.Builder(context.requireActivity())
        builder.setMessage(context.getString(R.string.confirmDeletionText))
            .setCancelable(false)
            .setPositiveButton(context.getString(R.string.yesText)) { dialog, id ->

                var deletionType = ""
                MainScope().launch() {
                    withContext(Dispatchers.Default) {
                        var databaseManager = DatabaseManager()



                        when(table)
                        {
                            "Projects" -> {

                                if (projectID != 0)
                                {
                                    databaseManager.deleteProjectFromFirestore(tableEntryDocumentIndex, projectID)
                                    deletionType = context.getString(R.string.projectsTextSingle)
                                }

                            }
                            "Events" -> {
                                databaseManager.deleteEventFromFirestore(tableEntryDocumentIndex)
                                deletionType = context.getString(R.string.eventsTextSingle)
                            }
                            "Announcements" -> {
                                databaseManager.deleteAnnouncementFromFirestore(tableEntryDocumentIndex)
                                deletionType = context.getString(R.string.announcementText)
                            }
                            "Users" -> {

                                if (userID != "")
                                {
                                    databaseManager.deleteUserFromFirestore(tableEntryDocumentIndex, userID)
                                    deletionType = context.getString(R.string.userText)
                                }

                            }
                        }
                        //databaseManager.deleteUserFromFirestore(tableEntryDocumentIndex)
                    }

                    Toast.makeText(context.requireActivity(), "${context.getString(R.string.deletedText)} $deletionType", Toast.LENGTH_SHORT).show()
                    GlobalClass.RefreshFragment(context)
                }

            }
            .setNegativeButton(context.getString(R.string.noText)) { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun ExpandEntryData(tableEntryIndex: Int, editMode: Boolean, table: String, context: Fragment,)
    {
        //create local fragment controller
        val fragmentControl = FragmentManager()
        val args = Bundle()

        when(table)
        {
            "Projects" -> {
                val expandedProjectView = add_project()

                args.putInt("selectedProjectID", tableEntryIndex)
                args.putBoolean("editMode", editMode)

                expandedProjectView.arguments = args

                fragmentControl.replaceFragment(
                    expandedProjectView,
                    R.id.flContent,
                    context.parentFragmentManager
                )
            }
            "Events" -> {

                val expandedEventView = create_event()

                args.putInt("selectedEventID", tableEntryIndex)
                args.putBoolean("editMode", editMode)

                expandedEventView.arguments = args

                fragmentControl.replaceFragment(
                    expandedEventView,
                    R.id.flContent,
                    context.parentFragmentManager
                )

            }
            "Announcements" -> {
                val expandedAnnouncementView = create_announcement()

                args.putInt("selectedAnnouncementID", tableEntryIndex)
                args.putBoolean("editMode", editMode)

                expandedAnnouncementView.arguments = args

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

    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun showAdminOptionMenu(tableEntry: String, tableEntryIndex: Int, context: Fragment, table: String)
    {
        //new dialog
        val builder = AlertDialog.Builder(context.requireActivity())

        //set the dialog title
        builder.setTitle(R.string.adminMenuTitleText)

        //888888888888888888888888888888888888888888888888888888888888888888888
        var menuArray = context.resources.getStringArray(R.array.adminItemFunctions)
        if (GlobalClass.currentUser.MemberTypeID != 3)
        {
            menuArray = menuArray.filter { it != menuArray[2].toString() }.toTypedArray()
        }
        //888888888888888888888888888888888888888888888888888888888888888888888

        // builder.setItems(R.array.adminItemFunctions) { dialog, selectedItem ->

        //set the source options for the dialog
        builder.setItems(menuArray) { dialog, selectedItem ->

            when (selectedItem)
            {
                0 -> {
                    //view

                    //create local fragment controller
                    val fragmentControl = FragmentManager()
                    val args = Bundle()
                    args.putBoolean("editMode", false)


                    when(table)
                    {

                        "Projects" -> {
                            ExpandEntryData(tableEntryIndex, false, "Projects", context)
                        }
                        "Events" -> {

                            ExpandEntryData(tableEntryIndex, false, "Events", context)

                            /*
                            val viewScreen = add_project() //change to events
                            args.putInt("selectedEventID", tableEntryIndex)
                            viewScreen.arguments = args

                            fragmentControl.replaceFragment(
                                viewScreen,
                                R.id.flContent,
                                context.parentFragmentManager
                            )
                             */
                        }
                        "Announcements" -> {


                            //----------------

                            var selectedAnnouncementIndex = GlobalClass.Announcements.indexOfLast{it.AnnouncementID == tableEntryIndex}


                            var fullNotice =
                                MaterialAlertDialogBuilder(context.requireActivity(), R.style.NoticeAlert)
                                    .setTitle(GlobalClass.Announcements[selectedAnnouncementIndex].AnnouncementTitle)   //needs to be the index od the id
                                    .setMessage(GlobalClass.Announcements[selectedAnnouncementIndex].AnnouncementMessage)
                                    .setIcon((R.drawable.notification_bell))
                                    .setNeutralButton(context.getString(R.string.okText)) { dialog, which ->
                                        // Respond to neutral button press
                                    }

                            fullNotice.show()

                            //----------------

                        }
                    }


                }
                1 -> {
                    //edit
                    //create local fragment controller
                    val fragmentControl = FragmentManager()
                    val args = Bundle()
                    args.putBoolean("editMode", true)


                    when(table)
                    {
                        "Projects" -> {
                            ExpandEntryData(tableEntryIndex, true, "Projects", context)
                        }
                        "Events" -> {
                            /*
                            val viewScreen = add_project() //change to events
                            args.putInt("selectedEventID", tableEntryIndex)
                            viewScreen.arguments = args

                            fragmentControl.replaceFragment(
                                viewScreen,
                                R.id.flContent,
                                context.parentFragmentManager
                            )
                             */

                            ExpandEntryData(tableEntryIndex, true, "Events", context)
                        }
                        "Announcements" -> {
                            
                            ExpandEntryData(tableEntryIndex, true, "Announcements", context)
                        }
                    }
                }
                2 -> {
                    //delete

                    when(table)
                    {

                        "Projects" -> {

                            var selectedProjectIndex = GlobalClass.Projects.indexOfLast { it.ProjectID == tableEntryIndex }

                            if (selectedProjectIndex != -1) {
                                //if the user is in the current project

                                deleteConfirmation(tableEntry, context, "Projects", "", GlobalClass.Projects[selectedProjectIndex].ProjectID)
                            }

                        }
                        "Events" -> {
                            deleteConfirmation(tableEntry, context, "Events", "", 0)
                        }
                        "Announcements" -> {
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