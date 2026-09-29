package com.example.lostnfound

import android.app.Application
import com.example.lostnfound.data.AuthRepository
import com.example.lostnfound.data.HttpAuthRepository
import com.example.lostnfound.data.HttpItemRepository
import com.example.lostnfound.data.HttpStorageService
import com.example.lostnfound.data.ImageStorageService
import com.example.lostnfound.data.ItemRepository
import com.example.lostnfound.data.network.ApiClient

class LostnFoundApplication : Application() {

    lateinit var authRepository: AuthRepository
    lateinit var itemRepository: ItemRepository
    lateinit var storageService: ImageStorageService

    override fun onCreate() {
        super.onCreate()
        val apiClient = ApiClient(this)
        val httpAuth = HttpAuthRepository(this, apiClient)
        authRepository = httpAuth
        itemRepository = HttpItemRepository(apiClient, httpAuth)
        storageService = HttpStorageService(apiClient)
    }
}
