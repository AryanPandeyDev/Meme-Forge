package com.example.memeforge.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class StoredMemeRepo (
    private val memeDAO : MemeDAO
) : MemeDAO {

    override fun getAll(): Flow<List<StoredMeme>> {
        return memeDAO.getAll()
    }

    override suspend fun insertMemes(memes : List<StoredMeme>) {
        return memeDAO.insertMemes(memes)
    }

    override suspend fun deleteAll() {
        return memeDAO.deleteAll()
    }

    override suspend fun resetAutoIncrement() {
        return memeDAO.resetAutoIncrement()
    }
}