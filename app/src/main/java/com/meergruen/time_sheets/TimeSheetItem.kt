package com.meergruen.time_sheets

import java.io.Serializable
import java.util.*
import kotlin.math.abs

class TimeSheetItem (
    var timeSheetTask: TimeSheetTask,
    var comment: String,
    var startTime: Date,
    var endTime: Date
): Serializable {

    fun durationInMilliSeconds(): Long {
        return abs(endTime.time - startTime.time)
    }

    fun durationInSeconds(): Double {
        return durationInMilliSeconds() / 1000.0
    }

    fun durationInMinutes(): Double {
        return durationInSeconds() / 60.0
    }

    fun durationInHours(): Double {
        return durationInMinutes() / 60.0
    }

    fun durationInDays(): Double {
        return durationInHours() / 24.0
    }

    fun durationInWorkdays(): Double {
        return durationInHours() / 8.0
    }


    fun duration(unit: Duration): Double {
        return when (unit) {
            Duration.MINUTES -> durationInMinutes()
            Duration.HOURS -> durationInHours()
            Duration.DAYS -> durationInDays()
            Duration.WORKDAYS -> durationInWorkdays()
        }
    }


    override fun toString(): String {
        return "TimeSheetItem [category=" + timeSheetTask.category +
                ", subcategory=" + timeSheetTask.subcategory +
                ", comment=" + comment +
                ", startTime=" + startTime.toString() +
                ", endTime=" + endTime.toString() + "]"
    }
}