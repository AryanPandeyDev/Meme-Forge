package com.example.memeforge.navigation

import android.net.Uri
import kotlinx.serialization.Serializable


sealed interface Route {
    @Serializable
    data object WelcomeScreen : Route
    @Serializable
    data object LoginScreen : Route
    @Serializable
    data object SignUpScreen : Route
    @Serializable
    data object TemplateScreen : Route
    @Serializable
    data class EditMemeScreen(val imageFromDevice : String? = null, val url : String, val imageName : String) : Route
    @Serializable
    data object FavMemeScreen : Route
}