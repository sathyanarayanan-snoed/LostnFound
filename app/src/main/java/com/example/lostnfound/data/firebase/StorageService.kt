package com.example.lostnfound.data.firebase

import android.net.Uri
import com.example.lostnfound.data.ImageStorageService
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StorageService : ImageStorageService {
    private val storage = FirebaseStorage.getInstance()

    override suspend fun uploadImage(uri: Uri, folder: String): Result<String> {
        return try {
            val filename = "${UUID.randomUUID()}.jpg"
            val ref = storage.reference.child("$folder/$filename")
            ref.putFile(uri).await()
            val downloadUrl = ref.downloadUrl.await()
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteImage(url: String): Result<Unit> {
        return try {
            val ref = storage.getReferenceFromUrl(url)
            ref.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
