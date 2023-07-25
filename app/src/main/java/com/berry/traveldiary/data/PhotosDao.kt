package com.berry.traveldiary.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.berry.traveldiary.model.Photos

@Dao
interface PhotosDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPhotoData(photos: Photos)

    @Query("SELECT * FROM photos_table ORDER BY photoId ASC")
    fun readAllData(): MutableList<Photos>

    @Delete
    suspend fun delete(photos: Photos)

//
//    @Query("Select * from diaryEntry_table where title like  :desc")
//    fun getSearchResults(desc: String): MutableList<DiaryEntries>

}