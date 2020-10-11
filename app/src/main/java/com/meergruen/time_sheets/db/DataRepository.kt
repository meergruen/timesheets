package com.meergruen.time_sheets.db

import com.meergruen.time_sheets.TimeSheetTask


class DataRepository private constructor(private val database: AppDatabase) {

    fun getTimeSheetItems(): List<TimeSheetItemEntity> {
        return database.timeSheetItemDao().getItems()

    }

    suspend fun saveTimeSheetItems(timeSheetItemEntities: List<TimeSheetItemEntity>) {
        database.timeSheetItemDao().insertAll(timeSheetItemEntities)
    }

    fun getTimeSheetTasks(): List<TimeSheetTask> {

        return getTimeSheetItems().groupBy { it.key() }.map { TimeSheetTask(it.value) }
    }


    companion object {
        private var sInstance: DataRepository? = null
        fun getInstance(database: AppDatabase): DataRepository? {
            if (sInstance == null) {
                synchronized(DataRepository::class.java) {
                    if (sInstance == null) {
                        sInstance = DataRepository(database)
                    }
                }
            }
            return sInstance
        }
    }

}
