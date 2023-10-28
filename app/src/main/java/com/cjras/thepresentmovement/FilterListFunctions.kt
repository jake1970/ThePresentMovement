package com.cjras.thepresentmovement

import android.content.Context
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FilterListFunctions {

    private val scrollViewUtils = ScrollViewTools()



    fun LoadAnnouncements(searchTerm: String, displayLayout: LinearLayout, startDate: String, endDate: String, context: FragmentActivity)
    {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")

        displayLayout.removeAllViews()

        for (announcement in GlobalClass.Announcements) {
            if (announcement.AnnouncementTitle.lowercase().contains(searchTerm.lowercase()) || announcement.AnnouncementMessage.lowercase().contains(searchTerm.lowercase()) || searchTerm == "") {

                var startDateFormatted : LocalDate? = null
                var endDateFormatted : LocalDate? = null

                if (startDate != context.getString(R.string.blankDate)) {
                    startDateFormatted = LocalDate.parse(startDate, formatter)
                }

                if (endDate != context.getString(R.string.blankDate)) {
                    endDateFormatted = LocalDate.parse(endDate, formatter)
                }

                if (startDate == context.getString(R.string.blankDate) || (startDateFormatted != null && (announcement.AnnouncementDate.isAfter(startDateFormatted!!)  || announcement.AnnouncementDate.isEqual(startDateFormatted!!)))) {

                    if (endDate == context.getString(R.string.blankDate) || (endDateFormatted != null && (announcement.AnnouncementDate.isBefore(endDateFormatted!!) || announcement.AnnouncementDate.isEqual(endDateFormatted!!)))) {


                        //val activityLayout = binding.llNotices;
                        var newAnnouncement = announcement_card(context)


                        newAnnouncement.binding.tvAnnouncementTime.text =
                            announcement.AnnouncementDate.format(DateTimeFormatter.ofPattern("dd/MM/yy"));
                        newAnnouncement.binding.tvAnnouncementTitle.text =
                            announcement.AnnouncementTitle
                        newAnnouncement.binding.tvAnnouncementText.text =
                            announcement.AnnouncementMessage

                        newAnnouncement.setOnClickListener()
                        {
                            var fullNotice =
                                MaterialAlertDialogBuilder(context, R.style.NoticeAlert)
                                    .setTitle(newAnnouncement.binding.tvAnnouncementTitle.text)
                                    .setMessage(newAnnouncement.binding.tvAnnouncementText.text)
                                    .setIcon((R.drawable.notification_bell))
                                    .setNeutralButton(context.getString(R.string.okText)) { dialog, which ->
                                        // Respond to neutral button press
                                    }

                            fullNotice.show()
                        }

                        //add the new view
                        displayLayout.addView(newAnnouncement)

                        //add space between custom cards
                        scrollViewUtils.generateSpacer(displayLayout, context, 14)

                    }
                }
            }
        }
    }

}