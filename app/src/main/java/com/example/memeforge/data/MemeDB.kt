package com.example.memeforge.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StoredMeme::class], version = 1)
abstract class MemeDB : RoomDatabase() {
    abstract fun getMemeDAO() : MemeDAO
}