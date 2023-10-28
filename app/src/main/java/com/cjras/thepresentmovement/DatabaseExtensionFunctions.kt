package com.cjras.thepresentmovement

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DatabaseExtensionFunctions {

     fun deleteUserConfirmation(userDocumentIndex: String, context: Context)
    {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Confirm Deletion?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->


                MainScope().launch() {
                    withContext(Dispatchers.Default) {
                        var databaseManager = DatabaseManager()
                        databaseManager.deleteUserFromFirestore(userDocumentIndex)
                    }

                    Toast.makeText(context, "Deleted User", Toast.LENGTH_SHORT).show()
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