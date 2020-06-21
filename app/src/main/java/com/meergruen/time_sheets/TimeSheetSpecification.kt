package com.meergruen.time_sheets

import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class TimeSheetSpecification (var name: String): Serializable {

    var timeSheetTasks = HashSet<TimeSheetTask>()
    var fileFormat = FileFormat.CSV

    // Columns
    var showStartTime = true
    var showEndTime = false
    var showCategory = false
    var showSubcategory = true
    var showComment = true
    var showDuration = true

    // Column names
    var startTimeTitle = "Date"
    var endTimeTitle = "Until"
    var categoryTitle = "Category"
    var subcategoryTitle = "Project"
    var commentTitle = "Notes"
    var durationTitle = "Effort"

    // Column formatting
    var startTimeFormatPattern = "dd.MM."
    var startTimeLocale: Locale = Locale.getDefault()
    var endTimeFormatPattern = "dd.MM."
    var endTimeLocale: Locale = Locale.getDefault()
    var durationUnit = Duration.HOURS

    // Private variables
    val calendar = Calendar.getInstance()


    // Formatting

    fun getDateString(date: LocalDateTime, dateFormatPattern: String, locale: Locale): String {
        val dateFormat =  SimpleDateFormat(dateFormatPattern, locale)
        return dateFormat.format(date)
    }

    private fun getColumnNames(): ArrayList<String> {
        val columnNames = ArrayList<String>()
        if (showStartTime)   columnNames.add(startTimeTitle)
        if (showEndTime)     columnNames.add(endTimeTitle)
        if (showCategory)    columnNames.add(categoryTitle)
        if (showSubcategory) columnNames.add(subcategoryTitle)
        if (showComment)     columnNames.add(commentTitle)
        if (showDuration)    columnNames.add(durationTitle)
        return columnNames
    }

    fun getTable(items: ArrayList<TimeSheetItem>): String {  // filter valid items before
        val table = ArrayList<ArrayList<String>>()
        for (item in items) {
            val row = ArrayList<String>()
            if (showStartTime)   row.add(getDateString(item.startTime, startTimeFormatPattern, startTimeLocale))
            if (showEndTime)     row.add(getDateString(item.endTime, endTimeFormatPattern, endTimeLocale))
            if (showCategory)    row.add(item.timeSheetTask.category)
            if (showSubcategory) row.add(item.timeSheetTask.subcategory)
            if (showComment)     row.add(item.comment)
            if (showDuration)    row.add("%.2f".format(item.duration(durationUnit)))
            table.add(row)
        }
        val columnNames = getColumnNames()

        return formatTable(columnNames, table, fileFormat)
    }


    // Time sheet item retrieval

    fun getValidItems(items: ArrayList<TimeSheetItem>) : ArrayList<TimeSheetItem> {
        val list = ArrayList<TimeSheetItem>()
        for ( item in items) {
            if ( item.timeSheetTask in timeSheetTasks  ) { // TODO: Check if working as intended
                list.add(item)
            }
        }
        return list
    }


    fun getItemsSince(items: ArrayList<TimeSheetItem>, since: LocalDateTime) : ArrayList<TimeSheetItem> {
        val list = ArrayList<TimeSheetItem>()
        for ( item in items) {
            if ( item.startTime >= since ) {
                list.add(item)
            }
        }
        return list
    }

    fun getItemsUntil(items: ArrayList<TimeSheetItem>, until: LocalDateTime) : ArrayList<TimeSheetItem> {
        val list = ArrayList<TimeSheetItem>()
        for ( item in items) {
            if ( item.startTime <= until ) {
                list.add(item)
            }
        }
        return list
    }


    fun getItemsSinceUntil(items: ArrayList<TimeSheetItem>, since: LocalDateTime, until: LocalDateTime) : ArrayList<TimeSheetItem> {
        return getItemsUntil(getItemsSince(items, since), until)
    }


    fun getItemsCurrentMonth(items: ArrayList<TimeSheetItem>) : ArrayList<TimeSheetItem> {
        val list = ArrayList<TimeSheetItem>()
        val thisMonth = LocalDateTime.now().month
        for ( item in items) {
            if ( item.startTime.month == thisMonth ) {
                list.add(item)
            }
        }
        return list
    }

    fun getItemsLastMonth(items: ArrayList<TimeSheetItem>) : ArrayList<TimeSheetItem> {
        val list = ArrayList<TimeSheetItem>()
        val lastMonth = LocalDateTime.now().minusMonths(1).month
        for ( item in items) {
            if ( item.startTime.month == lastMonth ) {
                list.add(item)
            }
        }
        return list
    }


    override fun toString(): String {
        return "TimeSheet [name=$name]"
    }


    enum class OutputMethod {
        DROPBOX,
        EMAIL
    }

    enum class Schedule {
        MONTHLY,
        CUSTOM
    }
}