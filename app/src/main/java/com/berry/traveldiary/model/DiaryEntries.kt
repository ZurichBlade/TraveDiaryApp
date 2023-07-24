package com.berry.traveldiary.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diaryEntry_table")
data class DiaryEntries(
    @PrimaryKey(autoGenerate = true)
    val entryId: Int,
    val title: String,
    val date: String,
    val location: String,
    val description: String
)
