package com.example.lostnfound.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute : NavKey

@Serializable
data object FeedRoute : NavKey

@Serializable
data class ItemDetailRoute(val itemId: String, val isLostItem: Boolean) : NavKey

@Serializable
data object ReportFoundRoute : NavKey

@Serializable
data object ReportLostRoute : NavKey

@Serializable
data object ProfileRoute : NavKey

@Serializable
data class SuccessRoute(val isLostItem: Boolean) : NavKey

@Serializable
data object UserSearchRoute : NavKey

@Serializable
data object AdminPanelRoute : NavKey
