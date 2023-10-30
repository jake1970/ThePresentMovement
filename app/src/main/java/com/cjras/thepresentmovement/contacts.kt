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



        binding.ivRefresh.setOnClickListener()
        {
            try {
                GlobalClass.RefreshFragment(this@contacts)
                }
                catch (e: Exception) {
                    GlobalClass.InformUser(
                        getString(R.string.errorText),
                        "${e}",
                        requireContext()
                    )
                }
        }



        binding.llExpansionMenu.setOnClickListener()
        {

            val animationManager = AnimationHandler()

            animationManager.rotatingArrowMenu(binding.llExpansionContent, binding.ivExpandArrow)

        }



        // Inflate the layout for this fragment
        return view
    }



    fun UpdateUI() {

        try {


            filterManager.populateMemberTypes(binding.spnMemberTypes, requireActivity())


            // Set an OnItemSelectedListener to handle item selection
            binding.spnMemberTypes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                    val selectedText = binding.spnMemberTypes.selectedItem.toString()

                    if (selectedText == "All")
                    {
                        filterManager.loadContacts(binding.etSearch.text.toString(), "All", binding.llContactsList, this@contacts, false)
                    }
                    else
                    {
                        //call method to filter list
                        filterManager.loadContacts(binding.etSearch.text.toString(), selectedText, binding.llContactsList, this@contacts, false)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle the case where nothing is selected
                }
            }



            binding.etSearch.addTextChangedListener { charSequence ->

                filterManager.loadContacts(charSequence.toString(), binding.spnMemberTypes.selectedItem.toString(), binding.llContactsList, this, false)
            }


            binding.llMyProfileCard.setOnClickListener()
            {
                filterManager.invokeExpandedContactsView(GlobalClass.currentUser.UserID, this)
            }


            with(GlobalClass.currentUser)
            {
                binding.tvContactName.text = getFullName()

                binding.tvContactRole.text= GlobalClass.currentUserMemberType

                binding.ivMyProfileImage.setImageBitmap(GlobalClass.currentUserImage)
            }

            filterManager.loadContacts("",  "All", binding.llContactsList, this, false)
            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE

        }
        catch (e: Exception)
        {
            GlobalClass.InformUser(getString(R.string.errorText), "${e.toString()}", requireContext())
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}