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

    private val filterManager = FilterListFunctions()


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
            GlobalClass.checkUser(this)

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
        } catch (e: Exception) {
            GlobalClass.InformUser(
                getString(R.string.errorText),
                "${e}",
                requireContext()
            )
        }
        //---------------------------------------------------------------------------------------------------------------------------------------------------------



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //When the refresh button is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.ivRefresh.setOnClickListener()
        {
            try {
                //call method to refresh the current fragment to pull new information from the database manually
                GlobalClass.RefreshFragment(this@contacts)
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
        //when the dropdown filter arrow is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.llExpansionMenu.setOnClickListener()
        {

            //animation handler instance
            val animationManager = AnimationHandler()

            //animate the opening/closing of the filter menu
            animationManager.rotatingArrowMenu(binding.llExpansionContent, binding.ivExpandArrow)
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



        // Inflate the layout for this fragment
        return view
    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to update the screen data
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun UpdateUI() {

        try {

            //call method to set the user types in the user type filter dropdown spinner
            filterManager.populateMemberTypes(binding.spnMemberTypes, requireActivity(), true)


            // Set an OnItemSelectedListener to handle item selection
            binding.spnMemberTypes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                    //the selected member filtering option
                    val selectedText = binding.spnMemberTypes.selectedItem.toString()

                    //if selective user filtering must be applied
                    if (selectedText == "All")
                    {
                        //call method to load the list of contacts, ignoring the member type filter
                        filterManager.loadContacts(binding.etSearch.text.toString(), "All", binding.llContactsList, this@contacts, false)
                    }
                    else
                    {
                        //call method to load the list of contacts, including the member type filter
                        filterManager.loadContacts(binding.etSearch.text.toString(), selectedText, binding.llContactsList, this@contacts, false)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle the case where nothing is selected
                }
            }


            //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
            //when the search filter text is changed
            //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
            binding.etSearch.addTextChangedListener { charSequence ->

                //call method to filter the list of contacts according to the search bar text
                filterManager.loadContacts(charSequence.toString(), binding.spnMemberTypes.selectedItem.toString(), binding.llContactsList, this, false)
            }
            //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



            //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
            //when the users own profile card is clicked
            //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
            binding.llMyProfileCard.setOnClickListener()
            {
                //call method to load the expanded contacts view with the current users data
                filterManager.invokeExpandedContactsView(GlobalClass.currentUser.UserID, this)
            }
            //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

            //set current users own profile card with the current users data
            with(GlobalClass.currentUser)
            {
                binding.tvContactName.text = getFullName()

                binding.tvContactRole.text= GlobalClass.currentUserMemberType

                binding.ivMyProfileImage.setImageBitmap(GlobalClass.currentUserImage)
            }

            //call method to load the unfiltered initial list of contacts
            filterManager.loadContacts("",  "All", binding.llContactsList, this, false)

            //hide the loading screen
            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE

        }
        catch (e: Exception)
        {
            GlobalClass.InformUser(getString(R.string.errorText), "${e.toString()}", requireContext())
        }

    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}