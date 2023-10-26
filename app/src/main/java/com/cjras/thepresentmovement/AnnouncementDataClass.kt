package com.cjras.thepresentmovement

import com.google.type.DateTime
import java.time.LocalDate

data class AnnouncementDataClass(
    var AnnouncementID: Int = 0,
    var AnnouncementTitle: String = "",
    var AnnouncementMessage : String = "",
    var AnnouncementDate: LocalDate = LocalDate.now(),
    var UserID : String = ""
)
