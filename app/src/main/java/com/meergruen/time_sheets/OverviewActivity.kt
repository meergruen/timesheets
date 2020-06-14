package com.meergruen.time_sheets

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

class OverviewActivity : AppCompatActivity() {

    private lateinit var itemAdapter: TimeSheetItemArrayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)
        //setSupportActionBar(findViewById(R.id.toolbar_overview))

        itemAdapter = TimeSheetItemArrayAdapter(this, ArrayList<TimeSheetItem>()) // TODO: use real items

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
            R.id.action_time_sheets -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
