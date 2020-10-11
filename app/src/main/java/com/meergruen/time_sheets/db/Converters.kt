package com.meergruen.time_sheets.db

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return if (value == null) null
            else LocalDateTime.ofInstant(
            Instant.ofEpochMilli(value),
            TimeZone.getDefault().toZoneId())
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long {
       // val zoned = date?.atZone(ZoneId.of("CET"))
        val zoned = date?.atZone(ZoneId.systemDefault() )
        return zoned?.toInstant()?.toEpochMilli() ?: 0
    }

}