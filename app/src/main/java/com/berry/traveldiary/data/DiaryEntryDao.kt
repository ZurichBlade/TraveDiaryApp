package com.berry.traveldiary.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.berry.traveldiary.model.DiaryEntries

@Dao
interface DiaryEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDiaryEntry(diaryEntries: DiaryEntries)

    @Query("SELECT * FROM diaryEntry_table ORDER BY entryId ASC")
    fun readAllData(): MutableList<DiaryEntries>

}