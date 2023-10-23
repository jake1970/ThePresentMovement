package com.cjras.thepresentmovement

data class MemberTypeDataClass(

    var MemberTypeID : Int = 0,
    var MemberType: String = "",
)
{
    fun getSingleMemberType(givenMemberTypeID : Int): String {

        var foundMemberType = ""
        for (memberType in GlobalClass.MemberTypes)
        {
            if (memberType.MemberTypeID == givenMemberTypeID)
            {
                foundMemberType = memberType.MemberType
                break
            }
        }

        return foundMemberType
    }
}