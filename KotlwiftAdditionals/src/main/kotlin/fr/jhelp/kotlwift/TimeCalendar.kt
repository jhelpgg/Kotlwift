package fr.jhelp.kotlwift

import java.util.Calendar

class TimeCalendar
{
    private val calendar = Calendar.getInstance()

    fun year() = this.calendar[Calendar.YEAR]

    fun month() = this.calendar[Calendar.MONTH] + 1

    fun dayOfMonth() = this.calendar[Calendar.DAY_OF_MONTH]

    fun dayOfWeek() = this.calendar[Calendar.DAY_OF_WEEK]

    fun hour() = this.calendar[Calendar.HOUR_OF_DAY]

    fun minute() = this.calendar[Calendar.MINUTE]

    fun second() = this.calendar[Calendar.SECOND]
}