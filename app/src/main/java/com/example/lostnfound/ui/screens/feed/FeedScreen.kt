package com.example.lostnfound.ui.screens.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lostnfound.domain.model.ItemType
import com.example.lostnfound.ui.components.EmptyStateView
import com.example.lostnfound.ui.theme.AppElevation
import com.example.lostnfound.ui.theme.AppRadius
import com.example.lostnfound.ui.theme.AppSpacing
import com.example.lostnfound.ui.viewmodel.FeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onItemClick: (String, Boolean) -> Unit,
    onReportFound: () -> Unit,
    onReportLost: () -> Unit,
    onProfileClick: () -> Unit,
    onSearchUsersClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }
    var showFabMenu by remember { mutableStateOf(false) }
    val tabs = listOf("All Items" to ItemType.ALL, "Lost" to ItemType.LOST, "Found" to ItemType.FOUND)

    LaunchedEffect(Unit) {
        viewModel.loadItems()
    }

    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                AnimatedVisibility(
                    visible = showFabMenu,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut()
                ) {
                    Column(
                        modifier = Modifier.padding(bottom = AppSpacing.LG),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.MD),
                        horizontalAlignment = Alignment.End
                    ) {
                        ExtendedFloatingActionButton(
                            text = { Text("Found Something", fontWeight = FontWeight.SemiBold) },
                            icon = { Icon(Icons.Default.Add, contentDescription = null) },
                            onClick = {
                                showFabMenu = false
                                onReportFound()
                            },
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            shape = RoundedCornerShape(AppRadius.XL),
                            elevation = FloatingActionButtonDefaults.elevation(AppElevation.Medium)
                        )
                        ExtendedFloatingActionButton(
                            text = { Text("Lost Something", fontWeight = FontWeight.SemiBold) },
                            icon = { Icon(Icons.Default.Add, contentDescription = null) },
                            onClick = {
                                showFabMenu = false
                                onReportLost()
                            },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            shape = RoundedCornerShape(AppRadius.XL),
                            elevation = FloatingActionButtonDefaults.elevation(AppElevation.Medium)
                        )
                    }
                }
                FloatingActionButton(
                    onClick = { showFabMenu = !showFabMenu },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(AppElevation.High)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Report Action",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = AppSpacing.XL, vertical = AppSpacing.LG)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Lost & Found Board",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "CIT Campus Community",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.SM),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onSearchUsersClick,
                                modifier = Modifier
                                    .size(44.dp)
                                    .shadow(AppElevation.Low, CircleShape)
                                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                            ) {
                                Icon(
                                    Icons.Outlined.PersonSearch,
                                    contentDescription = "Search Users",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            IconButton(
                                onClick = onProfileClick,
                                modifier = Modifier
                                    .size(44.dp)
                                    .shadow(AppElevation.Low, CircleShape)
                                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                            ) {
                                Icon(
                                    Icons.Outlined.Person,
                                    contentDescription = "Profile",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(AppSpacing.LG))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(AppElevation.Medium, RoundedCornerShape(AppRadius.Full)),
                        shape = RoundedCornerShape(AppRadius.Full),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = AppSpacing.SM, vertical = AppSpacing.XXS),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = uiState.searchFilters.query,
                                onValueChange = { viewModel.updateSearchQuery(it) },
                                placeholder = {
                                    Text(
                                        "Search items, descriptions, tags...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Search,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                trailingIcon = {
                                    if (uiState.searchFilters.query.isNotEmpty()) {
                                        IconButton(
                                            onClick = { viewModel.updateSearchQuery("") },
                                            modifier = Modifier.size(44.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.Close,
                                                contentDescription = "Clear search",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { showFilterSheet = true },
                                modifier = Modifier
                                    .padding(end = AppSpacing.XS)
                                    .size(44.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        RoundedCornerShape(AppRadius.LG)
                                    )
                            ) {
                                Icon(
                                    Icons.Outlined.FilterList,
                                    contentDescription = "Filters",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.XL, vertical = AppSpacing.SM),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.SM)
            ) {
                tabs.forEach { (title, type) ->
                    val isSelected = uiState.selectedTab == type
                    Surface(
                        onClick = { viewModel.selectTab(type) },
                        shape = RoundedCornerShape(AppRadius.LG),
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surface,
                        shadowElevation = if (isSelected) AppElevation.Low else AppElevation.Flat,
                        border = if (!isSelected) androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant
                        ) else null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = AppSpacing.MD),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.SM))

            if (uiState.searchFilters.query.isNotBlank() && !uiState.isLoading) {
                Text(
                    text = "${uiState.items.size} results found",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = AppSpacing.XL, vertical = AppSpacing.XS)
                )
            }

            PullToRefreshBox(
                isRefreshing = uiState.isLoading && uiState.items.isNotEmpty(),
                onRefresh = { viewModel.loadItems() },
                modifier = Modifier.fillMaxSize()
            ) {
                if (uiState.isLoading && uiState.items.isEmpty()) {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = AppSpacing.MD, vertical = AppSpacing.SM),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(4) {
                            ShimmerCard()
                        }
                    }
                } else if (uiState.items.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Outlined.SearchOff,
                        title = "No Items Found",
                        subtitle = "Try adjusting your search query, location filter, or switching between tabs.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = AppSpacing.MD, vertical = AppSpacing.SM),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.items,
                            key = { it.id }
                        ) { item ->
                            ItemCard(
                                item = item,
                                onClick = { onItemClick(item.id, item.isLostItem) }
                            )
                        }
                    }
                }

                if (uiState.isLoading && uiState.items.isNotEmpty()) {
                    Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(AppSpacing.LG),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        FilterSheet(
            currentFilters = uiState.searchFilters,
            onApply = { filters ->
                viewModel.applyFilters(filters)
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }
}
