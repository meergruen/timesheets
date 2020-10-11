package com.meergruen.time_sheets.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.meergruen.time_sheets.Duration
import com.meergruen.time_sheets.TimeSheetTask
import java.time.LocalDateTime

@Entity(tableName = "time_sheet_items")
class TimeSheetItemEntity (
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "category") var category: String,
    @ColumnInfo(name = "subcategory") var subcategory: String,
    @ColumnInfo(name = "comment") var comment: String,
    @ColumnInfo(name = "start_time") var startTime: LocalDateTime,
    @ColumnInfo(name = "end_time") var endTime: LocalDateTime ) {


    constructor(task: TimeSheetTask, comment: String, startTime: LocalDateTime, endTime: LocalDateTime) :
            this(Converters().dateToTimestamp(startTime) + Converters().dateToTimestamp(endTime),
                task.category,
                task.subcategory,
                comment,
                startTime,
                endTime )


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


    fun key(): Pair<String,String> {
        return Pair(category, subcategory)
    }

    fun startTimeStamp(): Long {
        return Converters().dateToTimestamp(startTime)
    }

    fun endTimeStamp(): Long {
        return Converters().dateToTimestamp(endTime)
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
        return "TimeSheetItem [category=" + category +
                ", subcategory=" + subcategory +
                ", comment=" + comment +
                ", startTime=" + startTime.toString() +
                ", endTime=" + endTime.toString() + "]"
    }
}
