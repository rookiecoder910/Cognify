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
import com.example.cognify.screens.SequenceRecallGameScreen
import com.example.cognify.screens.WordCompletionGameScreen
import com.example.cognify.screens.GameStatsScreen
import com.example.cognify.ui.screens.MemoryGameScreen
import com.example.cognify.ui.theme.CognifyTheme
import com.google.firebase.auth.FirebaseAuth

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
                                val user =
                                    com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
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

                                onNavigateToProgress = {
                                    navController.navigate("progressDashboard")
                                },
                                onNavigateToAchievements = {
                                    navController.navigate("achievements")
                                },
                                onNavigateToCaregiver = {
                                    navController.navigate("caregiverPortal")
                                },
                                        onNavigateToGameStats = {
                                    navController.navigate("insights")
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
                        composable("insights"){

                            GameStatsScreen(
                                userId =FirebaseAuth.getInstance().currentUser?.uid ?: "",  // Replace with actual user id from ViewModel or Firebase
                                gameName = "MemoryMatch",
                                onBack = { navController.popBackStack() })
                        }


                        composable("games") {
                            GamesListScreen(
                                onPlayMemory = { navController.navigate("games/memory") },
                                onPlayReaction = { navController.navigate("games/reaction") },
                                onPlaySudoku = { navController.navigate("games/sudoku") },
                                onPlaySequence = { navController.navigate("games/sequence") },
                                onPlayWord = { navController.navigate("games/word") }

                            )
                        }
                        composable("games/memory") {
                            MemoryGameScreen(onBack = { navController.popBackStack() })
                        }
                        composable("games/reaction") {
                            ReactionGameScreen(onBack = { navController.popBackStack() })
                        }
                        composable("games/sudoku") {
                            SudokuGameScreen(onBack = { navController.popBackStack() })
                        }
                        composable("games/sequence") {
                            SequenceRecallGameScreen(onBack = { navController.popBackStack() })
                        }
                        composable("games/word") {
                            WordCompletionGameScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}