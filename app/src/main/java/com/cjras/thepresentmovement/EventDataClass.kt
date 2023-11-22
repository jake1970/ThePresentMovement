package com.cjras.thepresentmovement

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

//event data class

data class EventDataClass @RequiresApi(Build.VERSION_CODES.O) constructor(
    var EventID: Int = 0,
    var EventTitle: String = "",
    var EventDate : LocalDate = LocalDate.now(),
    var EventLink: String = "",
    var UserID : String = "",
    var HasImage: Boolean = false
)
