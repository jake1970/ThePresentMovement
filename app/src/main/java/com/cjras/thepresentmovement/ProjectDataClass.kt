package com.cjras.thepresentmovement

import java.time.LocalDate

data class ProjectDataClass(
    var ProjectID: Int = 0,
    var ProjectTitle: String = "",
    var ProjectDate : LocalDate = LocalDate.now(),
    var ProjectOverview: String = "",
    var ProjectCompanyName : String = "",
    var ProjectCompanyAbout: String = "",
    var UserID : String = "",
    var HasImage: Boolean = false
)
