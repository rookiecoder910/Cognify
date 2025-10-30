package com.example.cognify.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cognify.LoginViewModel // Assuming this is your ViewModel path

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val ctx = LocalContext.current

    // Modern Color Palette (e.g., Deep Blue and Cyan)
    val primaryColor = Color(0xFF1E88E5)
    val secondaryColor = Color(0xFF00ACC1)
    val gradientColors = listOf(primaryColor.copy(alpha = 0.8f), secondaryColor.copy(alpha = 0.9f))

    if (state.success) {
        LaunchedEffect(Unit) {
            android.widget.Toast.makeText(ctx, "Welcome back!", android.widget.Toast.LENGTH_SHORT).show()
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Use a clean gradient background
            .background(Brush.verticalGradient(gradientColors)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 32.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                // --- 1. Header/Branding ---
                Text(
                    text = "🧠 Cognify",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = primaryColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 36.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sign in to continue",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- 2. Input Fields ---
                OutlinedTextField(
                    value = state.email,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- 3. Primary Action (Login Button) ---
                Button(
                    onClick = { viewModel.loginUser() },
                    enabled = !state.isLoading && state.email.isNotBlank() && state.password.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    // Animated Content for seamless loading/text transition
                    AnimatedContent(
                        targetState = state.isLoading,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(200)) + slideInVertically()).togetherWith(
                                fadeOut(animationSpec = tween(200)) + slideOutVertically()
                            )
                        }, label = "Login Button Text"
                    ) { isLoading ->
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Text("Login", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- 4. Secondary Actions (Sign Up & Forgot Password) ---

                // Sign Up is now a more subtle TextButton
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Don't have an account?")
                    TextButton(
                        onClick = {
                            viewModel.registerUser { success, msg ->
                                if (success) {
                                    android.widget.Toast.makeText(ctx, "Account created! Please log in.", android.widget.Toast.LENGTH_LONG).show()
                                } else {
                                    android.widget.Toast.makeText(ctx, "Error: ${msg ?: "Unknown error"}", android.widget.Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        enabled = !state.isLoading
                    ) {
                        Text("Sign Up", color = secondaryColor, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(
                    onClick = {
                        viewModel.sendPasswordReset { success, msg ->
                            if (success) {
                                android.widget.Toast.makeText(ctx, "Password reset email sent!", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                android.widget.Toast.makeText(ctx, "Error: ${msg ?: "Invalid email"}", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Forgot Password?", color = Color.Gray)
                }

                // --- 5. Error Message ---
                state.error?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}