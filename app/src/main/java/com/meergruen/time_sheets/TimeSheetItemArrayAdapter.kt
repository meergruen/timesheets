package com.meergruen.time_sheets

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TimeSheetItemArrayAdapter(context: Context, items: ArrayList<TimeSheetItem>) : ArrayAdapter<TimeSheetItem>(context, 0, items) {

    private var dateFormatPattern = "dd.MM.yyyy"
    private var locale = Locale.getDefault()
    private var durationUnit = Duration.HOURS

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val dateFormat =  SimpleDateFormat(dateFormatPattern, locale)


        // Get the data item for this position
        val resView: View = convertView ?:  LayoutInflater.from(context).inflate(R.layout.time_sheet_item_row, parent, false)
        val item: TimeSheetItem? = getItem(position)

        // Lookup view for data population
        val category = resView.findViewById(R.id.category_input) as TextView
        val subcategory = resView.findViewById(R.id.subcategory_input) as TextView
        val startDate = resView.findViewById(R.id.start_date_input) as TextView
        val duration = resView.findViewById(R.id.duration_input) as TextView
        val comment = resView.findViewById(R.id.comment_input) as TextView

        // Populate the data into the template view using the data object
        category.text = item!!.timeSheetTask.category
        subcategory.text = item.timeSheetTask.subcategory
        startDate.text = dateFormat.format(item.startTime)
        duration.text = "%.2f".format(item.duration(durationUnit))
        comment.text = item.comment

        // Return the completed view to render on screen
        return resView
    }
}