package com.copsiitbhu.agenticbank.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.copsiitbhu.agenticbank.ui.screens.HomeScreen
import com.copsiitbhu.agenticbank.ui.screens.SuccessScreen
import com.copsiitbhu.agenticbank.ui.screens.TransferScreen
import com.copsiitbhu.agenticbank.viewmodel.AgentViewModel


// ---------------------------------------------------------------------------
// Route constants
// ---------------------------------------------------------------------------

object Routes {
    const val HOME = "home"
    const val TRANSFER = "transfer"
    const val SUCCESS = "success"
}

// ---------------------------------------------------------------------------
// App Navigation Graph
// ---------------------------------------------------------------------------

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: AgentViewModel = viewModel()
) {
    // Observe navigation commands from the ViewModel (agent-driven navigation)
    val navCommand by viewModel.navigationCommand.collectAsState()

    LaunchedEffect(navCommand) {
        navCommand?.let { route ->
            when (route) {
                Routes.HOME -> navController.navigate(Routes.HOME) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
                Routes.TRANSFER -> navController.navigate(Routes.TRANSFER)
                Routes.SUCCESS -> navController.navigate(Routes.SUCCESS)
            }
            viewModel.consumeNavigationCommand()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(380)
            ) + fadeIn(tween(300))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(380)
            ) + fadeOut(tween(200))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(380)
            ) + fadeIn(tween(300))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(380)
            ) + fadeOut(tween(200))
        }
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToTransfer = { navController.navigate(Routes.TRANSFER) }
            )
        }

        composable(Routes.TRANSFER) {
            TransferScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SUCCESS) {
            SuccessScreen(
                viewModel = viewModel,
                onBackHome = {
                    viewModel.onResetToHome()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}
