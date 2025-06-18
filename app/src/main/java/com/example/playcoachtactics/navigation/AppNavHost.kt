package com.example.playcoachtactics.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.playcoachtactics.ui.screens.*
import com.example.playcoachtactics.viewmodels.TeamViewModel
import com.example.playcoachtactics.viewmodels.TacticalBoardViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AppNavHost(navController: NavHostController) {
    val teamViewModel = hiltViewModel<TeamViewModel>()
    val tacticalBoardViewModel = hiltViewModel<TacticalBoardViewModel>()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToBoard = {
                    navController.navigate("loading") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("loading") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("loading") {
            LoadingScreen()

            LaunchedEffect(Unit) {
                teamViewModel.loadTeamAndPlayers {
                    val positions = teamViewModel.getInitialFormationRelativeOffsets()
                    tacticalBoardViewModel.updatePositions(positions)
                    navController.navigate("board") {
                        popUpTo("loading") { inclusive = true }
                    }
                }
            }
        }

        composable("board") {
            TacticalBoardSimple(
                teamPlayers = teamViewModel.players,
                viewModel = tacticalBoardViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToSquad = { navController.navigate("squad") },
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }

        composable("squad") {
            SquadScreen(
                players = teamViewModel.players,
                viewModel = teamViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToBoard = { navController.navigate("board") },
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }

        composable("profile") {
            ProfileScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
                onNavigateToBoard = { navController.navigate("board") },
                onNavigateToSquad = { navController.navigate("squad") }
            )
        }
    }
}
