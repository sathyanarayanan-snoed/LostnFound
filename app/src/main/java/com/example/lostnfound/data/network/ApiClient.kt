package com.example.lostnfound.data.network

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

@Serializable
data class AuthRequest(
    val email: String,
    val password: String,
    val displayName: String = ""
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserInfo
)

@Serializable
data class UserInfo(
    val uid: String,
    val email: String,
    val displayName: String
)

@Serializable
data class ResetPasswordRequest(
    val email: String
)

@Serializable
data class UploadResponse(
    val url: String
)

@Serializable
data class UserItemsResponse(
    val found: List<com.example.lostnfound.domain.model.FoundItem> = emptyList(),
    val lost: List<com.example.lostnfound.domain.model.LostItem> = emptyList()
)

@Serializable
data class IdResponse(
    val id: String
)

class ApiClient(private val context: Context) {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun postJson(endpoint: String, bodyJson: String, token: String? = null): Result<String> = withContext(Dispatchers.IO) {
        try {
            val builder = Request.Builder()
                .url("${ApiConfig.BASE_URL}$endpoint")
                .post(bodyJson.toRequestBody(jsonMediaType))
            if (token != null) {
                builder.addHeader("Authorization", "Bearer $token")
            }
            val response = client.newCall(builder.build()).execute()
            val resBody = response.body?.string() ?: ""
            if (response.isSuccessful) {
                Result.success(resBody)
            } else {
                Result.failure(Exception(parseErrorMessage(resBody)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun get(endpoint: String, token: String? = null): Result<String> = withContext(Dispatchers.IO) {
        try {
            val builder = Request.Builder()
                .url("${ApiConfig.BASE_URL}$endpoint")
                .get()
            if (token != null) {
                builder.addHeader("Authorization", "Bearer $token")
            }
            val response = client.newCall(builder.build()).execute()
            val resBody = response.body?.string() ?: ""
            if (response.isSuccessful) {
                Result.success(resBody)
            } else {
                Result.failure(Exception(parseErrorMessage(resBody)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun put(endpoint: String, bodyJson: String = "{}", token: String? = null): Result<String> = withContext(Dispatchers.IO) {
        try {
            val builder = Request.Builder()
                .url("${ApiConfig.BASE_URL}$endpoint")
                .put(bodyJson.toRequestBody(jsonMediaType))
            if (token != null) {
                builder.addHeader("Authorization", "Bearer $token")
            }
            val response = client.newCall(builder.build()).execute()
            val resBody = response.body?.string() ?: ""
            if (response.isSuccessful) {
                Result.success(resBody)
            } else {
                Result.failure(Exception(parseErrorMessage(resBody)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun delete(endpoint: String, token: String? = null): Result<String> = withContext(Dispatchers.IO) {
        try {
            val builder = Request.Builder()
                .url("${ApiConfig.BASE_URL}$endpoint")
                .delete()
            if (token != null) {
                builder.addHeader("Authorization", "Bearer $token")
            }
            val response = client.newCall(builder.build()).execute()
            val resBody = response.body?.string() ?: ""
            if (response.isSuccessful) {
                Result.success(resBody)
            } else {
                Result.failure(Exception(parseErrorMessage(resBody)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadFile(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val file = copyUriToTempFile(uri)
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    file.name,
                    file.asRequestBody("image/*".toMediaType())
                )
                .build()

            val request = Request.Builder()
                .url("${ApiConfig.BASE_URL}/api/upload")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val resBody = response.body?.string() ?: ""
            file.delete()

            if (response.isSuccessful) {
                val uploadRes = json.decodeFromString<UploadResponse>(resBody)
                Result.success(uploadRes.url)
            } else {
                Result.failure(Exception(parseErrorMessage(resBody)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun copyUriToTempFile(uri: Uri): File {
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    private fun parseErrorMessage(jsonStr: String): String {
        return try {
            val map = json.decodeFromString<Map<String, String>>(jsonStr)
            map["error"] ?: map["message"] ?: jsonStr
        } catch (_: Exception) {
            jsonStr
        }
    }
}
