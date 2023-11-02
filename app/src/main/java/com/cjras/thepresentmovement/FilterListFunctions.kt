package com.cjras.thepresentmovement

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FilterListFunctions {

    private val scrollViewUtils = ScrollViewTools()

    fun populateMemberTypes(spinner: Spinner, context: Context, includeAll: Boolean) {
        val memberTypeOptions: ArrayList<String> =
            GlobalClass.MemberTypes.distinct().map { it.MemberType }.toCollection(ArrayList())

        if (includeAll == true) {
            memberTypeOptions.add(0, "All")
        }

        //val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, memberTypeOptions)
        val adapter = ArrayAdapter(context, R.layout.spinner_layout, memberTypeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

    }

    fun invokeExpandedContactsView(userID: String, context: Fragment) {
        //create local fragment controller
        val fragmentControl = FragmentManager()

        val expandedContactView = expanded_contact()
        val args = Bundle()

        args.putString("selectedUserID", userID)

        expandedContactView.arguments = args

        fragmentControl.replaceFragment(
            expandedContactView,
            R.id.flContent,
            context.parentFragmentManager
        )
    }

    fun loadContacts(
        searchTerm: String,
        memberTypeFilter: String,
        displayLayout: LinearLayout,
        context: Fragment,
        adminView: Boolean
    ) {
        displayLayout.removeAllViews()
        val scrollViewUtils = ScrollViewTools()


        for (user in GlobalClass.Users) {

            if (user != GlobalClass.currentUser) {

                if (user.getFullName().lowercase()
                        .contains(searchTerm.lowercase()) || searchTerm == ""
                ) {


                    val currentMemberTypeString =
                        MemberTypeDataClass().getSingleMemberType(user.MemberTypeID)



                    if (currentMemberTypeString == memberTypeFilter || memberTypeFilter == "All") {


                        var newContact = contact_card(context.requireActivity())

                        newContact.binding.tvContactName.text =
                            user.getFullName()


                        var memberType =
                            MemberTypeDataClass().getSingleMemberType(user.MemberTypeID)
                        newContact.binding.tvContactRole.text = memberType


                        newContact.setOnClickListener()
                        {

                            if (adminView == false) {
                                invokeExpandedContactsView(user.UserID, context)
                            } else {

                                //new dialog
                                val builder = AlertDialog.Builder(context.requireActivity())

                                //set the dialog title
                                builder.setTitle(R.string.adminMenuTitleText)

                                var menuArray =
                                    context.resources.getStringArray(R.array.adminItemFunctions)
                                menuArray = menuArray.filter { it != menuArray[1].toString() }
                                    .toTypedArray()

                                //set the source options for the dialog
                                builder.setItems(menuArray) { dialog, selectedItem ->

                                    when (selectedItem) {
                                        0 -> {
                                            //view
                                            invokeExpandedContactsView(user.UserID, context)
                                        }
                                        1 -> {
                                            //delete

                                            val currentUserIndex = GlobalClass.Users.indexOf(user)
                                            val currentUserDocumentIndex =
                                                GlobalClass.documents.allUserIDs[currentUserIndex]

                                            var databaseExtension = DatabaseExtensionFunctions()
                                            databaseExtension.deleteConfirmation(
                                                currentUserDocumentIndex,
                                                context,
                                                "Users",
                                                user.UserID,
                                                0
                                            )

                                        }

                                    }

                                }

                                //show the dialog
                                builder.show()

                            }


                        }
                        //add the new view
                        displayLayout.addView(newContact)

                        //add space between custom cards
                        scrollViewUtils.generateSpacer(displayLayout, context.requireActivity(), 14)

                    }
                }
            }
        }
    }


    fun LoadAnnouncements(
        searchTerm: String,
        displayLayout: LinearLayout,
        startDate: String,
        endDate: String,
        context: Fragment,
        adminView: Boolean
    ) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")

        displayLayout.removeAllViews()

        for (announcement in GlobalClass.Announcements) {
            if (announcement.AnnouncementTitle.lowercase()
                    .contains(searchTerm.lowercase()) || announcement.AnnouncementMessage.lowercase()
                    .contains(searchTerm.lowercase()) || searchTerm == ""
            ) {

                var startDateFormatted: LocalDate? = null
                var endDateFormatted: LocalDate? = null

                if (startDate != context.getString(R.string.blankDate)) {
                    startDateFormatted = LocalDate.parse(startDate, formatter)
                }

                if (endDate != context.getString(R.string.blankDate)) {
                    endDateFormatted = LocalDate.parse(endDate, formatter)
                }

                if (startDate == context.getString(R.string.blankDate) || (startDateFormatted != null && (announcement.AnnouncementDate.isAfter(
                        startDateFormatted!!
                    ) || announcement.AnnouncementDate.isEqual(startDateFormatted!!)))
                ) {

                    if (endDate == context.getString(R.string.blankDate) || (endDateFormatted != null && (announcement.AnnouncementDate.isBefore(
                            endDateFormatted!!
                        ) || announcement.AnnouncementDate.isEqual(endDateFormatted!!)))
                    ) {


                        //val activityLayout = binding.llNotices;
                        var newAnnouncement = announcement_card(context.requireActivity())


                        newAnnouncement.binding.tvAnnouncementTime.text =
                            announcement.AnnouncementDate.format(DateTimeFormatter.ofPattern("dd/MM/yy"));
                        newAnnouncement.binding.tvAnnouncementTitle.text =
                            announcement.AnnouncementTitle
                        newAnnouncement.binding.tvAnnouncementText.text =
                            announcement.AnnouncementMessage

                        newAnnouncement.setOnClickListener()
                        {

                            if (adminView == false) {
                                var fullNotice =
                                    MaterialAlertDialogBuilder(context.requireActivity(), R.style.NoticeAlert)
                                        .setTitle(newAnnouncement.binding.tvAnnouncementTitle.text)
                                        .setMessage(newAnnouncement.binding.tvAnnouncementText.text)
                                        .setIcon((R.drawable.notification_bell))
                                        .setNeutralButton(context.getString(R.string.okText)) { dialog, which ->
                                            // Respond to neutral button press
                                        }

                                fullNotice.show()
                            } else {

                                val currentAnnouncementIndex = GlobalClass.Announcements.indexOf(announcement)
                                val currentAnnouncementDocumentIndex = GlobalClass.documents.allAnnouncmentIds[currentAnnouncementIndex]

                                var databaseExtension = DatabaseExtensionFunctions()
                                databaseExtension.showAdminOptionMenu(
                                    currentAnnouncementDocumentIndex,
                                    announcement.AnnouncementID,
                                    context,
                                    "Announcements"
                                )

                                /*
                                 val currentProjectIndex = GlobalClass.Projects.indexOf(project)
                                val currentProjectDocumentIndex = GlobalClass.documents.allProjectIds[currentProjectIndex]

                                databaseExtension.showAdminOptionMenu(
                                    currentProjectDocumentIndex,
                                    project.ProjectID,
                                    context,
                                    "Projects"
                                )
                                 */
                            }
                        }

                        //add the new view
                        displayLayout.addView(newAnnouncement)

                        //add space between custom cards
                        scrollViewUtils.generateSpacer(displayLayout, context.requireActivity(), 14)

                    }
                }
            }
        }
    }



    fun LoadProjects(
        searchTerm: String,
        displayLayout: LinearLayout,
        startDate: String,
        endDate: String,
        context: Fragment,
        adminView: Boolean
    ) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
        var databaseManager = DatabaseManager()
        // val scrollViewUtils = ScrollViewTools()


        displayLayout.removeAllViews()


        for (project in GlobalClass.Projects) {
            if (project.ProjectTitle.lowercase()
                    .contains(searchTerm.lowercase()) || project.ProjectCompanyName.lowercase()
                    .contains(searchTerm.lowercase()) || searchTerm == ""
            ) {

                var startDateFormatted: LocalDate? = null
                var endDateFormatted: LocalDate? = null

                if (startDate != context.getString(R.string.blankDate)) {
                    startDateFormatted = LocalDate.parse(startDate, formatter)
                }

                if (endDate != context.getString(R.string.blankDate)) {
                    endDateFormatted = LocalDate.parse(endDate, formatter)
                }

                if (startDate == context.getString(R.string.blankDate) || (startDateFormatted != null && (project.ProjectDate.isAfter(
                        startDateFormatted!!
                    ) || project.ProjectDate.isEqual(startDateFormatted!!)))
                ) {

                    if (endDate == context.getString(R.string.blankDate) || (endDateFormatted != null && (project.ProjectDate.isBefore(
                            endDateFormatted!!
                        ) || project.ProjectDate.isEqual(endDateFormatted!!)))
                    ) {


                        val newProjectCard = home_feed_card(context.requireActivity())

                        newProjectCard.binding.tvEntryTitle.text = project.ProjectTitle
                        newProjectCard.binding.tvEntryText.text =
                            project.ProjectCompanyName //project company name instead of uppcoming project header
                        newProjectCard.binding.tvEntryDate.text =
                            project.ProjectDate.format(DateTimeFormatter.ofPattern("dd/MM/yy"));
                        newProjectCard.binding.ivEntryIcon.setImageBitmap(
                            databaseManager.getProjectDefaultImage(
                                context.requireActivity()
                            )
                        )

                        newProjectCard.setOnClickListener()
                        {

                            var databaseExtension = DatabaseExtensionFunctions()

                            if (adminView == false) {
                                //open project full view

                                databaseExtension.ExpandEntryData(project.ProjectID, false, "Projects", context)


                            } else {

                                val currentProjectIndex = GlobalClass.Projects.indexOf(project)
                                val currentProjectDocumentIndex = GlobalClass.documents.allProjectIds[currentProjectIndex]

                                databaseExtension.showAdminOptionMenu(
                                    currentProjectDocumentIndex,
                                    project.ProjectID,
                                    context,
                                    "Projects"
                                )


                            }
                        }

                            //add the new view
                            displayLayout.addView(newProjectCard)

                            //add space between custom cards
                            scrollViewUtils.generateSpacer(
                                displayLayout,
                                context.requireActivity(),
                                14
                            )


                    }
                }
            }
        }
    }




    fun LoadEvents(
        searchTerm: String,
        displayLayout: LinearLayout,
        startDate: String,
        endDate: String,
        context: Fragment,
        adminView: Boolean
    ) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
        var databaseManager = DatabaseManager()

        displayLayout.removeAllViews()


        for (event in GlobalClass.Events) {
            if (event.EventTitle.lowercase()
                    .contains(searchTerm.lowercase()) || event.EventLink.lowercase()
                    .contains(searchTerm.lowercase()) || searchTerm == ""
            ) {

                var startDateFormatted: LocalDate? = null
                var endDateFormatted: LocalDate? = null

                if (startDate != context.getString(R.string.blankDate)) {
                    startDateFormatted = LocalDate.parse(startDate, formatter)
                }

                if (endDate != context.getString(R.string.blankDate)) {
                    endDateFormatted = LocalDate.parse(endDate, formatter)
                }

                if (startDate == context.getString(R.string.blankDate) || (startDateFormatted != null && (event.EventDate.isAfter(
                        startDateFormatted!!
                    ) || event.EventDate.isEqual(startDateFormatted!!)))
                ) {

                    if (endDate == context.getString(R.string.blankDate) || (endDateFormatted != null && (event.EventDate.isBefore(
                            endDateFormatted!!
                        ) || event.EventDate.isEqual(endDateFormatted!!)))
                    ) {


                        val newEventCard = home_feed_card(context.requireActivity())

                        newEventCard.binding.tvEntryTitle.text = event.EventTitle
                        newEventCard.binding.tvEntryText.text = event.EventLink
                        newEventCard.binding.tvEntryDate.text =
                            event.EventDate.format(DateTimeFormatter.ofPattern("dd/MM/yy"));
                        newEventCard.binding.ivEntryIcon.setImageBitmap(
                            databaseManager.getEventDefaultImage(
                                context.requireActivity()
                            )
                        )

                        newEventCard.setOnClickListener()
                        {
                            var databaseExtension = DatabaseExtensionFunctions()

                            if (adminView == false) {
                                //open event full view

                                databaseExtension.ExpandEntryData(event.EventID, false, "Events", context)


                            } else {

                                //show admin menu

                                val currentEventIndex = GlobalClass.Events.indexOf(event)
                                val currentEventDocumentIndex = GlobalClass.documents.allEventIDs[currentEventIndex]

                                databaseExtension.showAdminOptionMenu(
                                    currentEventDocumentIndex,
                                    event.EventID,
                                    context,
                                    "Events"
                                )


                            }
                        }

                        //add the new view
                        displayLayout.addView(newEventCard)

                        //add space between custom cards
                        scrollViewUtils.generateSpacer(displayLayout, context.requireActivity(), 14)

                    }
                }
            }
        }
    }


}
