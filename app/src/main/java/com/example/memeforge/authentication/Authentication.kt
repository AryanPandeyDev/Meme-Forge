package com.example.memeforge.authentication


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import com.example.memeforge.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Authentication(
    private val context: Context,
) {
    private val tag = "GoogleAuthClient: "

    private val _signInError = MutableStateFlow(false)
    val signInError = _signInError.asStateFlow()

    private val _imageFlow : MutableStateFlow<String?> = MutableStateFlow(null)
    val imageFlow = _imageFlow.asStateFlow()

    private val credentialManager = CredentialManager.create(context)
    val firebaseAuth = FirebaseAuth.getInstance()

    fun isSingedIn(): Boolean {
        if (firebaseAuth.currentUser != null) {
            Log.d(tag,"already signed in")
            return true
        }
        return false
    }

    suspend fun signIn(): Boolean {
        if (isSingedIn()) {
            return true
        }

        try {
            val result = buildCredentialRequest()
            return handleSingIn(result)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e

            println(tag + "sinIn error: ${e.message}")
            return false
        }
    }

    private suspend fun handleSingIn(result: GetCredentialResponse): Boolean {
        val credential = result.credential

        if (
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {

            try {

                val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                println(tag + "name: ${tokenCredential.displayName}")
                println(tag + "email: ${tokenCredential.id}")
                println(tag + "image: ${tokenCredential.profilePictureUri}")
                _imageFlow.value = tokenCredential.profilePictureUri.toString()

                val authCredential = GoogleAuthProvider.getCredential(
                    tokenCredential.idToken, null
                )
                val authResult = firebaseAuth.signInWithCredential(authCredential).await()

                return authResult.user != null

            } catch (e: GoogleIdTokenParsingException) {
                println(tag + "GoogleIdTokenParsingException: ${e.message}")
                return false
            }

        } else {
            println(tag + "credential is not GoogleIdTokenCredential")
            return false
        }

    }

    private suspend fun buildCredentialRequest(): GetCredentialResponse {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetSignInWithGoogleOption.Builder(
                    serverClientId = context.getString(R.string.WEB_CLIENT_ID),
                ).build()
            )
            .build()

        return credentialManager.getCredential(
            request = request, context = context
        )
    }

    suspend fun signOut() {
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        firebaseAuth.signOut()
        Log.i(tag,"Sucessfully signed out")
    }

    suspend fun register(
        email: String, password: String
    ): Boolean {
        try {

            val result = suspendCoroutine { continuation ->

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        println(tag + "register success")
                        CoroutineScope(Dispatchers.IO).launch {
                            continuation.resume(login(email, password))
                        }
                    }
                    .addOnFailureListener {
                        println(tag + "register failure ${it.message}")
                        _signInError.value = true
                        Toast.makeText(context,it.message,Toast.LENGTH_SHORT).show()
                        continuation.resume(false)
                    }

            }

            return result

        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            println(tag + "register exception ${e.message}")
            return false
        }
    }

    suspend fun login(
        email: String, password: String
    ): Boolean {
        try {

            val result = suspendCoroutine { continuation ->
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        println(tag + "login success")
                        continuation.resume(true)
                    }
                    .addOnFailureListener {
                        println(tag + "login failure ${it.message}")
                        _signInError.value = true
                        Toast.makeText(context,"Incorrect email or password",Toast.LENGTH_SHORT).show()
                        continuation.resume(false)
                    }
            }

            return result

        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            println(tag + "login exception ${e.message}")
            return false
        }
    }

}