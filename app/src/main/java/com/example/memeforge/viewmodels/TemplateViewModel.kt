package com.example.memeforge.viewmodels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.memeforge.data.MemeDAO
import com.example.memeforge.data.StoredMeme
import com.example.memeforge.data.StoredMemeRepo
import com.example.memeforge.datastore.UserPreference
import com.example.memeforge.network.memedata.MemeX
import com.example.memeforge.network.MemeService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.Permissions
import javax.inject.Inject

@HiltViewModel
class TemplateViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val memeService : MemeService,
    private val memeRepo: MemeDAO,
    private val userPreference : UserPreference
) : ViewModel(){

    val notificationEnabled = userPreference.notificationsEnabled
    val darkModeEnabled = userPreference.darkModeEnabled



    fun toggleNotifications(enabled : Boolean) {
        Log.d("NotificationValue",enabled.toString())
        if (enabled && ContextCompat.checkSelfPermission(context,Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(context,"Please grant notification permissions",Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch {
            userPreference.setNotificationsEnabled(enabled)
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreference.setDarkModeEnabled(enabled)
        }
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading : StateFlow<Boolean> = _isLoading

    private val _memes = MutableStateFlow<List<StoredMeme>>(emptyList())
    val memes: StateFlow<List<StoredMeme>> = _memes


    init {
        getMemes()
    }

    fun setLoadingFalse() {
        _isLoading.value = false
    }

    private fun getMemes() {
        viewModelScope.launch(Dispatchers.IO) {
            if (getAllMemeFromStorage().first().isNotEmpty()) {
                getAllMemeFromStorage().collect {
                    _memes.value = it
                }
            } else {
                memeRepo.insertMemes(
                    memeService.getMemes().map{
                        StoredMeme(
                            url = it.url,
                            name = it.name,
                            height = it.height,
                            width = it.width,
                            boxCount = it.boxCount,
                        )
                    }
                )
                getAllMemeFromStorage().collect {
                    _memes.value = it
                }
            }
        }
    }

    fun updateMemes() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            deleteAllMemes()
            resetAutoIncrement()
            memeRepo.insertMemes(
                memeService.getMemes().map{
                    StoredMeme(
                        url = it.url,
                        name = it.name,
                        height = it.height,
                        width = it.width,
                        boxCount = it.boxCount,
                    )
                }
            )
            _isLoading.value = false
            getAllMemeFromStorage().collect {
                _memes.value = it
            }
        }
    }

    private fun getAllMemeFromStorage() : Flow<List<StoredMeme>> {
        return memeRepo.getAll()
    }

    private suspend fun deleteAllMemes() {
        memeRepo.deleteAll()
    }

    private suspend fun resetAutoIncrement() {
        memeRepo.resetAutoIncrement()
    }

    fun search(query: String): List<StoredMeme>? {
        val q = query.replace("","")
        val l = mutableListOf<StoredMeme>()
        for (meme in memes.value) {
            if (meme.name.replace(" ","").startsWith(q,ignoreCase = true)) {
                l.add(meme)
                continue
            }
            for (it in meme.name.split(" ",",")) {
                if (it.startsWith(query,ignoreCase = true)) {
                    l.add(meme)
                    break
                }
            }
        }
        return if (l.isEmpty()) null else l.toList()
    }
}