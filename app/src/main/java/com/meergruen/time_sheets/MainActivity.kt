package com.meergruen.time_sheets

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.SystemClock
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.meergruen.time_sheets.db.AppDatabase
import com.meergruen.time_sheets.db.DataRepository
import com.meergruen.time_sheets.db.TimeSheetItemEntity
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {


    private lateinit var categoryInput: EditText
    private lateinit var subcategoryInput: EditText
    private lateinit var commentInput: EditText

    private lateinit var categoryList: ListView
    private lateinit var filteredList: ListView
    private lateinit var recentList: ListView
    private lateinit var popularList: ListView

    private lateinit var timer: Chronometer
    private lateinit var startStopButton: Button

    private lateinit var categories: ArrayAdapter<String>

    private lateinit var filteredTasks: TimeSheetTaskArrayAdapter
    private lateinit var recentTasks: TimeSheetTaskArrayAdapter
    private lateinit var popularTasks: TimeSheetTaskArrayAdapter

    private var timeSheetTasks: MutableList<TimeSheetTask> = mutableListOf()
    private var timeSheetItems: MutableList<TimeSheetItemEntity> = mutableListOf()

    private var currentComment: String = ""
    private var currentCategory: String = ""
    private var currentSubcategory: String = ""
    private var currentTask: TimeSheetTask = TimeSheetTask("", "")

    private var timerRunning = false
    private var startTime: LocalDateTime = LocalDateTime.now()
    private var endTime: LocalDateTime = LocalDateTime.now()

    private fun getDatabase(): AppDatabase? {
        return AppDatabase.getDatabase(this)
    }

    private fun getRepository(): DataRepository? {
        return DataRepository.getInstance(getDatabase()!!)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)

        categoryInput = findViewById(R.id.category_input)
        subcategoryInput = findViewById(R.id.subcategory_input)
        commentInput = findViewById(R.id.comment_input)

        categoryList = findViewById(R.id.category_list)
        filteredList = findViewById(R.id.subcategory_list)
        popularList = findViewById(R.id.popular_list)
        recentList = findViewById(R.id.recent_list)

        timer = findViewById(R.id.timer)
        startStopButton = findViewById(R.id.start_stop_button)

        
        // Enable changing Task by selecting Item in a list

        categoryList.setOnItemClickListener { _, _, position, _ ->
            currentCategory = categories.getItem(position) ?: ""
            categoryInput.setText(currentCategory, TextView.BufferType.EDITABLE)
            updateFiltered()
        }

        filteredList.setOnItemClickListener {  _, _, position, _ ->
            currentCategory = filteredTasks.getItem(position)?.category ?: ""
            currentSubcategory = filteredTasks.getItem(position)?.subcategory ?: ""
            categoryInput.setText(currentCategory, TextView.BufferType.EDITABLE)
            subcategoryInput.setText(currentSubcategory, TextView.BufferType.EDITABLE)
            updateFiltered()
        }

        recentList.setOnItemClickListener { _, _, position, _ ->
            currentCategory = recentTasks.getItem(position)?.category ?: ""
            currentSubcategory = recentTasks.getItem(position)?.subcategory ?: ""
            categoryInput.setText(currentCategory, TextView.BufferType.EDITABLE)
            subcategoryInput.setText(currentSubcategory, TextView.BufferType.EDITABLE)
            updateFiltered()
        }

        popularList.setOnItemClickListener { _, _, position, _ ->
            currentCategory = popularTasks.getItem(position)?.category ?: ""
            currentSubcategory = popularTasks.getItem(position)?.subcategory ?: ""
            categoryInput.setText(currentCategory, TextView.BufferType.EDITABLE)
            subcategoryInput.setText(currentSubcategory, TextView.BufferType.EDITABLE)
            updateFiltered()
        }

        initRecommendations()

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
            R.id.action_time_sheets -> {
                startActivity(Intent(this, TimeSheetsActivity::class.java))
                true
            }
            R.id.action_overview -> {
                startActivity(Intent(this, OverviewActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    // Button

    suspend fun onStartStopButtonPressed(@Suppress("UNUSED_PARAMETER")v: View) {

        if ( timerRunning ) {

            // Adjust Screen
            startStopButton.text = getString(R.string.start_label)
            startStopButton.setBackgroundColor(ContextCompat.getColor(this, R.color.startGreen))

            enableEditText(categoryInput)
            enableEditText(subcategoryInput)
            enableEditText(commentInput)

            // Add Task to List
            currentTask = TimeSheetTask(currentCategory, currentSubcategory)
            val newItemAdded = updateTimeSheetTasks()

            // Update Visible Recommendations
            recentTasks = updateRecommendationView(recentList, timeSheetTasks, compareByDescending { it.lastUsed })
            popularTasks = updateRecommendationView(popularList, timeSheetTasks, compareByDescending { it.timesUsed })
            if (newItemAdded) {
                updateCategoryView()
                updateFiltered()
            }

            // Add Item to List
            timeSheetItems.add(TimeSheetItemEntity(currentTask, currentComment, startTime, endTime))
            this.getRepository()?.saveTimeSheetItems(timeSheetItems)

            // (Re-)Start Timer
            timer.stop()
            endTime = LocalDateTime.now()
            timerRunning = false

        }
        else {

            // Adjust Screen
            startStopButton.text = getString(R.string.stop_label)
            startStopButton.setBackgroundColor(ContextCompat.getColor(this, R.color.stopRed))

            disableEditText(categoryInput)
            disableEditText(subcategoryInput)
            disableEditText(commentInput)


            // Initialize Item Info
            currentCategory = categoryInput.text.toString()
            currentSubcategory = subcategoryInput.text.toString()
            currentComment = commentInput.text.toString()

            // (Re-)Start Timer
            timerRunning = true
            timer.base = SystemClock.elapsedRealtime()
            timer.start()
            startTime = LocalDateTime.now()

        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean { // Clear cursor when tap outside occurs
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }


    // Helper functions

    private fun updateTimeSheetTasks(): Boolean {
        for ( task: TimeSheetTask in timeSheetTasks) {
            if ( task.category == currentCategory && task.subcategory == currentSubcategory) {
                task.hasBeenUsedAgain()
                return false
            }
        }
        timeSheetTasks.add( currentTask )
        return true
    }

    private fun updateRecommendationView(listView: ListView,  tasks: List<TimeSheetTask>,
                                         comparator: Comparator<TimeSheetTask>): TimeSheetTaskArrayAdapter {
        listView.adapter = TimeSheetTaskArrayAdapter(this, tasks.sortedWith(comparator))
        return listView.adapter as TimeSheetTaskArrayAdapter
    }

    private fun updateCategoryView() {
        categories = ArrayAdapter(this, R.layout.string_row)
        categories.addAll(timeSheetTasks.map {it.category}.distinct().sorted())
        categoryList.adapter = categories
    }

    private fun updateFiltered() {
        val filtered = ArrayList(timeSheetTasks.filter {it.category == currentCategory})
        filteredTasks = updateRecommendationView(filteredList, filtered, compareBy ({ it.category }, {it.subcategory}))
    }

    private fun initRecommendations() {

        timeSheetTasks = getRepository()?.getTimeSheetTasks()!!.toMutableList()

        updateCategoryView()
        updateFiltered()

        recentTasks = updateRecommendationView(recentList, timeSheetTasks, compareByDescending { it.lastUsed })
        popularTasks = updateRecommendationView(popularList, timeSheetTasks, compareByDescending { it.timesUsed })
    }


    private fun disableEditText(editText: EditText) {
        editText.isEnabled = false
        editText.setBackgroundResource(R.drawable.rounded_corner_disabled)
    }


    private fun enableEditText(editText: EditText) {
        editText.isEnabled = true
        editText.setBackgroundResource(R.drawable.rounded_corner_enabled)
    }


}
