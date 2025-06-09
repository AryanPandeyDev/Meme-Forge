package com.example.memeforge.viewmodels

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memeforge.filehandling.InternalStoragePhoto
import com.example.memeforge.filehandling.SaveOrDeleteImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavScreenViewModel @Inject constructor(private val app : Application) : ViewModel(){
    private val saveOrDeleteImage = SaveOrDeleteImage()
    private var _photos = MutableStateFlow<List<InternalStoragePhoto>>(emptyList())
    val photos : StateFlow<List<InternalStoragePhoto>>
        get() = _photos

    init {
        loadPhotosFromInternalStorage()
    }

    private fun loadPhotosFromInternalStorage(){
        viewModelScope.launch(Dispatchers.IO) {
            _photos.value = saveOrDeleteImage.loadPhotosFromInternalStorage(app)
        }
    }

    fun savePhotoToExternalStorage(bmp : Bitmap, displayName : String) : Boolean {
        return saveOrDeleteImage.savePhotoToExternalStorage(bmp,displayName,app)
    }

    fun deletePhotoFromInternalStorage(filename: String) : Boolean {
        val result = saveOrDeleteImage.deletePhotoFromInternalStorage(filename,app)
        loadPhotosFromInternalStorage()
        return result
    }
}