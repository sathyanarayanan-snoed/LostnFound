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

data class ReportFoundUiState(
    val finderName: String = "",
    val finderContact: String = "",
    val description: String = "",
    val placeFound: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val category: ItemCategory = ItemCategory.OTHER,
    val imageUri: Uri? = null,
    val currentStep: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSubmitted: Boolean = false,
    val isRateLimited: Boolean = false,
    val potentialMatches: List<LostItem> = emptyList(),
    val isSearchingMatches: Boolean = false
)

class ReportFoundViewModel(
    private val itemRepository: ItemRepository,
    private val authRepository: AuthRepository,
    private val storageService: ImageStorageService
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportFoundUiState())
    val uiState: StateFlow<ReportFoundUiState> = _uiState.asStateFlow()

    private var matchJob: Job? = null

    fun updateFinderName(name: String) {
        _uiState.value = _uiState.value.copy(finderName = name)
    }

    fun updateFinderContact(contact: String) {
        _uiState.value = _uiState.value.copy(finderContact = contact)
    }

    fun updateDescription(desc: String) {
        _uiState.value = _uiState.value.copy(description = desc)
        searchPotentialMatches()
    }

    fun updatePlaceFound(place: String) {
        _uiState.value = _uiState.value.copy(placeFound = place)
    }

    fun updateLocation(lat: Double, lon: Double) {
        _uiState.value = _uiState.value.copy(latitude = lat, longitude = lon)
    }

    fun updateCategory(category: ItemCategory) {
        _uiState.value = _uiState.value.copy(category = category)
        searchPotentialMatches()
    }

    fun updateImageUri(uri: Uri?) {
        _uiState.value = _uiState.value.copy(imageUri = uri)
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
            val result = itemRepository.searchLostItems(filters)
            _uiState.value = _uiState.value.copy(
                potentialMatches = result.getOrDefault(emptyList()),
                isSearchingMatches = false
            )
        }
    }

    fun nextStep() {
        if (_uiState.value.currentStep < 2) {
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
                val uploadResult = storageService.uploadImage(uri, "found_items")
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

            val item = FoundItem(
                reporterId = userId,
                finderName = _uiState.value.finderName,
                finderContact = _uiState.value.finderContact,
                placeFound = _uiState.value.placeFound,
                description = _uiState.value.description,
                category = _uiState.value.category.name,
                imageUrl = imageUrl,
                latitude = _uiState.value.latitude,
                longitude = _uiState.value.longitude
            )

            val result = itemRepository.addFoundItem(item)
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

    fun resetForm() {
        _uiState.value = ReportFoundUiState(
            finderName = _uiState.value.finderName,
            finderContact = _uiState.value.finderContact
        )
    }
}
