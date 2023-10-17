package com.cjras.thepresentmovement

import android.R
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cjras.thepresentmovement.databinding.ActivityLoginBinding
import com.cjras.thepresentmovement.databinding.ActivityMainBinding
import com.cjras.thepresentmovement.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class login : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //Set view binding
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Hide the action bar
        supportActionBar?.hide()

        //set status bar color
        window.statusBarColor = ContextCompat.getColor(this, com.cjras.thepresentmovement.R.color.main_grey)



        //---------------------------------------------------------------------------------------------------------------------------------------------------------

        binding.btnSignUp.setOnClickListener()
        {

            //me
            //GlobalClass.currentUser.UserID = "PMYdocWRj7R2GS4REUQq"

            //robz
            GlobalClass.currentUser.UserID = "dgYsVK3ezlrXpwZLmQiq"



            var intent = Intent(this, home_bottom_navigation::class.java) //ViewActivity
            startActivity(intent)


        }

    }
}