package com.cjras.thepresentmovement

import com.google.type.DateTime
import java.time.LocalDate

data class AnnouncementDataClass(
    var AnnouncementID: String = "",
    var AnnouncementTitle: String = "",
    var AnnouncementMessage : String = "",
    var AnnouncementDate: LocalDate = LocalDate.now(),
    var UserID : String = ""
)
