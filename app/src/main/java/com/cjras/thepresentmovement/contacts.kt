package com.cjras.thepresentmovement

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Space
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cjras.thepresentmovement.databinding.FragmentContactsBinding
import kotlinx.coroutines.*


/**
 * A simple [Fragment] subclass.
 * Use the [contacts.newInstance] factory method to
 * create an instance of this fragment.
 */
class contacts : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        val view = binding.root

        //---------------------------------------------------------------------------------------------------------------------------------------------------------
        //initial data population
        //---------------------------------------------------------------------------------------------------------------------------------------------------------

       // var loadingCover = GlobalClass.addLoadingCover(layoutInflater, view)


        try {

            //loadingCover.visibility = View.GONE
            //requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE

            //Read Data
            //GlobalScope.launch{
            //val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
            MainScope().launch{
                if (GlobalClass.UpdateDataBase == true) {
                requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE
                withContext(Dispatchers.Default) {

                    // Toast.makeText(activity, "loading", Toast.LENGTH_SHORT).show()

                    //loadingCover.visibility = View.VISIBLE
                    //requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE

                    var databaseManager = DatabaseManager()

                    databaseManager.updateFromDatabase()
                }
                    //GlobalClass.MemberTypes = databaseManager.getAllMemberTypesFromFirestore()
                    //GlobalClass.Users = databaseManager.getAllUsersFromFirestore()
                    //GlobalClass.UpdateDataBase = false

                }
               // withContext(Dispatchers.Main) {
                    UpdateUI()
               // }
            }
        }
        catch (e: Error)
        {
            GlobalClass.InformUser(getString(R.string.errorText), "${e.toString()}", requireContext())
        }


        binding.etSearch.addTextChangedListener { charSequence ->

            binding.llContactsList.removeAllViews()

            loadContacts(charSequence.toString())
        }

        binding.ivRefresh.setOnClickListener()
        {
            GlobalClass.RefreshFragment(this)
        }

        // Inflate the layout for this fragment
        return view
    }

    private fun invokeExpandedContactsView(userID : String)
    {
        //create local fragment controller
        val fragmentControl = FragmentManager()

        val expandedContactView = expanded_contact()
        val args = Bundle()

        args.putString("selectedUserID", userID)
        /*
args.putBoolean("myProfile", false)
args.putString("firstName", user.FirstName)
args.putString("lastName", user.LastName)
args.putString("emailAddress", user.EmailAddress)
args.putString("memberType", userType)
args.putString("quote", user.Quote)
args.putString("contactNumber", user.ContactNumber)
args.putString("companyName", user.CompanyName)
args.putString("linkedIn", user.LinkedIn)
args.putString("website", user.Website)
args.putString("userImageURI", user.UserImageURI)

 */

        expandedContactView.arguments = args

        fragmentControl.replaceFragment(
            expandedContactView,
            R.id.flContent,
            parentFragmentManager
        )
    }

    fun loadContacts(searchTerm: String)
    {
        for (user in GlobalClass.Users) {

            if (user != GlobalClass.currentUser) {

                if (user.getFullName().lowercase().contains(searchTerm.lowercase()) || searchTerm == "") {
                    val activityLayout = binding.llContactsList;
                    var newContact = contact_card(activity)

                    newContact.binding.tvContactName.text =
                        user.getFullName() //"${user.FirstName} ${user.LastName}"


                    var memberType = MemberTypeDataClass().getSingleMemberType(user.MemberTypeID)
                    newContact.binding.tvContactRole.text = memberType


                    newContact.setOnClickListener()
                    {

                        invokeExpandedContactsView(user.UserID)
                    }
                    //add the new view
                    activityLayout.addView(newContact)


                    val scale = requireActivity().resources.displayMetrics.density
                    val pixels = (14 * scale + 0.5f)

                    val spacer = Space(activity)
                    spacer.minimumHeight = pixels.toInt()
                    activityLayout.addView(spacer)

                }
            }
        }
    }


    fun UpdateUI(/*loadingCover : ViewGroup*/) {

        try {

            //loadingCover.visibility = View.GONE
            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE

            binding.llMyProfileCard.setOnClickListener()
            {
                invokeExpandedContactsView(GlobalClass.currentUser.UserID)
            }

            //**********************************************************************************************************************
            with(GlobalClass.currentUser)
            {
                binding.tvContactName.text = getFullName()

//                var userType = getString(R.string.memberText)
//
//                if (MemberTypeID == 2) {
//                    userType = getString(R.string.seniorMemberText)
//                }
//                binding.tvContactRole.text= userType
                binding.tvContactRole.text= GlobalClass.currentUserMemberType

                /*
                val storageReference = FirebaseStorage.getInstance().reference.child("ContactImages/${UserID}")

                if (HasImage) {
                    val imgFile = File.createTempFile("temptImage", "jpg")
                    storageReference.getFile(imgFile)
                        .addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                "Image Retrieved From Cloud Firestore",
                                Toast.LENGTH_SHORT
                            )

                            val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                            binding.ivMyProfileImage.setImageBitmap(bitmap)

                            //remove loading screen
                            binding.ivLoadingLogo.visibility = View.GONE
                            binding.pbLoadingBar.visibility = View.GONE

                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                "Image Failed To Be Retrieved From Cloud Firestore",
                                Toast.LENGTH_SHORT
                            )
                        }

                }

                 */

                binding.ivMyProfileImage.setImageBitmap(GlobalClass.currentUserImage)
            }
            //**********************************************************************************************************************


            //remove loading screen
            /*
            binding.ivLoadingLogo.visibility = View.GONE
            binding.pbLoadingBar.visibility = View.GONE
             */
            binding.llContactsList.removeAllViews()
        loadContacts("")
/*
                for (user in GlobalClass.Users) {

                    if (user != GlobalClass.currentUser) {

                        val activityLayout = binding.llContactsList;
                        var newContact = contact_card(activity)

                        newContact.binding.tvContactName.text =
                            user.getFullName() //"${user.FirstName} ${user.LastName}"


                        var memberType = MemberTypeDataClass().getSingleMemberType(user.MemberTypeID)
                        newContact.binding.tvContactRole.text = memberType
                        /*
                        var memberType = ""
                        newContact.binding.tvContactRole.text = ""

                        GlobalScope.launch {
                            var databaseManager = DatabaseManager()
                            memberType = databaseManager.getSingleMemberType(user.MemberTypeID)

                            withContext(Dispatchers.Main) {
                                newContact.binding.tvContactRole.text = memberType
                            }
                        }
                                                 */


//                    var userType = getString(R.string.memberText)
//                    if (user.MemberTypeID == 2) {
//                        userType = getString(R.string.seniorMemberText)
//                    }
//                    newContact.binding.tvContactRole.text = userType
//


                        newContact.setOnClickListener()
                        {
                            /*
                        //create local fragment controller
                        val fragmentControl = FragmentManager()

                        val expandedContactView = expanded_contact()
                        val args = Bundle()


                        args.putString("selectedUserID", user.UserID)
                        /*
                args.putBoolean("myProfile", false)
                args.putString("firstName", user.FirstName)
                args.putString("lastName", user.LastName)
                args.putString("emailAddress", user.EmailAddress)
                args.putString("memberType", userType)
                args.putString("quote", user.Quote)
                args.putString("contactNumber", user.ContactNumber)
                args.putString("companyName", user.CompanyName)
                args.putString("linkedIn", user.LinkedIn)
                args.putString("website", user.Website)
                args.putString("userImageURI", user.UserImageURI)

                 */

                        expandedContactView.arguments = args

                        fragmentControl.replaceFragment(
                            expandedContactView,
                            R.id.flContent,
                            parentFragmentManager
                        )

                         */

                            invokeExpandedContactsView(user.UserID)
                        }
                        //add the new view
                        activityLayout.addView(newContact)


                        val scale = requireActivity().resources.displayMetrics.density
                        val pixels = (14 * scale + 0.5f)

                        val spacer = Space(activity)
                        spacer.minimumHeight = pixels.toInt()
                        activityLayout.addView(spacer)

                    }
                }


 */

        }
        catch (e: Error)
        {
            GlobalClass.InformUser(getString(R.string.errorText), "${e.toString()}", requireContext())
        }




        /*
        //---------------------------------------------------------------------------------------------------------------------------------------------------------

        //---------------------------------------------------------------------------------------------
        //test code for custom components
        //---------------------------------------------------------------------------------------------
        for (i in 1..8) {

            val activityLayout = binding.llContactsList;
            var newContact = contact_card(activity)

            newContact.binding.tvContactName.text = "Jake Young"
            newContact.binding.tvContactRole.text = "Senior Member"

            newContact.setOnClickListener()
            {
                //create local fragment controller
                val fragmentControl = FragmentManager()

                val expandedContactView = expanded_contact()
                val args = Bundle()

                args.putBoolean("myProfile", false)
                args.putString("contactFullName", "083 444 5566")
                args.putString("contactRole", "083 444 5566")
                args.putString("contactQuote", "083 444 5566")
                args.putString("contactPhoneNumber", "083 444 5566")
                args.putString("contactEmailAddress", "083 444 5566")
                args.putString("contactCompanyName", "083 444 5566")
                args.putString("contactLinkedIn", "083 444 5566")
                args.putString("contact", "083 444 5566")

                expandedContactView.arguments = args

                fragmentControl.replaceFragment(expandedContactView, R.id.flContent, parentFragmentManager)

                //fragmentControl.replaceFragment(expanded_contact(), R.id.flContent, parentFragmentManager)
            }


            //add the new view
            activityLayout.addView(newContact)


            val scale = requireActivity().resources.displayMetrics.density
            val pixels = (14 * scale + 0.5f)

            val spacer = Space(activity)
            spacer.minimumHeight = pixels.toInt()
            activityLayout.addView(spacer)

        }
        //---------------------------------------------------------------------------------------------


         */

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}