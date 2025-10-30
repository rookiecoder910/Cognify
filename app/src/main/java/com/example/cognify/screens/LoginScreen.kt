package com.example.cognify.ui.screens

import LoginViewModel
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val state = viewModel.uiState.collectAsState().value
    val ctx = LocalContext.current

    if (state.success) {
        Toast.makeText(ctx, "Welcome back!", Toast.LENGTH_SHORT).show()
        onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Cognify Login", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // LOGIN + SIGNUP ROW
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { viewModel.loginUser() },
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading)
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    else
                        Text("Login")
                }

                Button(
                    onClick = {
                        viewModel.registerUser { success, msg ->
                            if (success) {
                                Toast.makeText(ctx, "Account created! Please log in.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(ctx, "Error: ${msg ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = !state.isLoading
                ) {
                    Text("Sign Up")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // FORGOT PASSWORD
            TextButton(
                onClick = {
                    viewModel.sendPasswordReset { success, msg ->
                        if (success) {
                            Toast.makeText(ctx, "Password reset email sent!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(ctx, "Error: ${msg ?: "Invalid email"}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            ) {
                Text("Forgot Password?")
            }

            // ERROR MESSAGE
            state.error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
