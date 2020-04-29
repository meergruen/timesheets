package com.meergruen.time_sheets

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {


    private lateinit var categoryInput: EditText
    private lateinit var subcategoryInput: EditText
    private lateinit var commentInput: EditText

    private lateinit var categoryList: ListView
    private lateinit var subcategoryList: ListView
    private lateinit var recentList: ListView
    private lateinit var popularList: ListView

    private lateinit var timer: Chronometer
    private lateinit var startStopButton: Button

    private lateinit var categories: ArrayAdapter<String>
    private lateinit var currentSubcategories: ArrayAdapter<String>
    private lateinit var recentTasks: TaskArrayAdapter
    private lateinit var popularTasks: TaskArrayAdapter

    private var subcategories: HashMap<String, ArrayAdapter<String>> = HashMap()

    private var categoriesUsed: ArrayList<TimeSheetTask> = ArrayList()
    private var recentUsed: ArrayList<TimeSheetTask> = ArrayList()
    private var mostUsed: ArrayList<TimeSheetTask> = ArrayList()
    private var timeSheetItems: ArrayList<TimeSheetItem> = ArrayList()

    private var currentCategory: String = ""
    private var currentSubcategory: String = ""

    private var timerRunning = false
    private var startTime: Date = Date()
    private var endTime: Date = Date()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        categoryInput = findViewById(R.id.category_input)
        subcategoryInput = findViewById(R.id.subcategory_input)
        commentInput = findViewById(R.id.comment_input)


        categoryList = findViewById(R.id.category_list)
        subcategoryList = findViewById(R.id.subcategory_list)
        popularList = findViewById(R.id.popular_list)
        recentList = findViewById(R.id.recent_list)

        timer = findViewById(R.id.timer);
        startStopButton = findViewById(R.id.start_stop_button)


        categories = ArrayAdapter<String>(this, R.layout.string_row)
        currentSubcategories = ArrayAdapter<String>(this, R.layout.string_row)
        recentTasks = TaskArrayAdapter(this, categoriesUsed)
        popularTasks = TaskArrayAdapter(this, categoriesUsed)

        
        // Enable changing Task by selecting Item in a list

        categoryList.setOnItemClickListener { _, _, position, _ ->
            currentCategory = categories.getItem(position) ?: ""
            categoryInput.setText(currentCategory, TextView.BufferType.EDITABLE)
            updateSubcategoryListView()
        }

        subcategoryList.setOnItemClickListener { _, _, position, _ ->
            currentSubcategory = currentSubcategories.getItem(position) ?: ""
            subcategoryInput.setText(currentSubcategory, TextView.BufferType.EDITABLE)
        }

        recentList.setOnItemClickListener { _, _, position, _ ->
            currentCategory = recentTasks.getItem(position)?.category ?: ""
            currentSubcategory = recentTasks.getItem(position)?.subcategory ?: ""
            categoryInput.setText(currentCategory, TextView.BufferType.EDITABLE)
            subcategoryInput.setText(currentSubcategory, TextView.BufferType.EDITABLE)
            updateSubcategoryListView()
        }

        popularList.setOnItemClickListener { _, _, position, _ ->
            currentCategory = recentTasks.getItem(position)?.category ?: ""
            currentSubcategory = recentTasks.getItem(position)?.subcategory ?: ""
            categoryInput.setText(currentCategory, TextView.BufferType.EDITABLE)
            subcategoryInput.setText(currentSubcategory, TextView.BufferType.EDITABLE)
            updateSubcategoryListView()
        }


        initCategoryLists()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_timesheets -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


    // Button

    fun onStartStopButtonPressed(view: View) {

        if ( timerRunning ) {

            timer.stop();
            endTime = Date()

            startStopButton.text = getString(R.string.start_label)
            startStopButton.setBackgroundColor(ContextCompat.getColor(this, R.color.startGreen))

          //  saveData()
          //  saveCategoryList()

            enableEditText(categoryInput)
            enableEditText(subcategoryInput)
            enableEditText(commentInput)
/*
            enableEditText(categoryList)
            enableEditText(subcategoryList)*/
            timerRunning = false

        }
        else {

            timer.start();
            startTime = Date()

            startStopButton.text = getString(R.string.stop_label)
            startStopButton.setBackgroundColor(ContextCompat.getColor(this, R.color.stopRed))

            disableEditText(categoryInput)
            disableEditText(subcategoryInput)
            disableEditText(commentInput)

/*
            disableEditText(categoryList)
            disableEditText(subcategoryList)*/
            timerRunning = true

        }
    }


    // Helper functions

    @Suppress("unchecked_cast")
    private fun initCategoryLists() {
        val file = File(getDir("data", Context.MODE_PRIVATE), "categories")



        /*

        if (file.exists()) {
            val inputStream = ObjectInputStream(FileInputStream(file))
            categoriesUsed = inputStream.readObject() as ArrayList<TimeSheetSubject>

            for (item in categoriesUsed ) {
                if (item.category in subcategories) {

                    subcategories[item.category]!!.add(item.subcategory)
                }
                else {

                    categories!!.add(item.category)
                    subcategories[item.category] = ArrayAdapter<String>(this, R.layout.string_row)
                }
            }
            categories!!.addAll(subcategories.keys)
        }*/

        categoryList.adapter = categories
        subcategoryList.adapter = currentSubcategories
    }

    private fun saveCategoryList() {


        subcategories[currentCategory] = currentSubcategories!!
        if (subcategories[currentCategory]!!.getPosition(currentSubcategory) < 0) {

            // Update array adapter
            subcategories[currentCategory]!!.add(currentSubcategory)

            // Update List to write
            categoriesUsed.add(TimeSheetTask(currentCategory, currentSubcategory))

        }
        else {
            // Update list to write
            val subject = findTimeSheetSubject(currentCategory, currentSubcategory)
            subject!!.hasBeenUsedAgain()
        }

        val file = File(getDir("data", Context.MODE_PRIVATE), "categories")
        val outputStream = ObjectOutputStream(FileOutputStream(file))
        outputStream.writeObject(categoriesUsed)
        outputStream.flush()
        outputStream.close()
    }


    private fun findTimeSheetSubject(category: String, subcategory: String): TimeSheetTask? {
        for ( item: TimeSheetTask in categoriesUsed) {
            if ( item.category == category && item.subcategory == subcategory) {
                return item
            }
        }
        return null
    }



    private fun updateSubcategoryListView() {
        if (currentCategory in subcategories) {
            currentSubcategories = subcategories[currentCategory]!!
        }
        else {
            subcategories[currentCategory] = ArrayAdapter<String>(this, R.layout.string_row)
            currentSubcategories = subcategories[currentCategory]!!
        }
    }

    private fun disableEditText(editText: EditText) {
        editText.isFocusable = false
        editText.isEnabled = false
        editText.isCursorVisible = false
        //editText.keyListener = null
        editText.setBackgroundResource(R.drawable.rounded_corner_disabled)
    }


    private fun enableEditText(editText: EditText) {
        editText.isFocusable = true
        editText.isEnabled = true
        editText.isCursorVisible = true
        editText.setBackgroundResource(R.drawable.rounded_corner_enabled)
    }

    private fun saveData() {
        val category = categoryInput.text.toString()
        val subcategory = subcategoryInput.text.toString()
        val comment = commentInput.text.toString()

        timeSheetItems.add(TimeSheetItem(category, subcategory, comment, startTime, endTime))

        val file = File(getDir("data", Context.MODE_PRIVATE), "time_sheet_items")
        val outputStream = ObjectOutputStream(FileOutputStream(file))
        outputStream.writeObject(timeSheetItems)
        outputStream.flush()
        outputStream.close()
    }

}
