package com.meergruen.time_sheets

import android.util.Log
import java.io.Serializable
import java.time.LocalDateTime
import java.time.Period
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.abs

class TimeSheetItem (
    var timeSheetTask: TimeSheetTask,
    var comment: String,
    var startTime: LocalDateTime,
    var endTime: LocalDateTime
): Serializable {

    fun durationInMilliSeconds(): Double {
        val duration = java.time.Duration.between(startTime, endTime)
        return duration.toMillis().toDouble()
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
            Duration.WORKDAYS -> durationInDays()
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