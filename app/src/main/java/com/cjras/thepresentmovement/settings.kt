package com.cjras.thepresentmovement

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cjras.thepresentmovement.databinding.FragmentRegisterUserBinding
import com.cjras.thepresentmovement.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth

private var _binding: FragmentSettingsBinding? = null
private val binding get() = _binding!!
private lateinit var  firebaseAuth: FirebaseAuth
class settings : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root
        firebaseAuth = FirebaseAuth.getInstance()

        val filterManager = FilterListFunctions()

        binding.llMyProfileCard.setOnClickListener()
        {
            filterManager.invokeExpandedContactsView(GlobalClass.currentUser.UserID, this)
        }
        binding.tvResetPassword.setOnClickListener(){
            firebaseAuth.sendPasswordResetEmail(firebaseAuth.currentUser!!.email.toString())
            Toast.makeText(requireActivity(), "Password reset link sent to: " + firebaseAuth.currentUser!!.email.toString(), Toast.LENGTH_SHORT).show()
        }
        binding.tvCampaignes.setOnClickListener(){
            openBrowser("https://www.thepresentmovement.org/campaigns")
        }
        binding.tvDonate.setOnClickListener(){
            openBrowser("https://www.thepresentmovement.org/projects")
        }
        binding.tvShop.setOnClickListener(){
            openBrowser("https://thepresent.shop/")
        }
        binding.tvInitiatives.setOnClickListener(){
            openBrowser("https://www.thepresentmovement.org/about-us")
        }
        binding.tvLogout.setOnClickListener(){
            // gl Jake, idk what this should do :-)
        }
        return view
    }
    private fun openBrowser(url: String) {
        var url = url
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}