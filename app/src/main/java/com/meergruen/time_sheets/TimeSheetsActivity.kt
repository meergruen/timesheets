package com.meergruen.time_sheets

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import java.util.*
import kotlin.collections.ArrayList



//import androidx.appcompat.widget.Toolbar
//import com.google.android.material.tabs.TabItem


class TimeSheetsActivity : AppCompatActivity() {

    private lateinit var mTabLayout: TabLayout
    private lateinit var mViewPager: ViewPager
    private lateinit var mPagerAdapter: CheesePagerAdapter

    private lateinit var sendFab: FloatingActionButton
    private lateinit var addFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_sheets)
       // val actionBar = actionBar // works?
       // actionBar!!.setDisplayHomeAsUpEnabled(true)

        // Retrieve the Toolbar from our content view, and set it as the action bar
       /* val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)*/

        addFab = findViewById(R.id.add_time_sheet)
        sendFab = findViewById(R.id.send_time_sheet)

        addFab.setOnClickListener { _ ->
            addRandomTab()
        }
        sendFab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }


        mTabLayout = findViewById(R.id.tabs)
        mViewPager = findViewById(R.id.tabs_viewpager)
        mPagerAdapter = CheesePagerAdapter()
        mViewPager.adapter = mPagerAdapter
        mViewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })
        mTabLayout.addOnTabSelectedListener(mTabListener)
        setupButtons()
        setupRadioGroup()
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btn_add_tab).setOnClickListener { addRandomTab() }
        findViewById<Button>(R.id.btn_remove_tab).setOnClickListener {
            if (mTabLayout.tabCount >= 1) {
                mTabLayout.removeTabAt(mTabLayout.tabCount - 1)
                mPagerAdapter.removeTab()
            }
        }
    }

    private fun addRandomTab() {
        val r = Random()
        val cheese: String = Cheeses.sCheeseStrings[r.nextInt(Cheeses.sCheeseStrings.size)]
        mTabLayout.addTab(mTabLayout.newTab().setText(cheese))
        mPagerAdapter.addTab(cheese)
    }

    private fun setupRadioGroup() {
        // Setup the initially checked item
        when (mTabLayout.tabMode) {
            TabLayout.MODE_SCROLLABLE -> (findViewById<RadioButton>(R.id.rb_tab_scrollable)).isChecked =
                true
            TabLayout.MODE_FIXED -> (findViewById<RadioButton>(R.id.rb_tab_fixed)).isChecked =
                true
        }
        var rg = findViewById<RadioGroup>(R.id.radiogroup_tab_mode)
        rg.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.rb_tab_fixed -> mTabLayout.tabMode = TabLayout.MODE_FIXED
                R.id.rb_tab_scrollable -> mTabLayout.tabMode = TabLayout.MODE_SCROLLABLE
            }
        }
        when (mTabLayout.tabGravity) {
            TabLayout.GRAVITY_CENTER -> (findViewById<RadioButton>(R.id.rb_tab_g_center)).isChecked =
                true
            TabLayout.GRAVITY_FILL -> (findViewById<RadioButton>(R.id.rb_tab_g_fill)).isChecked =
                true
        }
        rg = findViewById(R.id.radiogroup_tab_gravity)
        rg.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.rb_tab_g_center -> mTabLayout.tabGravity = TabLayout.GRAVITY_CENTER
                R.id.rb_tab_g_fill -> mTabLayout.tabGravity = TabLayout.GRAVITY_FILL
            }
        }
    }

    private val mTabListener: OnTabSelectedListener = object : OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            mViewPager.currentItem = tab.position
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            // no-op
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            // no-op
        }
    }

    private class CheesePagerAdapter : PagerAdapter() {
        private val mCheeses: ArrayList<CharSequence> = ArrayList()
        fun addTab(title: String) {
            mCheeses.add(title)
            notifyDataSetChanged()
        }

        fun removeTab() {
            if (mCheeses.isNotEmpty()) {
                mCheeses.removeAt(mCheeses.size - 1)
                notifyDataSetChanged()
            }
        }

        override fun getCount(): Int {
            return mCheeses.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val tv = TextView(container.context)
            tv.text = getPageTitle(position)
            tv.gravity = Gravity.CENTER
           // tv.setTextAppearance(tv.context, R.style.TextAppearance_AppCompat_Title)
            container.addView(
                tv, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            return tv
        }

        override fun isViewFromObject(
            view: View,
            `object`: Any
        ): Boolean {
            return view === `object`
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mCheeses[position]
        }

        override fun destroyItem(
            container: ViewGroup,
            position: Int,
            `object`: Any
        ) {
            container.removeView(`object` as View)
        }
    }
}
