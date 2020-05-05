package com.meergruen.time_sheets

import java.io.Serializable
import java.util.*
import kotlin.Comparator

class TimeSheetTask (val category: String, val subcategory: String ): Serializable, Comparator<TimeSheetTask> {

    var lastUsed: Date? = Date()
    var timesUsed: Int = 1

    fun hasBeenUsedAgain() {
        lastUsed = Date()
        timesUsed += 1
    }

    override fun toString(): String {
        return "TimeSheetSubject: [category=" + category +
                ", subcategory=" + subcategory +
                ", lastUsed=" + lastUsed.toString() +
                ", timesUsed=" + timesUsed + "]"
    }

    override fun compare(task1: TimeSheetTask, task2: TimeSheetTask): Int {
        if ( task1.category < task2.category ) return -1
        if ( task1.category > task2.category ) return 1
        if ( task1.subcategory < task2.subcategory ) return -1
        if ( task1.subcategory > task2.subcategory ) return 1
        return 0
    }
}