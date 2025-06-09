package com.example.memeforge.network.memedata


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Meme(
    @SerialName("data")
    val `data`: Data,
    @SerialName("success")
    val success: Boolean
)