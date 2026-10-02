package com.example.lostnfound.ui.screens.report

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.lostnfound.domain.model.FoundItem
import com.example.lostnfound.domain.model.ItemCategory
import com.example.lostnfound.ui.components.AppTextField
import com.example.lostnfound.ui.components.CategoryChip
import com.example.lostnfound.ui.components.ImagePicker
import com.example.lostnfound.ui.components.LocationPicker
import com.example.lostnfound.ui.theme.AppElevation
import com.example.lostnfound.ui.theme.AppRadius
import com.example.lostnfound.ui.theme.AppSpacing
import com.example.lostnfound.ui.theme.PrimaryLight
import com.example.lostnfound.ui.viewmodel.ReportLostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportLostScreen(
    viewModel: ReportLostViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSubmitted) {
        if (uiState.isSubmitted) {
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Report Lost Item",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (uiState.currentStep > 0) viewModel.previousStep() else onBack()
                        },
                        modifier = Modifier.padding(start = AppSpacing.SM)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.LG)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.XL),
                    shape = RoundedCornerShape(AppRadius.LG),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(AppSpacing.MD),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.SM)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = when (uiState.currentStep) {
                                    0 -> "Step 1: Owner Details"
                                    1 -> "Step 2: Item & Location"
                                    2 -> "Step 3: Proof of Ownership"
                                    else -> "Step 4: Photo Verification"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${uiState.currentStep + 1} of 4",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        LinearProgressIndicator(
                            progress = { (uiState.currentStep + 1) / 4f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }

                AnimatedContent(
                    targetState = uiState.currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                        } else {
                            slideInHorizontally { -it } + fadeIn() togetherWith
                                    slideOutHorizontally { it } + fadeOut()
                        }
                    },
                    label = "stepTransition"
                ) { step ->
                    Column(
                        modifier = Modifier.padding(horizontal = AppSpacing.XL),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.XL)
                    ) {
                        when (step) {
                            0 -> OwnerStep(uiState, viewModel)
                            1 -> ItemStep(uiState, viewModel)
                            2 -> ProofStep(uiState, viewModel)
                            3 -> PhotoStep(uiState, viewModel)
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.XL, vertical = AppSpacing.LG),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.MD)
                ) {
                    if (uiState.currentStep > 0) {
                        OutlinedButton(
                            onClick = { viewModel.previousStep() },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            shape = RoundedCornerShape(AppRadius.LG),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Text(
                                text = "Back",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Button(
                        onClick = {
                            if (uiState.currentStep == 3) viewModel.submit() else viewModel.nextStep()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        shape = RoundedCornerShape(AppRadius.LG),
                        enabled = isNextEnabled(uiState),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (uiState.currentStep == 3) "Submit Lost Item" else "Next Step",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(AppSpacing.LG),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
private fun OwnerStep(
    uiState: com.example.lostnfound.ui.viewmodel.ReportLostUiState,
    viewModel: ReportLostViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.LG)) {
        Text(
            text = "Your Details",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Your identity and contact info allow finders and campus security to notify you immediately upon recovery.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        AppTextField(
            value = uiState.ownerName,
            onValueChange = { viewModel.updateOwnerName(it) },
            label = "Your Full Name",
            leadingIcon = {
                Icon(
                    Icons.Outlined.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
        AppTextField(
            value = uiState.ownerContact,
            onValueChange = { viewModel.updateOwnerContact(it) },
            label = "Contact Info (Email or Phone)",
            leadingIcon = {
                Icon(
                    Icons.Outlined.Mail,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
    }
}

@Composable
private fun ItemStep(
    uiState: com.example.lostnfound.ui.viewmodel.ReportLostUiState,
    viewModel: ReportLostViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.LG)) {
        Text(
            text = "Item Details",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        AppTextField(
            value = uiState.description,
            onValueChange = { viewModel.updateDescription(it) },
            label = "What did you lose? (e.g., Black Lenovo Laptop Bag)",
            leadingIcon = {
                Icon(
                    Icons.Outlined.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )

        Text(
            text = "Category",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(AppSpacing.SM)) {
            items(ItemCategory.entries) { category ->
                CategoryChip(
                    label = category.displayName,
                    selected = uiState.category == category,
                    onSelected = { viewModel.updateCategory(category) }
                )
            }
        }

        Text(
            text = "Last Seen Location",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        AppTextField(
            value = uiState.lastSeenLocation,
            onValueChange = { viewModel.updateLastSeenLocation(it) },
            label = "Describe Location (e.g., Main Block, Cafeteria)",
            leadingIcon = {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )

        Text(
            text = "Pin on Campus Map (Optional)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Surface(
            shape = RoundedCornerShape(AppRadius.LG),
            modifier = Modifier.clip(RoundedCornerShape(AppRadius.LG))
        ) {
            LocationPicker(
                onLocationSelected = { lat, lon -> viewModel.updateLocation(lat, lon) }
            )
        }

        if (uiState.potentialMatches.isNotEmpty()) {
            Text(
                text = "Recently Found Items Matching Your Description",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(AppSpacing.MD)) {
                items(uiState.potentialMatches) { item ->
                    MatchCard(item)
                }
            }
        }
    }
}

@Composable
private fun ProofStep(
    uiState: com.example.lostnfound.ui.viewmodel.ReportLostUiState,
    viewModel: ReportLostViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.LG)) {
        Text(
            text = "Proof of Ownership",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Surface(
            shape = RoundedCornerShape(AppRadius.LG),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(AppSpacing.MD),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.SM)
            ) {
                Icon(
                    Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = PrimaryLight,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "A specific identifier (serial number, private engraving, or receipt) prevents fraudulent claims and speeds up item release.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        AppTextField(
            value = uiState.proofOfOwnership,
            onValueChange = { viewModel.updateProofOfOwnership(it) },
            label = "Proof Details (Serial #, unique scratches, etc.)",
            leadingIcon = {
                Icon(
                    Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
        ImagePicker(
            imageUri = uiState.proofImageUri,
            onImageSelected = { viewModel.updateProofImageUri(it) },
            label = "Attach Proof Image / Receipt (Optional)"
        )
    }
}

@Composable
private fun PhotoStep(
    uiState: com.example.lostnfound.ui.viewmodel.ReportLostUiState,
    viewModel: ReportLostViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.LG)) {
        Text(
            text = "Reference Photograph",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "An existing photo of your item helps finders quickly recognize it around campus.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ImagePicker(
            imageUri = uiState.imageUri,
            onImageSelected = { viewModel.updateImageUri(it) },
            label = "Take or Upload Item Photo"
        )
    }
}

@Composable
private fun MatchCard(item: FoundItem) {
    Surface(
        modifier = Modifier.width(170.dp),
        shape = RoundedCornerShape(AppRadius.LG),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = AppElevation.Low
    ) {
        Column {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.description,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(105.dp)
                    .clip(RoundedCornerShape(topStart = AppRadius.LG, topEnd = AppRadius.LG)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(AppSpacing.SM)) {
                Text(
                    text = item.description,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(AppSpacing.XXS))
                Text(
                    text = "Found at: ${item.placeFound}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun isNextEnabled(uiState: com.example.lostnfound.ui.viewmodel.ReportLostUiState): Boolean {
    return when (uiState.currentStep) {
        0 -> uiState.ownerName.isNotBlank() && uiState.ownerContact.isNotBlank()
        1 -> uiState.description.isNotBlank() && uiState.lastSeenLocation.isNotBlank()
        2 -> uiState.proofOfOwnership.isNotBlank()
        else -> uiState.imageUri != null
    }
}
