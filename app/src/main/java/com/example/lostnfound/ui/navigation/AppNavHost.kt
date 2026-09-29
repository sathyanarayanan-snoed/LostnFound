package com.example.lostnfound.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.lostnfound.LostnFoundApplication
import com.example.lostnfound.ui.screens.admin.AdminPanelScreen
import com.example.lostnfound.ui.screens.auth.LoginScreen
import com.example.lostnfound.ui.screens.detail.ItemDetailScreen
import com.example.lostnfound.ui.screens.feed.FeedScreen
import com.example.lostnfound.ui.screens.profile.ProfileScreen
import com.example.lostnfound.ui.screens.report.ReportFoundScreen
import com.example.lostnfound.ui.screens.report.ReportLostScreen
import com.example.lostnfound.ui.screens.report.SuccessScreen
import com.example.lostnfound.ui.screens.search.UserSearchScreen
import com.example.lostnfound.ui.viewmodel.AdminViewModel
import com.example.lostnfound.ui.viewmodel.AuthViewModel
import com.example.lostnfound.ui.viewmodel.FeedViewModel
import com.example.lostnfound.ui.viewmodel.ItemDetailViewModel
import com.example.lostnfound.ui.viewmodel.ReportFoundViewModel
import com.example.lostnfound.ui.viewmodel.ReportLostViewModel
import com.example.lostnfound.ui.viewmodel.UserSearchViewModel
import com.example.lostnfound.ui.viewmodel.ViewModelFactory

@Composable
fun AppNavHost() {
    val context = LocalContext.current
    val app = context.applicationContext as LostnFoundApplication
    val factory = remember { ViewModelFactory(app.authRepository, app.itemRepository, app.storageService) }

    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val authState by authViewModel.uiState.collectAsState()
    val startRoute = if (authState.isAuthenticated) FeedRoute else LoginRoute
    val backStack = rememberNavBackStack(startRoute)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<LoginRoute> {
                LoginScreen(
                    viewModel = authViewModel,
                    onAuthenticated = {
                        if (backStack.lastOrNull() == LoginRoute) {
                            backStack.clear()
                            backStack.add(FeedRoute)
                        }
                    }
                )
            }
            entry<FeedRoute> {
                val feedViewModel: FeedViewModel = viewModel(factory = factory)
                FeedScreen(
                    viewModel = feedViewModel,
                    onItemClick = { itemId, isLostItem ->
                        backStack.add(ItemDetailRoute(itemId, isLostItem))
                    },
                    onReportFound = { backStack.add(ReportFoundRoute) },
                    onReportLost = { backStack.add(ReportLostRoute) },
                    onProfileClick = { backStack.add(ProfileRoute) },
                    onSearchUsersClick = { backStack.add(UserSearchRoute) }
                )
            }
            entry<ItemDetailRoute> { route ->
                val detailViewModel: ItemDetailViewModel = viewModel(factory = factory)
                ItemDetailScreen(
                    itemId = route.itemId,
                    isLostItem = route.isLostItem,
                    viewModel = detailViewModel,
                    onBack = { backStack.removeLastOrNull() }
                )
            }
            entry<ReportFoundRoute> {
                val reportFoundViewModel: ReportFoundViewModel = viewModel(factory = factory)
                ReportFoundScreen(
                    viewModel = reportFoundViewModel,
                    onBack = { backStack.removeLastOrNull() },
                    onSuccess = {
                        backStack.removeLastOrNull()
                        backStack.add(SuccessRoute(isLostItem = false))
                    }
                )
            }
            entry<ReportLostRoute> {
                val reportLostViewModel: ReportLostViewModel = viewModel(factory = factory)
                ReportLostScreen(
                    viewModel = reportLostViewModel,
                    onBack = { backStack.removeLastOrNull() },
                    onSuccess = {
                        backStack.removeLastOrNull()
                        backStack.add(SuccessRoute(isLostItem = true))
                    }
                )
            }
            entry<SuccessRoute> { route ->
                SuccessScreen(
                    isLostItem = route.isLostItem,
                    onViewPosts = {
                        backStack.removeLastOrNull()
                        backStack.add(ProfileRoute)
                    },
                    onBackToHome = {
                        backStack.clear()
                        backStack.add(FeedRoute)
                    }
                )
            }
            entry<ProfileRoute> {
                ProfileScreen(
                    viewModel = authViewModel,
                    itemRepository = app.itemRepository,
                    onBack = { backStack.removeLastOrNull() },
                    onSignOut = {
                        backStack.clear()
                        backStack.add(LoginRoute)
                    },
                    onAdminClick = { backStack.add(AdminPanelRoute) }
                )
            }
            entry<UserSearchRoute> {
                val userSearchViewModel: UserSearchViewModel = viewModel(factory = factory)
                UserSearchScreen(
                    viewModel = userSearchViewModel,
                    onBack = { backStack.removeLastOrNull() }
                )
            }
            entry<AdminPanelRoute> {
                val adminViewModel: AdminViewModel = viewModel(factory = factory)
                AdminPanelScreen(
                    viewModel = adminViewModel,
                    onBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
