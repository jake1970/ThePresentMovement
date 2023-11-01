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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //Set view binding
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        //Hide the action bar
        supportActionBar?.hide()

        //set status bar color
        window.statusBarColor = ContextCompat.getColor(this, com.cjras.thepresentmovement.R.color.main_grey)

        val etUsername = binding.etUsername
        val etPassword = binding.etPassword



        //---------------------------------------------------------------------------------------------------------------------------------------------------------

        binding.btnSignUp.setOnClickListener()
        {

            //me
            //GlobalClass.currentUser.UserID = "PMYdocWRj7R2GS4REUQq"

            //robz
            //GlobalClass.currentUser.UserID = "dgYsVK3ezlrXpwZLmQiq"


            val email = etUsername.text.toString()
            val password = etPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        var intent = Intent(this, home_bottom_navigation::class.java) //ViewActivity
                        startActivity(intent)
                        GlobalClass.currentUser.UserID = firebaseAuth.currentUser?.uid.toString()
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

    override fun onBackPressed() {
        // Do nothing
    }
}