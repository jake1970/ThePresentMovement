package com.cjras.thepresentmovement

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.type.DateTime
import java.time.LocalDate

data class AnnouncementDataClass @RequiresApi(Build.VERSION_CODES.O) constructor(
    var AnnouncementID: String = "",
    var AnnouncementTitle: String = "",
    var AnnouncementMessage : String = "",
    var AnnouncementDate: LocalDate = LocalDate.now(),
    var UserID : String = ""
)
