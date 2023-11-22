package com.cjras.thepresentmovement

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //View binding
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //Data population
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        try {

            GlobalClass.checkUser(this)

            MainScope().launch {

                //if new information has been added pull new data from the database
                if (GlobalClass.UpdateDataBase == true) {

                    //show loading screen
                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE

                    withContext(Dispatchers.Default) {

                        var databaseManager = DatabaseManager()

                        //call method to retrieve all data from the database
                        databaseManager.updateFromDatabase()
                    }

                }

                //call method to update the ui when the new database information has been loaded (if required)
                UpdateUI()
            }
        } catch (e: Exception) {
            //call method to show the error
            GlobalClass.InformUser(
                getString(R.string.errorText),
                "$e",
                requireContext()
            )
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //When the refresh button is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.ivRefresh.setOnClickListener()
        {
            try {
                //call method to refresh the current fragment to pull new information from the database manually
                GlobalClass.RefreshFragment(this@settings)
            }
            catch (e: Exception) {
                //call method to show the error
                GlobalClass.InformUser(
                    getString(R.string.errorText),
                    "$e",
                    requireContext()
                )
            }
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


        firebaseAuth = FirebaseAuth.getInstance()

        binding.tvResetPassword.setOnClickListener(){
            firebaseAuth.sendPasswordResetEmail(firebaseAuth.currentUser!!.email.toString())
            Toast.makeText(requireActivity(), getString(R.string.emailResetSent) + firebaseAuth.currentUser!!.email.toString(), Toast.LENGTH_SHORT).show()
        }
        binding.tvCampaignes.setOnClickListener(){
            GlobalClass.openBrowser("https://www.thepresentmovement.org/campaigns", requireActivity())
        }
        binding.tvDonate.setOnClickListener(){
            GlobalClass.openBrowser("https://www.thepresentmovement.org/donate", requireActivity())
        }
        binding.tvShop.setOnClickListener(){
            GlobalClass.openBrowser("https://thepresent.shop/", requireActivity())
        }
        binding.tvInitiatives.setOnClickListener(){
            GlobalClass.openBrowser("https://www.thepresentmovement.org/initiatives", requireActivity())
        }


        return view
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to update the screen data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private fun UpdateUI()
    {

        try {

            //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
            //When the profile card is clicked
            //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
            binding.llMyProfileCard.setOnClickListener()
            {
                val filterManager = FilterListFunctions()

                //call method to take the user to an expanded contacts view to see their profile information
                filterManager.invokeExpandedContactsView(GlobalClass.currentUser.UserID, this)
            }
            //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------



            //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
            //When the logout button is clicked
            //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
            binding.tvLogout.setOnClickListener()
            {
                //call logout method
                GlobalClass.logout(requireActivity())
            }
            //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------


            //set the users profile information for the contact card
            binding.tvContactName.text = GlobalClass.currentUser.getFullName()
            binding.tvContactRole.text= GlobalClass.currentUserMemberType
            binding.ivMyProfileImage.setImageBitmap(GlobalClass.currentUserImage)

            //hide the loading screen
            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE

        }
        catch (e: Exception)
        {
            //call method to show the error
            GlobalClass.InformUser(getString(R.string.errorText), "$e", requireContext())
        }
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


}