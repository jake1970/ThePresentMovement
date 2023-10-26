package com.cjras.thepresentmovement


import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.cjras.thepresentmovement.databinding.FragmentNoticesBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class notices : Fragment() {

    private var _binding: FragmentNoticesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNoticesBinding.inflate(inflater, container, false)
        val view = binding.root

        try {

            //Read Data
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

        binding.tvStartDate.setOnClickListener(){
            datePicker(true, binding.tvStartDate)

        }

        binding.tvEndDate.setOnClickListener(){
            datePicker(false, binding.tvEndDate)
        }

        binding.tvStartDate.doAfterTextChanged { char ->
            LoadAnnouncements(binding.etSearch.text.toString(), binding.llNotices, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString())
        }

        binding.tvEndDate.doAfterTextChanged { char ->
            LoadAnnouncements(binding.etSearch.text.toString(), binding.llNotices, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString())
        }


        binding.ivRefresh.setOnClickListener()
        {
            GlobalClass.RefreshFragment(this)
        }


        binding.etSearch.addTextChangedListener { charSequence ->

            LoadAnnouncements(charSequence.toString(), binding.llNotices, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString())
        }

        return view
    }

    private fun LoadAnnouncements(searchTerm: String, displayLayout: LinearLayout, startDate: String, endDate: String)
    {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")

        displayLayout.removeAllViews()
        val scrollViewUtils = ScrollViewTools()

        for (announcement in GlobalClass.Announcements) {
            if (announcement.AnnouncementTitle.lowercase().contains(searchTerm.lowercase()) || announcement.AnnouncementMessage.lowercase().contains(searchTerm.lowercase()) || searchTerm == "") {

                var startDateFormatted : LocalDate? = null
                var endDateFormatted : LocalDate? = null

               if (startDate != getString(R.string.blankDate)) {
                    startDateFormatted = LocalDate.parse(startDate, formatter)
               }

                if (endDate != getString(R.string.blankDate)) {
                    endDateFormatted = LocalDate.parse(endDate, formatter)
                }

                if (startDate == getString(R.string.blankDate) || (startDateFormatted != null && (announcement.AnnouncementDate.isAfter(startDateFormatted!!)  || announcement.AnnouncementDate.isEqual(startDateFormatted!!)))) {

                    if (endDate == getString(R.string.blankDate) || (endDateFormatted != null && (announcement.AnnouncementDate.isBefore(endDateFormatted!!) || announcement.AnnouncementDate.isEqual(endDateFormatted!!)))) {


                        val activityLayout = binding.llNotices;
                        var newAnnouncement = announcement_card(activity)


                        newAnnouncement.binding.tvAnnouncementTime.text =
                            announcement.AnnouncementDate.format(DateTimeFormatter.ofPattern("dd/MM/yy"));
                        newAnnouncement.binding.tvAnnouncementTitle.text =
                            announcement.AnnouncementTitle
                        newAnnouncement.binding.tvAnnouncementText.text =
                            announcement.AnnouncementMessage

                        newAnnouncement.setOnClickListener()
                        {
                            var fullNotice =
                                MaterialAlertDialogBuilder(requireContext(), R.style.NoticeAlert)
                                    .setTitle(newAnnouncement.binding.tvAnnouncementTitle.text)
                                    .setMessage(newAnnouncement.binding.tvAnnouncementText.text)
                                    .setIcon((R.drawable.notification_bell))
                                    .setNeutralButton(resources.getString(R.string.okText)) { dialog, which ->
                                        // Respond to neutral button press
                                    }

                            fullNotice.show()
                        }

                        //add the new view
                        activityLayout.addView(newAnnouncement)

                        //add space between custom cards
                        scrollViewUtils.generateSpacer(activityLayout, requireActivity(), 14)

                    }
                }
            }
        }
    }

    private fun UpdateUI()
    {


        try {


            LoadAnnouncements("", binding.llNotices, binding.tvStartDate.text.toString(), binding.tvEndDate.text.toString())
            requireActivity().findViewById<RelativeLayout>(R.id.rlLoadingCover).visibility = View.GONE

        }
        catch (e: Exception)
        {
            GlobalClass.InformUser(getString(R.string.errorText), "${e.toString()}", requireContext())
        }



    }

    private fun datePicker(startPrompt: Boolean, entryField: TextView)
    {

        var entryPrompt =getString(R.string.dateEndPrompt)

        if (startPrompt)
        {
            entryPrompt = getString(R.string.dateStartPrompt)
        }

        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText(entryPrompt)
        val picker = builder.build()
        picker.show(childFragmentManager, picker.toString())

        picker.addOnPositiveButtonClickListener {
            // Respond to positive button click.
            val selectedDate = SimpleDateFormat("dd/MM/yy")
            entryField.text = selectedDate.format(picker.selection)
        }

        picker.addOnNegativeButtonClickListener {
            // Respond to negative button click.
            entryField.text = getString(R.string.blankDate)
        }

        picker.addOnCancelListener {
            // Respond to cancel button click.
            entryField.text = getString(R.string.blankDate)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}