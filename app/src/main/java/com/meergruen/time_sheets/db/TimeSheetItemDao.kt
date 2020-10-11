package com.meergruen.time_sheets.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TimeSheetItemDao {
    @Query("SELECT * FROM time_sheet_items")
    fun getItems(): List<TimeSheetItemEntity>

    @Insert
    suspend fun insertAll(timeSheetItemEntities: List<TimeSheetItemEntity>)
}
