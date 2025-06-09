package com.example.memeforge.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface MemeDAO {

    @Query("SELECT * FROM StoredMeme")
    fun getAll(): Flow<List<StoredMeme>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemes(memes : List<StoredMeme>)

    @Query("DELETE FROM StoredMeme")
    suspend fun deleteAll()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'StoredMeme'")
    suspend fun resetAutoIncrement()
}