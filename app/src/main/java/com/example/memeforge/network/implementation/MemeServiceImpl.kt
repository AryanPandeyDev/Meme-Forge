package com.example.memeforge.network.implementation

import com.example.memeforge.network.memedata.Meme
import com.example.memeforge.network.memedata.MemeX
import com.example.memeforge.network.MemeService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get


class MemeServiceImpl (private val client: HttpClient) : MemeService {

    override suspend fun getMemes(): List<MemeX> {
        return try {
            val response: Meme =
                client.get("https://api.imgflip.com/get_memes").body()
            response.data.memes
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}