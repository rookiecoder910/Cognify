package com.example.cognify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cognify.screens.HomeScreen

import com.example.cognify.ui.screens.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            LoginScreen(onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true } // remove login from back stack
                                }
                            })
                        }

                        composable("home") {
                            HomeScreen(onLogout = {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}
