package com.example.memeforge.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StoredMeme(
    val url : String,
    val name : String,
    val height : Int,
    val width : Int,
    val boxCount : Int,
    @PrimaryKey(autoGenerate = true)
    val memeId : Int = 0
)
