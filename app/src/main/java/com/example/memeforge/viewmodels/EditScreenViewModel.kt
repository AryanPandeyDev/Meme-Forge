package com.example.memeforge.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.memeforge.BuildConfig
import com.example.memeforge.colordiscription.ColorDescription
import com.example.memeforge.filehandling.SaveOrDeleteImage
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Stack

class EditScreenViewModel : ViewModel() {

    private var _response = MutableStateFlow<String?>(null)
    val response : StateFlow<String?> = _response

    var captionLoading = MutableStateFlow(false)

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash-lite",
        apiKey = BuildConfig.API_KEY
    )

    init {
        Log.d("api_key",BuildConfig.API_KEY)
    }

    fun resetState() {
        _response.value = null
        captionLoading.value = false

    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream).also {
                inputStream?.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getCaptionFromAI(prompt : String,memeName : String) {
        viewModelScope.launch(Dispatchers.IO) {
            _response.value = generativeModel.generateContent(prompt + "for the meme $memeName and reply with only 1 caption").text
            captionLoading.value = false
            _response.value?.let{
                Log.d("GeminiResponse",it)
            }
        }
    }

    private val saveOrDeleteImage = SaveOrDeleteImage()
    var addedToFavourite = mutableStateOf(false)
    var captionState =  mutableStateOf("")
    val imageStack = Stack<Bitmap>()


    val colors = listOf(
        ColorDescription(Color.Black, selected = mutableStateOf(true)),
        ColorDescription(Color.White, selected = mutableStateOf(false)),
        ColorDescription(Color.Red, selected = mutableStateOf(false)),
        ColorDescription(Color.Blue.copy(0.4f), selected = mutableStateOf(false)),
        ColorDescription(Color.Green, selected = mutableStateOf(false)),
        ColorDescription(Color.Gray, selected = mutableStateOf(false)),
        ColorDescription(Color(0xFFFFA500), selected = mutableStateOf(false)), // Orange
        ColorDescription(Color(0xFF800080), selected = mutableStateOf(false)), // Purple
        ColorDescription(Color(0xFFFFD700), selected = mutableStateOf(false)), // Gold
        ColorDescription(Color(0xFF00FFFF), selected = mutableStateOf(false)), // Cyan
        ColorDescription(Color(0xFFFF1493), selected = mutableStateOf(false)), // Deep Pink
        ColorDescription(Color(0xFF4B0082), selected = mutableStateOf(false))  // Indigo
    )



    fun savePhotoToExternalStorage(bmp : Bitmap, displayName : String,context: Context) : Boolean {
        return saveOrDeleteImage.savePhotoToExternalStorage(bmp,displayName,context)
    }

    fun savePhotoToInternalStorage(filename : String, bmp : Bitmap,context: Context) : Boolean {
        return saveOrDeleteImage.savePhotoToInternalStorage(filename,bmp,context)
    }


    suspend fun createImage(url : String,context : Context) : Bitmap {
        val loading = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(url)
            .build()
        val result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }


    fun drawTextOnBitmap(
        bitmap: Bitmap,
        text: String,
        position: Offset,
        textSizePx: Float,
        captionColor: Color
    ): Bitmap {
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = captionColor.toArgb()
            textSize = textSizePx
            typeface = Typeface.DEFAULT_BOLD
        }

        // Get text metrics
        val metrics = paint.fontMetrics
        val lineHeight = metrics.descent - metrics.ascent
        var currentY = position.y

        // Split text by newlines and draw each line
        text.split("\n").forEach { line ->
            val baseline = -metrics.ascent // Reset baseline for each line
            canvas.drawText(line, position.x, currentY + baseline, paint)
            currentY += lineHeight // Move down for next line
        }

        return result
    }


}