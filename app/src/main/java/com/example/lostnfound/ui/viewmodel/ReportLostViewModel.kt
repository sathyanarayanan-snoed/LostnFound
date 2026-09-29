package com.example.lostnfound.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lostnfound.data.AuthRepository
import com.example.lostnfound.data.ImageStorageService
import com.example.lostnfound.data.ItemRepository
import com.example.lostnfound.domain.model.FoundItem
import com.example.lostnfound.domain.model.ItemCategory
import com.example.lostnfound.domain.model.LostItem
import com.example.lostnfound.domain.model.SearchFilters
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReportLostUiState(
    val ownerName: String = "",
    val ownerContact: String = "",
    val description: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val category: ItemCategory = ItemCategory.OTHER,
    val lostDate: Long = System.currentTimeMillis(),
    val imageUri: Uri? = null,
    val proofOfOwnership: String = "",
    val proofImageUri: Uri? = null,
    val currentStep: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSubmitted: Boolean = false,
    val isRateLimited: Boolean = false,
    val potentialMatches: List<FoundItem> = emptyList(),
    val isSearchingMatches: Boolean = false
)

class ReportLostViewModel(
    private val itemRepository: ItemRepository,
    private val authRepository: AuthRepository,
    private val storageService: ImageStorageService
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportLostUiState())
    val uiState: StateFlow<ReportLostUiState> = _uiState.asStateFlow()

    private var matchJob: Job? = null

    fun updateOwnerName(name: String) {
        _uiState.value = _uiState.value.copy(ownerName = name)
    }

    fun updateOwnerContact(contact: String) {
        _uiState.value = _uiState.value.copy(ownerContact = contact)
    }

    fun updateDescription(desc: String) {
        _uiState.value = _uiState.value.copy(description = desc)
        searchPotentialMatches()
    }

    fun updateLocation(lat: Double, lon: Double) {
        _uiState.value = _uiState.value.copy(latitude = lat, longitude = lon)
    }

    fun updateCategory(category: ItemCategory) {
        _uiState.value = _uiState.value.copy(category = category)
        searchPotentialMatches()
    }

    fun updateLostDate(date: Long) {
        _uiState.value = _uiState.value.copy(lostDate = date)
    }

    fun updateImageUri(uri: Uri?) {
        _uiState.value = _uiState.value.copy(imageUri = uri)
    }

    fun updateProofOfOwnership(proof: String) {
        _uiState.value = _uiState.value.copy(proofOfOwnership = proof)
    }

    fun updateProofImageUri(uri: Uri?) {
        _uiState.value = _uiState.value.copy(proofImageUri = uri)
    }

    private fun searchPotentialMatches() {
        matchJob?.cancel()
        val desc = _uiState.value.description
        if (desc.length < 3) return

        matchJob = viewModelScope.launch {
            delay(500)
            _uiState.value = _uiState.value.copy(isSearchingMatches = true)
            val filters = SearchFilters(
                query = desc,
                category = _uiState.value.category
            )
            val result = itemRepository.searchFoundItems(filters)
            _uiState.value = _uiState.value.copy(
                potentialMatches = result.getOrDefault(emptyList()),
                isSearchingMatches = false
            )
        }
    }

    fun nextStep() {
        if (_uiState.value.currentStep < 3) {
            _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep + 1)
        }
    }

    fun previousStep() {
        if (_uiState.value.currentStep > 0) {
            _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep - 1)
        }
    }

    fun submit() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch

            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            var imageUrl = ""
            val uri = _uiState.value.imageUri
            if (uri != null) {
                val uploadResult = storageService.uploadImage(uri, "lost_items")
                if (uploadResult.isSuccess) {
                    imageUrl = uploadResult.getOrDefault("")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to upload image"
                    )
                    return@launch
                }
            }

            var proofImageUrl = ""
            val proofUri = _uiState.value.proofImageUri
            if (proofUri != null) {
                val uploadResult = storageService.uploadImage(proofUri, "proof_images")
                if (uploadResult.isSuccess) {
                    proofImageUrl = uploadResult.getOrDefault("")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to upload proof image"
                    )
                    return@launch
                }
            }

            val item = LostItem(
                reporterId = userId,
                ownerName = _uiState.value.ownerName,
                ownerContact = _uiState.value.ownerContact,
                description = _uiState.value.description,
                category = _uiState.value.category.name,
                lostDate = _uiState.value.lostDate,
                imageUrl = imageUrl,
                proofOfOwnership = _uiState.value.proofOfOwnership,
                proofImageUrl = proofImageUrl,
                latitude = _uiState.value.latitude,
                longitude = _uiState.value.longitude
            )

            val result = itemRepository.addLostItem(item)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isSubmitted = result.isSuccess,
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null, isRateLimited = false)
    }
}
