package com.cjras.thepresentmovement

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.cjras.thepresentmovement.databinding.FragmentAdminBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class admin : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        val view = binding.root


        //check that the current user is an admin user
        if (GlobalClass.currentUser.MemberTypeID == 3)
        {
            //show the manage accounts option/button
            binding.tvManageAccounts.visibility = View.VISIBLE
        }



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //Data population
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        try {

            GlobalClass.checkUser(this)

            //Read Data
            MainScope().launch{

                if (GlobalClass.UpdateDataBase == true) {
                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE
                    withContext(Dispatchers.Default) {

                        var databaseManager = DatabaseManager()
                        databaseManager.updateFromDatabase()
                    }
                }
                requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE
            }
        }
        catch (e: Exception)
        {
            GlobalClass.InformUser(getString(R.string.errorText), "${e.toString()}", requireContext())
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //when the manage accounts button is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tvManageAccounts.setOnClickListener()
        {
            //call method to open the manage accounts list fragment
            LaunchAdminContentList(getString(R.string.accountsText))
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //when the manage events button is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tvManageEvents.setOnClickListener()
        {
            //call method to open the manage events list fragment
            LaunchAdminContentList(getString(R.string.eventsText))
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //when the manage projects button is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tvManageProjects.setOnClickListener()
        {
            //call method to open the manage projects list fragment
            LaunchAdminContentList(getString(R.string.projectsText))
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //when the manage announcements button is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tvManageAnnouncements.setOnClickListener()
        {
            //call method to open the manage announcements list fragment
            LaunchAdminContentList(getString(R.string.announcementsText))
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //When the refresh button is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.ivRefresh.setOnClickListener()
        {
            try {
                //call method to refresh the current fragment to pull new information from the database manually
                GlobalClass.RefreshFragment(this@admin)
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




        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //when the contact us button is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tvContactUs.setOnClickListener()
        {
            //intent to direct user
            val intent = Intent(Intent.ACTION_SENDTO)

            //set the intent data to the desired email address
            intent.data = Uri.parse("mailto:colbyvanstaden@gmail.com") //source - https://stackoverflow.com/a/6506999

            //call intent
            startActivity(intent)
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



        // Inflate the layout for this fragment
        return view
    }




    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //method to open the content management screen
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private fun LaunchAdminContentList(screenTitle: String)
    {

        //create local fragment controller
        val fragmentControl = FragmentManager()

        //instance of admin content list fragment
        val adminContentList = admin_content_list()
        val args = Bundle()

        //set the screen function argument to the passed value
        args.putString("selectedFunction", screenTitle)

        adminContentList.arguments = args

        //open the fragment instance
        fragmentControl.replaceFragment(adminContentList, R.id.flContent, parentFragmentManager)

    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


}