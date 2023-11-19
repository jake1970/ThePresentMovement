package com.cjras.thepresentmovement

//document ID data class
data class DocumentID (
        var allUserIDs: ArrayList<String> = ArrayList(), //list of user id documents
        var allMemberTypeIDs: ArrayList<String> = ArrayList(), //list of member type id documents
        var allAnnouncmentIds: ArrayList<String> = ArrayList(), //list of announcement id documents
        var allEventIDs: ArrayList<String> = ArrayList(), //list of event id documents
        var allProjectIds: ArrayList<String> = ArrayList(), //list of project id documents
        var allUserProjectIds: ArrayList<String> = ArrayList() //list of user project id documents
    )
{
}