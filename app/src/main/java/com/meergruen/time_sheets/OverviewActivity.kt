package com.meergruen.time_sheets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.meergruen.time_sheets.db.AppDatabase
import com.meergruen.time_sheets.db.DataRepository
import com.meergruen.time_sheets.db.TimeSheetItemEntity
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class OverviewActivity : AppCompatActivity() {

    private lateinit var timeSheetItems: List<TimeSheetItemEntity>

    private var dateFormatPattern = "dd.MM.yyyy"
    private var locale = Locale.getDefault()
    private var durationUnit = Duration.HOURS


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)
        //setSupportActionBar(findViewById(R.id.toolbar_overview))
        createEditableItemTable()

    }
    private fun getDatabase(): AppDatabase? {
        return AppDatabase.getDatabase(this)
    }

    private fun getRepository(): DataRepository? {
        return DataRepository.getInstance(getDatabase()!!)
    }

    @SuppressLint("SetTextI18n")
    private fun createEditableItemTable() {
        val dateFormat =  SimpleDateFormat(dateFormatPattern, locale)

        timeSheetItems = getRepository()?.getTimeSheetItems()!!

        val table = findViewById<TableLayout>(R.id.raw_data_table)

        if (table.childCount > 1) {
            table.removeViews(1, table.childCount - 1)
        }


        val inflater = layoutInflater
        for ( item in timeSheetItems) {

            val row = inflater.inflate(R.layout.time_sheet_item_row, table, false) as TableRow

            // Lookup view for data population
            val category = row.findViewById(R.id.category_input) as EditText
            val subcategory = row.findViewById(R.id.subcategory_input) as EditText
            val startDate = row.findViewById(R.id.start_date_input) as EditText
            val duration = row.findViewById(R.id.duration_input) as EditText
            val comment = row.findViewById(R.id.comment_input) as EditText

            // Populate the data into the template view using the data object
            category.setText(item.category, TextView.BufferType.EDITABLE)
            subcategory.setText(item.subcategory, TextView.BufferType.EDITABLE)
            startDate.setText( dateFormat.format(item.startTime), TextView.BufferType.EDITABLE)
            duration.setText("%.2f".format(item.duration(durationUnit)), TextView.BufferType.EDITABLE) // TODO: time input? or 2 edit fields?
            comment.setText(item.comment, TextView.BufferType.EDITABLE)

            startDate.setOnClickListener { insertDateTime(item) }
            duration.addTextChangedListener (object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    item.endTime = item.startTime.plusHours(duration.text.toString().toLong())
                }
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            })

            table.addView(row)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_time_sheets -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    suspend fun onSaveButtonPressed(@Suppress("UNUSED_PARAMETER") view: View) {
        getRepository()?.saveTimeSheetItems(timeSheetItems)
        Toast.makeText(this, "Data has been saved!", Toast.LENGTH_SHORT).show()
    }

    fun onResetButtonPressed(@Suppress("UNUSED_PARAMETER") view: View) {
        createEditableItemTable()
        Toast.makeText(this, "Data has been reloaded!", Toast.LENGTH_SHORT).show()
    }

    private fun insertDateTime(item: TimeSheetItemEntity) {

        val dialogView: View = View.inflate(this, R.layout.date_time_picker, null)
        val alertDialog = android.app.AlertDialog.Builder(this).create()

        val button = dialogView.findViewById<Button>(R.id.date_time_set)
        val datePicker = dialogView.findViewById<DatePicker>(R.id.date_picker)
        val timePicker = dialogView.findViewById<TimePicker>(R.id.time_picker)

        button.setOnClickListener{

                item.startTime = LocalDateTime.of(
                    datePicker.year,
                    datePicker.month,
                    datePicker.dayOfMonth,
                    timePicker.hour,
                    timePicker.minute
                )
                alertDialog.dismiss()

        }
        alertDialog.setView(dialogView)
        alertDialog.show()

    }
}
