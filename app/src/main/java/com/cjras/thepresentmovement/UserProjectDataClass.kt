package com.cjras.thepresentmovement

//user projects data class
data class UserProjectDataClass(
    var UserProjectID: Int = 0, //primary key
    var UserID: String = "", //the user who joins the project
    var ProjectID : Int = 0 //the project the user joins
)
