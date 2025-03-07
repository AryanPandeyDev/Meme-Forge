package com.example.memeforge.navigation

import kotlinx.serialization.Serializable


sealed interface Route {
    @Serializable
    data object LoginScreen : Route
    @Serializable
    data object TemplateScreen : Route
}