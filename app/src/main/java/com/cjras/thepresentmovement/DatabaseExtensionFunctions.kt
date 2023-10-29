package com.cjras.thepresentmovement

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DatabaseExtensionFunctions {


    fun deleteConfirmation(tableEntryDocumentIndex: String, context: Fragment, table: String)
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
                                databaseManager.deleteProjectFromFirestore(tableEntryDocumentIndex)
                                deletionType = context.getString(R.string.projectsTextSingle)
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
                                databaseManager.deleteUserFromFirestore(tableEntryDocumentIndex)
                                deletionType = context.getString(R.string.userText)
                            }
                        }
                        databaseManager.deleteUserFromFirestore(tableEntryDocumentIndex)
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


    fun showAdminOptionMenu(tableEntry: String, tableEntryIndex: Int, context: Fragment, table: String)
    {
        //new dialog
        val builder = AlertDialog.Builder(context.requireActivity())

        //set the dialog title
        builder.setTitle(R.string.adminMenuTitleText)

        //set the source options for the dialog
        builder.setItems(R.array.adminItemFunctions) { dialog, selectedItem ->

            when (selectedItem)
            {
                0 -> {
                    //view

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
                            val viewScreen = add_project()
                            args.putInt("selectedProjectID", tableEntryIndex)
                            viewScreen.arguments = args

                            fragmentControl.replaceFragment(
                                viewScreen,
                                R.id.flContent,
                                context.parentFragmentManager
                            )
                        }
                        "Events" -> {
                            val viewScreen = add_project() //change to events
                            args.putInt("selectedEventID", tableEntryIndex)
                            viewScreen.arguments = args

                            fragmentControl.replaceFragment(
                                viewScreen,
                                R.id.flContent,
                                context.parentFragmentManager
                            )
                        }
                        "Announcements" -> {
                            val viewScreen = add_project() // change to announcements
                            args.putInt("selectedProjectID", tableEntryIndex)
                            viewScreen.arguments = args

                            fragmentControl.replaceFragment(
                                viewScreen,
                                R.id.flContent,
                                context.parentFragmentManager
                            )
                        }
                    }
                }
                2 -> {
                    //delete

                    when(table)
                    {

                        "Projects" -> {
                            deleteConfirmation(tableEntry, context, "Projects")
                        }
                        "Events" -> {
                            deleteConfirmation(tableEntry, context, "Events")
                        }
                        "Announcements" -> {
                            deleteConfirmation(tableEntry, context, "Announcements")
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



}