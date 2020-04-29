package com.meergruen.time_sheets

import java.io.Serializable
import java.util.*

class TimeSheetTask (val category: String, val subcategory: String ): Serializable {

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

}