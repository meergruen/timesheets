package com.meergruen.time_sheets

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class TaskArrayAdapter(context: Context, tasks: ArrayList<TimeSheetTask> ) : ArrayAdapter<TimeSheetTask>(context, 0, tasks) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // Get the data item for this position
        val resView: View = convertView ?:  LayoutInflater.from(context).inflate(R.layout.task_row, parent, false)
        val task: TimeSheetTask? = getItem(position)

        // Lookup view for data population
        val tvName = resView.findViewById(R.id.row_category_label) as TextView
        val tvHome = resView.findViewById(R.id.row_subcategory_label) as TextView

        // Populate the data into the template view using the data object
        tvName.text = task!!.category
        tvHome.text = task.subcategory

        // Return the completed view to render on screen
        return resView
    }
}