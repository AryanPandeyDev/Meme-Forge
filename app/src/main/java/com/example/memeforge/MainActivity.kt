package com.example.memeforge

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.memeforge.navigation.Navigation
import com.example.memeforge.ui.theme.DarkModeEnabled
import com.example.memeforge.ui.theme.MemeForgeTheme
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateOrRequestPermission(this)
        enableEdgeToEdge()
        setContent {
            val darkModeEnabled = hiltViewModel<DarkModeEnabled>()
            MemeForgeTheme(
                darkTheme = darkModeEnabled.darkModeEnabled.collectAsState(true).value
            ) {
                Navigation()
            }
        }
    }
}


fun updateOrRequestPermission(activity: Activity) {
    val hasWritePermission = ContextCompat.checkSelfPermission(
        activity,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    if (!hasWritePermission) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1001
        )
    }
}


fun requestNotificationPermission(context : Context,notificationPermissionLauncher : ManagedActivityResultLauncher<String, Boolean>,toggleNotification: (Boolean) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Launch permission request
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // Permission already granted
            toggleNotification(true)
        }
    } else {
        // No permission needed for older versions
        toggleNotification(true)
    }
}

