package com.example.lostnfound.data

import android.net.Uri
import com.example.lostnfound.data.network.ApiClient

class HttpStorageService(private val apiClient: ApiClient) : ImageStorageService {
    override suspend fun uploadImage(uri: Uri, folder: String): Result<String> {
        return apiClient.uploadFile(uri)
    }

    override suspend fun deleteImage(url: String): Result<Unit> {
        return Result.success(Unit)
    }
}
