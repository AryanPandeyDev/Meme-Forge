package com.example.memeforge.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.memeforge.data.MemeDAO
import com.example.memeforge.data.MemeDB
import com.example.memeforge.data.StoredMeme
import com.example.memeforge.data.StoredMemeRepo
import com.example.memeforge.network.MemeService
import com.example.memeforge.network.implementation.MemeServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun providesMemeDB( @ApplicationContext context: Context) : MemeDB {
        return Room.databaseBuilder(
            context,
            MemeDB::class.java,
            "Meme_DataBase"
        ).build()
    }

    @Provides
    @Singleton
    fun createMemeRepo(memeDB: MemeDB): MemeDAO {
        val memeRepo = StoredMemeRepo(memeDB.getMemeDAO())
        return memeRepo
    }


    @Provides
    @Singleton
    fun providesService() : HttpClient {
        return HttpClient(Android) {

            install(HttpTimeout) {
                requestTimeoutMillis = 10000
                connectTimeoutMillis = 10000
                socketTimeoutMillis = 10000
            }


            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }

            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("Message" , message)
                    }
                }
            }

            install(ResponseObserver) {
                onResponse {
                    Log.i("Response", it.status.value.toString())
                }
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }
        }
    }

    @Provides
    @Singleton
    fun providesMemeServiceImpl(client : HttpClient) : MemeService {
        return MemeServiceImpl(client)
    }

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        ) {
            context.preferencesDataStoreFile("user_prefs")
        }
    }

}