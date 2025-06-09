package com.example.memeforge.network.memedata


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("memes")
    val memes: List<MemeX>
)