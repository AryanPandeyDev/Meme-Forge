package com.example.memeforge.network.implementation

import com.example.memeforge.network.memedata.MemeX
import com.example.memeforge.network.MemeService

class MockMemeService : MemeService {
    override suspend fun getMemes(): List<MemeX> {
        return listOf(
            MemeX(
                id = "1",
                name = "Mock Meme 1",
                url = "https://example.com/meme1.jpg",
                width = 500,
                height = 500,
                boxCount = 2
            ),
            MemeX(
                id = "2",
                name = "Mock Meme 2",
                url = "https://example.com/meme2.jpg",
                width = 600,
                height = 400,
                boxCount = 2
            )
        )
    }
}
