package com.example.memeforge.network.memedata


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemeX(
    @SerialName("box_count")
    val boxCount: Int,
    @SerialName("height")
    val height: Int,
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("url")
    val url: String,
    @SerialName("width")
    val width: Int
)