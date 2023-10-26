package com.cjras.thepresentmovement

import java.time.LocalDate

data class EventDataClass(
    var EventID: Int = 0,
    var EventTitle: String = "",
    var EventDate : LocalDate = LocalDate.now(),
    var EventLink: String = "",
    var UserID : String = "",
    var HasImage: Boolean = false
)
