package com.example.memeforge.filehandling

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.AlertDialogDefaults
import io.ktor.util.Identity.decode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class SaveOrDeleteImage() {

    private val minSDK29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun savePhotoToExternalStorage(bmp : Bitmap, displayName : String,context: Context) : Boolean {
        val contentResolver = context.contentResolver
        val imageCollection = if (minSDK29) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH, bmp.width)
            put(MediaStore.Images.Media.HEIGHT, bmp.height)
        }

        return try {
            val uri = contentResolver.insert(imageCollection, contentValues)
                ?: throw IOException("Couldn't create MediaStore entry")

            contentResolver.openOutputStream(uri)?.use { outputStream ->
                if (!bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                    throw IOException("Couldn't save bitmap")
                }
            }

            Toast.makeText(context, "Image Saved", Toast.LENGTH_SHORT).show()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Couldn't save image", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun savePhotoToInternalStorage(filename: String, bmp: Bitmap,context: Context): Boolean {
        return try {
            context.openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream ->
                if(!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap.")
                }
            }
            Toast.makeText(context,"Image added to favourites",Toast.LENGTH_SHORT).show()
            true
        } catch(e: IOException) {
            e.printStackTrace()
            false
        }
    }

    suspend fun loadPhotosFromInternalStorage(context: Context): List<InternalStoragePhoto> {
        return withContext(Dispatchers.IO) {
            val files = context.filesDir.listFiles()
            //checked files size
            if (files != null) {
                Log.d("FilesList" , files.size.toString())
            }
            files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
                val bytes = it.readBytes()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                InternalStoragePhoto(it.name, bmp)
            } ?: listOf()
        }
    }

    fun deletePhotoFromInternalStorage(filename: String,context: Context): Boolean {
        return try {
            context.deleteFile(filename)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}