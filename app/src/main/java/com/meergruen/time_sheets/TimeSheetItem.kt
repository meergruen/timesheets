package com.meergruen.time_sheets

import java.io.Serializable
import java.util.*
import kotlin.math.abs

class TimeSheetItem (
    var category: String,
    var subcategory: String,
    var comment: String,
    var startTime: Date,
    var endTime: Date
): Serializable {

    fun duration(): Long {
        return abs(endTime.time - startTime.time)
    }

    override fun toString(): String {
        return "TimeSheetItem [category=" + category +
                ", subcategory=" + subcategory +
                ", comment=" + comment +
                ", startTime=" + startTime.toString() +
                ", endTime=" + endTime.toString() + "]"
    }
}