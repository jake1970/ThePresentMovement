package com.cjras.thepresentmovement

//member type data class
data class MemberTypeDataClass(

    var MemberTypeID : Int = 0,
    var MemberType: String = "",
)
{
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Method to get the text value for a members type/role
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    fun getSingleMemberType(givenMemberTypeID : Int): String {

        //the members type value
        var foundMemberType = ""

        //loop through all member types
        for (memberType in GlobalClass.MemberTypes)
        {
            //if the member type ID matches the inputted member type ID
            if (memberType.MemberTypeID == givenMemberTypeID)
            {
                //set the members type value
                foundMemberType = memberType.MemberType
                break
            }
        }

        //return the string value of the members type
        return foundMemberType
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}