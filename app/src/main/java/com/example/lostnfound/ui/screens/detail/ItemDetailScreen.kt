package com.example.lostnfound.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContactPhone
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.lostnfound.ui.components.AppButton
import com.example.lostnfound.ui.components.LocationPicker
import com.example.lostnfound.ui.theme.AccentGreen
import com.example.lostnfound.ui.theme.PrimaryLight
import com.example.lostnfound.ui.theme.SecondaryLight
import com.example.lostnfound.ui.theme.WarningAmber
import com.example.lostnfound.ui.viewmodel.ItemDetailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ItemDetailScreen(
    itemId: String,
    isLostItem: Boolean,
    viewModel: ItemDetailViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(itemId) {
        viewModel.loadItem(itemId, isLostItem)
    }

    if (uiState.claimSuccess) {
        LaunchedEffect(Unit) { onBack() }
    }

    if (uiState.isLoading && uiState.foundItem == null && uiState.lostItem == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val imageUrl = if (isLostItem) uiState.lostItem?.imageUrl.orEmpty() else uiState.foundItem?.imageUrl.orEmpty()
    val description = if (isLostItem) uiState.lostItem?.description.orEmpty() else uiState.foundItem?.description.orEmpty()
    val category = if (isLostItem) uiState.lostItem?.category.orEmpty() else uiState.foundItem?.category.orEmpty()
    val reportedAt = if (isLostItem) uiState.lostItem?.reportedAt ?: 0L else uiState.foundItem?.reportedAt ?: 0L
    val status = if (isLostItem) uiState.lostItem?.status.orEmpty() else uiState.foundItem?.status.orEmpty()
    val lat = if (isLostItem) uiState.lostItem?.latitude else uiState.foundItem?.latitude
    val lon = if (isLostItem) uiState.lostItem?.longitude else uiState.foundItem?.longitude
    val contact = if (isLostItem) uiState.lostItem?.ownerContact.orEmpty() else uiState.foundItem?.finderContact.orEmpty()
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
        ) {
            if (imageUrl.isNotBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = description,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = if (isLostItem)
                                    listOf(SecondaryLight, SecondaryLight.copy(alpha = 0.5f))
                                else
                                    listOf(PrimaryLight, PrimaryLight.copy(alpha = 0.5f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isLostItem) "LOST" else "FOUND",
                        style = MaterialTheme.typography.displayLarge,
                        color = Color.White.copy(alpha = 0.35f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.55f), Color.Transparent)
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                        )
                    )
            )

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .size(44.dp)
                    .shadow(8.dp, CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                shape = RoundedCornerShape(14.dp),
                color = if (isLostItem) SecondaryLight else AccentGreen,
                shadowElevation = 6.dp
            ) {
                Text(
                    text = if (isLostItem) "Lost Item" else "Found Item",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 28.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (status == "claimed") {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AccentGreen.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentGreen.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Outlined.CheckCircle, "Claimed", tint = AccentGreen)
                        Text(
                            text = "This item has been successfully claimed",
                            color = AccentGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (isLostItem && uiState.lostItem?.flagged == true) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = WarningAmber.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Outlined.Warning, "Flagged", tint = WarningAmber)
                        Text(
                            text = "Under review — proof of ownership verification pending",
                            color = WarningAmber,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DetailRow(Icons.Outlined.Category, "Category", category.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() })
                DetailRow(Icons.Outlined.CalendarToday, "Reported Date", dateFormatter.format(Date(reportedAt)))

                if (!isLostItem && uiState.foundItem != null) {
                    DetailRow(Icons.Outlined.Person, "Found By", uiState.foundItem!!.finderName)
                    DetailRow(Icons.Outlined.LocationOn, "Location Found", uiState.foundItem!!.placeFound)
                    if (contact.isNotBlank()) {
                        DetailRow(Icons.Outlined.ContactPhone, "Contact Details", contact)
                    }
                }

                if (isLostItem && uiState.lostItem != null) {
                    DetailRow(Icons.Outlined.Person, "Reported By", uiState.lostItem!!.ownerName)
                    DetailRow(Icons.Outlined.CalendarToday, "Date Lost", dateFormatter.format(Date(uiState.lostItem!!.lostDate)))
                    if (contact.isNotBlank()) {
                        DetailRow(Icons.Outlined.ContactPhone, "Owner Contact", contact)
                    }
                    if (uiState.isOwner) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 3.dp,
                            border = androidx.compose.foundation.BorderStroke(
                                0.75.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Outlined.Shield,
                                        contentDescription = null,
                                        tint = PrimaryLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Proof of Ownership",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = uiState.lostItem!!.proofOfOwnership,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            if (lat != null && lon != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Campus Map Location",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        0.75.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.clip(RoundedCornerShape(18.dp))
                ) {
                    LocationPicker(
                        onLocationSelected = { _, _ -> },
                        initialLatitude = lat,
                        initialLongitude = lon,
                        isReadOnly = true
                    )
                }
            }

            if (uiState.isOwner && status == "active") {
                Spacer(modifier = Modifier.height(8.dp))
                AppButton(
                    text = "Mark as Claimed",
                    onClick = { viewModel.markAsClaimed(itemId) },
                    isLoading = uiState.isLoading,
                    containerColor = AccentGreen
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    if (value.isBlank()) return
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            0.75.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = label,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
