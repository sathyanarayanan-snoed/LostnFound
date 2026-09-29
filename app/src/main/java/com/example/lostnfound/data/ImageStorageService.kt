package com.example.lostnfound.data

import android.net.Uri

interface ImageStorageService {
    suspend fun uploadImage(uri: Uri, folder: String = "general"): Result<String>
    suspend fun deleteImage(url: String): Result<Unit>
}
