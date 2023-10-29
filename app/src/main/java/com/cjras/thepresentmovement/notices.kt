package com.cjras.thepresentmovement


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.cjras.thepresentmovement.databinding.FragmentNoticesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class notices : Fragment() {

    private var _binding: FragmentNoticesBinding? = null
    private val binding get() = _binding!!

    private val scrollViewUtils = ScrollViewTools()
    private val filterManager = FilterListFunctions()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //View Binding
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        _binding = FragmentNoticesBinding.inflate(inflater, container, false)
        val view = binding.root
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //Data population
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        try {

            //Read Data
            MainScope().launch {

                //if new information has been added pull new data from the database
                if (GlobalClass.UpdateDataBase == true) {

                    //show the loading screen
                    requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                        View.VISIBLE

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
        //When the start date filter is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tvStartDate.setOnClickListener() {

            //call method to show the date picker
            scrollViewUtils.datePicker(this, true, binding.tvStartDate)
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //When the end date filter is clicked
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tvEndDate.setOnClickListener() {

            //call method to show the date picker
            scrollViewUtils.datePicker(this, false, binding.tvEndDate)
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //When the start date filter value changes
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tvStartDate.doAfterTextChanged { char ->

            //call method to load the notices in accordance with the start date filter
            filterManager.LoadAnnouncements(
                binding.etSearch.text.toString(),
                binding.llNotices,
                binding.tvStartDate.text.toString(),
                binding.tvEndDate.text.toString(),
                this@notices,
                false
            )
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //When the end date filter value changes
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.tvEndDate.doAfterTextChanged { char ->

            //call method to load the notices in accordance with the start date filter
            filterManager.LoadAnnouncements(
                binding.etSearch.text.toString(),
                binding.llNotices,
                binding.tvStartDate.text.toString(),
                binding.tvEndDate.text.toString(),
                this@notices,
                false
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
                GlobalClass.RefreshFragment(this@notices)
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
        //When the search filter value changes
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        binding.etSearch.addTextChangedListener { charSequence ->

            //call method to load the notices in accordance with the search filter
            filterManager.LoadAnnouncements(
                charSequence.toString(),
                binding.llNotices,
                binding.tvStartDate.text.toString(),
                binding.tvEndDate.text.toString(),
                this@notices,
                false
            )
        }
        //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        return view
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to update the screen data
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private fun UpdateUI() {


        try {

            //call method to load the initial unfiltered list of notices
            filterManager.LoadAnnouncements(
                "",
                binding.llNotices,
                binding.tvStartDate.text.toString(),
                binding.tvEndDate.text.toString(),
                this@notices,
                false
            )

            //hide the loading screen
            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility =
                View.GONE

        } catch (e: Exception) {
            //call method to show the error
            GlobalClass.InformUser(
                getString(R.string.errorText),
                "${e.toString()}",
                requireContext()
            )
        }


    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}