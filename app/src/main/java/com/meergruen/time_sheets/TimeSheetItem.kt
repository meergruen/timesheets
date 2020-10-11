package com.meergruen.time_sheets

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.meergruen.time_sheets.db.Converters
import java.io.Serializable
import java.time.LocalDateTime


class TimeSheetItem (
    var timeSheetTask: TimeSheetTask,
    val comment: String,
    var startTime: LocalDateTime,
    var endTime: LocalDateTime
): Serializable {

    //companion object { private const val serialVersionUID: Long = 1 } // if java serialization used for persisting data



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