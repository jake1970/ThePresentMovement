package com.cjras.thepresentmovement


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.cjras.thepresentmovement.databinding.FragmentContactsBinding
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
            MainScope().launch {
                if (GlobalClass.UpdateDataBase == true) {

                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                        View.VISIBLE

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
                "${e.toString()}",
                requireContext()
            )
        }


        binding.etSearch.addTextChangedListener { charSequence ->

            loadContacts(charSequence.toString(), binding.spnMemberTypes.selectedItem.toString(), binding.llContactsList)
        }

        binding.ivRefresh.setOnClickListener()
        {
            GlobalClass.RefreshFragment(this)
        }

        binding.llExpansionMenu.setOnClickListener()
        {

            val animationManager = AnimationHandler()

            animationManager.rotatingArrowMenu(binding.llExpansionContent, binding.ivExpandArrow)

        }




        // Inflate the layout for this fragment
        return view
    }


    private fun populateMemberTypes() {
        val memberTypeOptions: ArrayList<String> =
            GlobalClass.MemberTypes.distinct().map { it.MemberType }.toCollection(ArrayList())
        memberTypeOptions.add(0, "All")

        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, memberTypeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnMemberTypes.adapter = adapter


        // binding.spnMemberTypes.entre = GlobalClass.MemberTypes.distinct()

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

    fun loadContacts(searchTerm: String, memberTypeFilter: String, displayLayout: LinearLayout)
    {
        displayLayout.removeAllViews()
        val scrollViewUtils = ScrollViewTools()


        for (user in GlobalClass.Users) {

            if (user != GlobalClass.currentUser) {

                if (user.getFullName().lowercase().contains(searchTerm.lowercase()) || searchTerm == "") {


                    val currentMemberTypeString = MemberTypeDataClass().getSingleMemberType(user.MemberTypeID)

                  // Toast.makeText(requireContext(), currentMemberTypeString, Toast.LENGTH_SHORT).show() //88888888888888888888888888888888888888888888888888888

                    if ( currentMemberTypeString == memberTypeFilter || memberTypeFilter == "All") {

                      //  Toast.makeText(requireContext(), currentMemberTypeString+ "1", Toast.LENGTH_SHORT).show() //88888888888888888888888888888888888888888888888888888

                        val activityLayout = binding.llContactsList;
                        var newContact = contact_card(activity)

                        newContact.binding.tvContactName.text =
                            user.getFullName()


                        var memberType =
                            MemberTypeDataClass().getSingleMemberType(user.MemberTypeID)
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
    }


    fun UpdateUI() {

        try {

            populateMemberTypes()

//fix for spinner not showing text in correct color
            binding.spnMemberTypes.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    (view as TextView).setTextColor(Color.BLACK) //Change selected text color


                    val selectedText = binding.spnMemberTypes.selectedItem.toString()
                    if (selectedText == "All")
                    {
                        loadContacts(binding.etSearch.text.toString(), "All", binding.llContactsList)
                    }
                    else
                    {
                        //call method to filter list
                        loadContacts(binding.etSearch.text.toString(), selectedText, binding.llContactsList)
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            })


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

            loadContacts("",  "All", binding.llContactsList)
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