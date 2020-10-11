package com.meergruen.time_sheets

import com.meergruen.time_sheets.db.TimeSheetItemEntity
import java.io.Serializable
import java.time.LocalDateTime
import kotlin.Comparator

class TimeSheetTask : Serializable, Comparator<TimeSheetTask> {

    var category: String
    var subcategory: String

    constructor( category: String,  subcategory: String ){ // to primary possible?
        this.category = category
        this.subcategory = subcategory
    }

    constructor( items: List<TimeSheetItemEntity> ){

        this.category = items[0].category
        this.subcategory = items[0].subcategory

        lastUsed = items.maxBy{it.startTimeStamp()}?.startTime ?:  LocalDateTime.now()
        timesUsed = items.count()
    }


    //companion object { private const val serialVersionUID: Long = 1 } // if java serialization used for persisting data

    var lastUsed: LocalDateTime = LocalDateTime.now()
    var timesUsed: Int = 1

    fun hasBeenUsedAgain() {
        lastUsed = LocalDateTime.now()
        timesUsed += 1
    }

    override fun toString(): String {
        return "TimeSheetTask: [category=" + category +
                ", subcategory=" + subcategory +
                ", lastUsed=" + lastUsed.toString() +
                ", timesUsed=" + timesUsed + "]"
    }

    override fun compare(task1: TimeSheetTask, task2: TimeSheetTask): Int { // natural, alphabetical ordering
        if ( task1.category < task2.category ) return -1
        if ( task1.category > task2.category ) return 1
        if ( task1.subcategory < task2.subcategory ) return -1
        if ( task1.subcategory > task2.subcategory ) return 1
        return 0
    }
}