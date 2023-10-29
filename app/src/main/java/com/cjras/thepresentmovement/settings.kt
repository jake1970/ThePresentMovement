package com.cjras.thepresentmovement

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import com.cjras.thepresentmovement.databinding.FragmentRegisterUserBinding
import com.cjras.thepresentmovement.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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


        try {
            MainScope().launch {
                if (GlobalClass.UpdateDataBase == true) {

                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE

                    withContext(Dispatchers.Default) {

                        var databaseManager = DatabaseManager()

                        databaseManager.updateFromDatabase()
                    }


                }
                UpdateUI()
            }
        } catch (e: Error) {
            GlobalClass.InformUser(
                getString(R.string.errorText),
                "${e}",
                requireContext()
            )
        }

        binding.ivRefresh.setOnClickListener()
        {
            try {
                GlobalClass.RefreshFragment(this@settings)
            }
            catch (e: Error) {
                GlobalClass.InformUser(
                    getString(R.string.errorText),
                    "${e}",
                    requireContext()
                )
            }
        }

        firebaseAuth = FirebaseAuth.getInstance()

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


        return view
    }
    private fun UpdateUI()
    {

        binding.llMyProfileCard.setOnClickListener()
        {
            val filterManager = FilterListFunctions()
            filterManager.invokeExpandedContactsView(GlobalClass.currentUser.UserID, this)
        }

        binding.tvLogout.setOnClickListener(){
            firebaseAuth.signOut()

            var intent = Intent(requireActivity(), login::class.java) //ViewActivity
            GlobalClass.currentUser = UserDataClass()
            GlobalClass.UpdateDataBase = true
            startActivity(intent)
        }



        binding.tvContactName.text = GlobalClass.currentUser.getFullName()
        binding.tvContactRole.text= GlobalClass.currentUserMemberType
        binding.ivMyProfileImage.setImageBitmap(GlobalClass.currentUserImage)
    }


    private fun openBrowser(url: String) {
        var url = url
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}