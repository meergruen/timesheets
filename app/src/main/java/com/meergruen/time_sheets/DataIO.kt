package com.meergruen.time_sheets

import android.content.Context
import android.util.Log
import java.io.*


@Suppress("unchecked_cast")
fun loadTimeSheetTasks(context: Context): ArrayList<TimeSheetTask> {
    var res = ArrayList<TimeSheetTask>()
    val file = File(context.getDir("data", Context.MODE_PRIVATE), "tasks")
    if (file.exists()) {
        val inputStream = ObjectInputStream(FileInputStream(file))
        res = inputStream.readObject() as ArrayList<TimeSheetTask>
        inputStream.close()

        Log.i("IO", "Loaded Tasks:" + res.joinToString(", "))
    }
    else {
        Log.i("IO", "Tasks file does not exist. Initializing empty list.")
    }
    return res
}


fun saveTimeSheetTasks(context: Context, tasks: ArrayList<TimeSheetTask>)  {
    val file = File(context.getDir("data", Context.MODE_PRIVATE), "tasks")

    Log.i("IO", "Write Tasks:" + tasks.joinToString(", "))

    val outputStream = ObjectOutputStream(FileOutputStream(file))
    outputStream.writeObject(tasks)
    outputStream.flush()
    outputStream.close()
}


@Suppress("unchecked_cast")
fun loadTimeSheetItems(context: Context): ArrayList<TimeSheetItem> {
    var res = ArrayList<TimeSheetItem>()
    val file = File(context.getDir("data", Context.MODE_PRIVATE), "items")
    if (file.exists()) {
        val inputStream = ObjectInputStream(FileInputStream(file))
        res = inputStream.readObject() as ArrayList<TimeSheetItem>
        inputStream.close()

        Log.i("IO", "Loaded Items:" + res.joinToString(", "))
    }
    else {
        Log.i("IO", "Items file does not exist. Initializing empty list.")
    }
    return res
}


fun saveTimeSheetItems(context: Context, items: ArrayList<TimeSheetItem>)  {
    val file = File(context.getDir("data", Context.MODE_PRIVATE), "items")

    Log.i("IO", "Write Items:" + items.joinToString(", "))

    val outputStream = ObjectOutputStream(FileOutputStream(file))
    outputStream.writeObject(items)
    outputStream.flush()
    outputStream.close()
}