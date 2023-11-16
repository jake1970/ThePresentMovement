package com.cjras.thepresentmovement

import android.R
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cjras.thepresentmovement.databinding.ActivityLoginBinding
import com.cjras.thepresentmovement.databinding.ActivityMainBinding
import com.cjras.thepresentmovement.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.firebase.auth.FirebaseAuth


class login : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private val myPrefsFile = "MyPrefsFile";
    private val myUserID = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //Set view binding
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()

        //Hide the action bar
        supportActionBar?.hide()

        //set status bar color
        window.statusBarColor = ContextCompat.getColor(this, com.cjras.thepresentmovement.R.color.main_grey)


        val etUsername = binding.etUsername
        val etPassword = binding.etPassword

        //When Login button is clicked
        binding.btnSignUp.setOnClickListener()
        {

            // get username and password from edit texts
            val email = etUsername.text.toString()
            val password = etPassword.text.toString()

            //check if username and password are not empty and log in user if the login details are valid, sends user to the home page if login is successful
            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        var intent = Intent(this, home_bottom_navigation::class.java) //ViewActivity
                        startActivity(intent)
                        GlobalClass.currentUser.UserID = firebaseAuth.currentUser?.uid.toString()

                        getSharedPreferences(myPrefsFile, MODE_PRIVATE)
                            .edit()
                            .putString(myUserID, GlobalClass.currentUser.UserID)
                            .commit();

                    } else {
                        Toast.makeText(this, it.exception?.localizedMessage.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please ensure all fields are filled out", Toast.LENGTH_LONG)
                    .show()
            }

        }

    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //When the native android back button/functionality is invoked
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    override fun onBackPressed() {
        // Do nothing
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}