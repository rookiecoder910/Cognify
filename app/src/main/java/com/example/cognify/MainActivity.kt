package com.example.cognify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cognify.screens.HomeScreen
import com.example.cognify.ui.screens.LoginScreen

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
                            // simple splash -> go to login or home
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
                            HomeScreen(onLogout = {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }, onNavigateToGames = {
                                navController.navigate("games")
                            })
                        }
                        composable("games") {
                            GamesListScreen(onPlayMemory = { navController.navigate("games/memory") },
                                onPlayReaction = { navController.navigate("games/reaction") })
                        }
                        composable("games/memory") {
                            MemoryGameScreen(onBack = { navController.popBackStack() })
                        }
                        composable("games/reaction") {
                            ReactionGameScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
