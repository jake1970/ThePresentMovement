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

    //instantiate scroll view tools to be used to add visual spaces between list items
    private val scrollViewUtils = ScrollViewTools()


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to populate a spinner with the member types
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun populateMemberTypes(spinner: Spinner, context: Context, includeAll: Boolean) {

        //create list containing distinct values for member type text values. eg: Senior Member
        val memberTypeOptions: ArrayList<String> =
            GlobalClass.MemberTypes.distinct().map { it.MemberType }.toCollection(ArrayList())


        //check if the first item in the spinner should be populated with the "All" members option
        if (includeAll == true) {

            //add the "All" members option to the first position in the member type options
            memberTypeOptions.add(0, "All")
        }

        //new adapter based on the custom xml and member type options list
        val adapter = ArrayAdapter(context, R.layout.spinner_layout, memberTypeOptions)

        //set the adapters drop down configuration
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        //set the spinners adapter to the configured adapter
        spinner.adapter = adapter

    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to open the expanded contacts view
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun invokeExpandedContactsView(userID: String, context: Fragment) {

        //create local fragment controller
        val fragmentControl = FragmentManager()

        //create an instance of the expanded contact fragment
        val expandedContactView = expanded_contact()

        //create an arguments bundle for th fragment
        val args = Bundle()

        //add the selected user id as an argument
        args.putString("selectedUserID", userID)

        //set the fragment instance arguments
        expandedContactView.arguments = args

        //open the instance of the expanded contacts fragment
        fragmentControl.replaceFragment(
            expandedContactView,
            R.id.flContent,
            context.parentFragmentManager
        )
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to load the contacts/all users custom component cards according to the given filter parameters: search term and what type of member is being looked for
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun loadContacts(
        searchTerm: String,
        memberTypeFilter: String,
        displayLayout: LinearLayout,
        context: Fragment,
        adminView: Boolean
    ) {

        //clear the current view containing the list of contacts
        displayLayout.removeAllViews()

        //new scroll view utility instance to add visual spaces between list items
        val scrollViewUtils = ScrollViewTools()

        //loop through the users
        for (user in GlobalClass.Users) {

            //exclude the current user from the list of contacts
            if (user != GlobalClass.currentUser) {

                //check if the search text criteria is within the members full name or if the search text criteria was not specified
                if (user.getFullName().lowercase()
                        .contains(searchTerm.lowercase()) || searchTerm == ""
                ) {


                    //the current members role type as a string value. eg: 2 = Senior Member
                    //call method to get the current member type as a string, pass the member type id
                    val currentMemberTypeString =
                        MemberTypeDataClass().getSingleMemberType(user.MemberTypeID)


                    //check if the member type criteria matches the current users role, or if the member type criteria is not being used as a search factor
                    //if the member type filter is "All" the member type is not being considered when searching
                    if (currentMemberTypeString == memberTypeFilter || memberTypeFilter == "All") {


                        //new custom component card to show the matching users
                        var newContact = contact_card(context.requireActivity())

                        //set the card contact name to the users full name
                        newContact.binding.tvContactName.text =
                            user.getFullName()


                        //set the member type to the text value of the corresponding member type
                        //call method to get the matching member type text value based on the member type id
                        var memberType =
                            MemberTypeDataClass().getSingleMemberType(user.MemberTypeID)

                        //set the member type on the custom card component
                        newContact.binding.tvContactRole.text = memberType


                        //---------------------------------------------------------------------------------------------------------------------------------------------------------
                        //When the custom contact card is clicked
                        //---------------------------------------------------------------------------------------------------------------------------------------------------------
                        newContact.setOnClickListener()
                        {

                            //check if the function is being run from the admin view
                            if (adminView == false) {

                                //if the function is not run from the admin view

                                //call method to open the contacts information in the expanded contact fragment
                                //pass the selected cards user id and the current fragment as context
                                invokeExpandedContactsView(user.UserID, context)

                            } else {

                                //if the function is run from the admin view

                                //new dialog
                                val builder = AlertDialog.Builder(context.requireActivity())

                                //set the dialog title
                                builder.setTitle(R.string.adminMenuTitleText)

                                //new menu option list from the defined list
                                var menuArray =
                                    context.resources.getStringArray(R.array.adminItemFunctions)

                                //remove the delete option from the menu
                                menuArray = menuArray.filter { it != menuArray[1].toString() }
                                    .toTypedArray()

                                //set the source options for the dialog
                                builder.setItems(menuArray) { dialog, selectedItem ->

                                    //evaluate the selected option from the popup
                                    when (selectedItem) {
                                        0 -> {
                                            //if the view option is selected

                                            //call method to open the contacts information in the expanded contact fragment
                                            //pass the selected cards user id and the current fragment as context
                                            invokeExpandedContactsView(user.UserID, context)
                                        }
                                        1 -> {
                                            //if the delete option is selected

                                            //get the index of the selected user in the list of users
                                            val currentUserIndex = GlobalClass.Users.indexOf(user)

                                            //get the index of the users document entry
                                            val currentUserDocumentIndex =
                                                GlobalClass.documents.allUserIDs[currentUserIndex]

                                            //instantiate database extension utility
                                            var databaseExtension = DatabaseExtensionFunctions()

                                            //call method to give the user the option of deleting another user
                                            //pass the users current document index, the passed context, the appropriate table, the users user id, and the default project id.
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
                        //---------------------------------------------------------------------------------------------------------------------------------------------------------

                        //add the new view
                        displayLayout.addView(newContact)

                        //add space between custom cards
                        scrollViewUtils.generateSpacer(displayLayout, context.requireActivity(), 14)

                    }
                }
            }
        }
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------





    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to load the announcements/notices custom component cards according to the given filter parameters: search term, start date, and end date
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
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
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to load the projects custom component cards according to the given filter parameters: search term, start date, and end date
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
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
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to load the events custom component cards according to the given filter parameters: search term, start date, and end date
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
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
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


}
