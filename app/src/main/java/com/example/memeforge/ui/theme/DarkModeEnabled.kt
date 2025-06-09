package com.example.memeforge.ui.theme

import androidx.lifecycle.ViewModel
import com.example.memeforge.datastore.UserPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DarkModeEnabled @Inject constructor(private val userPreference: UserPreference) : ViewModel() {
    val darkModeEnabled = userPreference.darkModeEnabled
}