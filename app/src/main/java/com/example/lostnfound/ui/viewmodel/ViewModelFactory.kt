package com.example.lostnfound.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lostnfound.data.AuthRepository
import com.example.lostnfound.data.ImageStorageService
import com.example.lostnfound.data.ItemRepository

class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val itemRepository: ItemRepository,
    private val storageService: ImageStorageService
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(authRepository) as T
            modelClass.isAssignableFrom(FeedViewModel::class.java) -> FeedViewModel(itemRepository) as T
            modelClass.isAssignableFrom(ItemDetailViewModel::class.java) -> ItemDetailViewModel(itemRepository, authRepository) as T
            modelClass.isAssignableFrom(ReportFoundViewModel::class.java) -> ReportFoundViewModel(itemRepository, authRepository, storageService) as T
            modelClass.isAssignableFrom(ReportLostViewModel::class.java) -> ReportLostViewModel(itemRepository, authRepository, storageService) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
