package com.example.cognify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cognify.screens.AchievementsScreen
import com.example.cognify.screens.CaregiverPortalScreen
import com.example.cognify.screens.HomeScreen
import com.example.cognify.screens.ProgressDashboardScreen
import com.example.cognify.screens.ReactionGameScreen
import com.example.cognify.screens.SudokuGameScreen
import com.example.cognify.screens.GamesListScreen


import com.example.cognify.screens.LoginScreen
import com.example.cognify.ui.screens.MemoryGameScreen
import com.example.cognify.ui.theme.CognifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CognifyTheme {
                val navController = rememberNavController()
                Surface(color = MaterialTheme.colorScheme.background) {
                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") {
                            androidx.compose.runtime.LaunchedEffect(Unit) {
                                val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                                if (user != null) navController.navigate("home") {
                                    popUpTo("splash") { inclusive = true }
                                } else navController.navigate("login") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        }
                        composable("login") {
                            LoginScreen(onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            })
                        }


                        composable("home") {
                            HomeScreen(
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onNavigateToGames = {
                                    navController.navigate("games")
                                },
                                // NEW NAVIGATION CALLS
                                onNavigateToProgress = {
                                    navController.navigate("progressDashboard")
                                },
                                onNavigateToAchievements = {
                                    navController.navigate("achievements")
                                },
                                onNavigateToCaregiver = {
                                    navController.navigate("caregiverPortal")
                                }
                            )
                        }


                        composable("progressDashboard") {
                            ProgressDashboardScreen(onBack = { navController.popBackStack() })
                        }

                        composable("achievements") {
                            AchievementsScreen(onBack = { navController.popBackStack() })
                        }

                        composable("caregiverPortal") {
                            CaregiverPortalScreen(onBack = { navController.popBackStack() })
                        }

                        // Games List and individual game screens
                        composable("games") {
                            GamesListScreen(
                                onPlayMemory = { navController.navigate("games/memory") },
                                onPlayReaction = { navController.navigate("games/reaction") },
                                onPlaySudoku = { navController.navigate("games/sudoku") }
                            )
                        }
                        composable("games/memory") {
                            MemoryGameScreen(onBack = { navController.popBackStack() })
                        }
                        composable("games/reaction") {
                            ReactionGameScreen(onBack = { navController.popBackStack() })
                        }
                        composable("games/sudoku"){
                            SudokuGameScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}