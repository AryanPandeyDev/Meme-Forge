package com.example.memeforge.network

import com.example.memeforge.network.memedata.MemeX

interface MemeService {
    suspend fun getMemes() : List<MemeX>
}