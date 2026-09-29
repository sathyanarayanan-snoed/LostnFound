package com.example.lostnfound.ui.main

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * Main screen of the application.
 * Currently a placeholder to resolve build errors in tests.
 */
@Composable
fun MainScreen(items: List<String>) {
    LazyColumn {
        items(items) { item ->
            Text("Hello $item!")
        }
    }
}
