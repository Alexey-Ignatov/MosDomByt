package ru.acurresearch.dombyta.Utils

import android.widget.DatePicker
import java.util.*


fun DatePicker.getdate(): java.util.Date {
    val day = dayOfMonth
    val month = month
    val year = year

    val calendar = Calendar.getInstance()
    calendar.set(year, month, day)

    return calendar.getTime()
}