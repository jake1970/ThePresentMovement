package com.cjras.thepresentmovement

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

//project data class

data class ProjectDataClass @RequiresApi(Build.VERSION_CODES.O) constructor(
    var ProjectID: Int = 0,
    var ProjectTitle: String = "",
    var ProjectDate : LocalDate = LocalDate.now(), //the date when the project is set to take place
    var ProjectOverview: String = "",
    var ProjectCompanyName : String = "",
    var ProjectCompanyAbout: String = "",
    var UserID : String = "",
    var HasImage: Boolean = false //whether the project has an image or not
)
