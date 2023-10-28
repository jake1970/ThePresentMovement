package com.cjras.thepresentmovement

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DatabaseExtensionFunctions {

    fun deleteUserConfirmation(userDocumentIndex: String, context: Fragment)
    {
        val builder = AlertDialog.Builder(context.requireActivity())
        builder.setMessage("Confirm Deletion?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->


                MainScope().launch() {
                    withContext(Dispatchers.Default) {
                        var databaseManager = DatabaseManager()
                        databaseManager.deleteUserFromFirestore(userDocumentIndex)
                    }

                    GlobalClass.RefreshFragment(context)
                    Toast.makeText(context.requireActivity(), "Deleted User", Toast.LENGTH_SHORT).show()
                }

            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    fun deleteProjectConfirmation(projectDocumentIndex: String, context: Fragment)
    {
        val builder = AlertDialog.Builder(context.requireActivity())
        builder.setMessage("Confirm Deletion?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->


                MainScope().launch() {
                    withContext(Dispatchers.Default) {
                        var databaseManager = DatabaseManager()
                        databaseManager.deleteProjectFromFirestore(projectDocumentIndex)

                        //val firebaseAuth = FirebaseAuth.getInstance()
                       // firebaseAuth
                    }

                    GlobalClass.RefreshFragment(context)
                    Toast.makeText(context.requireActivity(), "Deleted Project", Toast.LENGTH_SHORT).show()
                }

            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

}