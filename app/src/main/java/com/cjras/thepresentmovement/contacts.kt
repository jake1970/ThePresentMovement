package com.cjras.thepresentmovement

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Space
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cjras.thepresentmovement.databinding.FragmentContactsBinding
import com.google.api.Distribution.BucketOptions.Linear
import kotlinx.coroutines.*


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


        try {
            MainScope().launch{
                if (GlobalClass.UpdateDataBase == true) {

                requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.VISIBLE

                withContext(Dispatchers.Default) {

                    var databaseManager = DatabaseManager()

                    databaseManager.updateFromDatabase()
                }


                }
                    UpdateUI()
            }
        }
        catch (e: Error)
        {
            GlobalClass.InformUser(getString(R.string.errorText), "${e.toString()}", requireContext())
        }


        binding.etSearch.addTextChangedListener { charSequence ->

            loadContacts(charSequence.toString(), binding.llContactsList)
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

        expandedContactView.arguments = args

        fragmentControl.replaceFragment(
            expandedContactView,
            R.id.flContent,
            parentFragmentManager
        )
    }

    fun loadContacts(searchTerm: String, displayLayout: LinearLayout)
    {
        displayLayout.removeAllViews()
        val scrollViewUtils = ScrollViewTools()

        for (user in GlobalClass.Users) {

            if (user != GlobalClass.currentUser) {

                if (user.getFullName().lowercase().contains(searchTerm.lowercase()) || searchTerm == "") {
                    val activityLayout = binding.llContactsList;
                    var newContact = contact_card(activity)

                    newContact.binding.tvContactName.text =
                        user.getFullName()


                    var memberType = MemberTypeDataClass().getSingleMemberType(user.MemberTypeID)
                    newContact.binding.tvContactRole.text = memberType


                    newContact.setOnClickListener()
                    {

                        invokeExpandedContactsView(user.UserID)
                    }
                    //add the new view
                    activityLayout.addView(newContact)

                    //add space between custom cards
                    scrollViewUtils.generateSpacer(activityLayout, requireActivity(), 14)

                }
            }
        }
    }


    fun UpdateUI() {

        try {

            binding.llMyProfileCard.setOnClickListener()
            {
                invokeExpandedContactsView(GlobalClass.currentUser.UserID)
            }


            with(GlobalClass.currentUser)
            {
                binding.tvContactName.text = getFullName()

                binding.tvContactRole.text= GlobalClass.currentUserMemberType

                binding.ivMyProfileImage.setImageBitmap(GlobalClass.currentUserImage)
            }

            loadContacts("",  binding.llContactsList)
            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE

        }
        catch (e: Error)
        {
            GlobalClass.InformUser(getString(R.string.errorText), "${e.toString()}", requireContext())
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}