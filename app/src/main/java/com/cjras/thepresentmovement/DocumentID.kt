package com.cjras.thepresentmovement

data class DocumentID (
        var allUserIDs: ArrayList<String> = ArrayList(),
        var allMemberTypeIDs: ArrayList<String> = ArrayList(),
        var allAnnouncmentIds: ArrayList<String> = ArrayList(),
        var allEventIDs: ArrayList<String> = ArrayList(),
        var allProjectIds: ArrayList<String> = ArrayList()
        var allUserProjectIds: ArrayList<String> = ArrayList()
    )
{
}