package com.example.cognify.screens

import androidx.compose.animation.core.*
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToGames: () -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email ?: "Guest"

    // --- 1. Define Colors and Animation Setup ---
    val primaryColor = Color(0xFF1E88E5)
    val lightPrimary = Color(0xFF64B5F6)
    val secondaryColor = Color(0xFF00ACC1)

    // Colors used in the animation
    val animationColors = remember { listOf(primaryColor, lightPrimary, secondaryColor, Color(0xFF26A69A)) }

    val infiniteTransition = rememberInfiniteTransition(label = "GradientAnimation")

    // Animate the X offset slowly
    val xOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "xOffset"
    )

    // Animate the Y offset slowly (at a different speed for variety)
    val yOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "yOffset"
    )

    // Create the Animated Brush
    val animatedBrush = Brush.linearGradient(
        colors = animationColors,
        start = Offset(xOffset * 0.5f, yOffset * 0.5f),
        end = Offset(xOffset * 1.5f, yOffset * 1.5f)
    )
    // --- End Animation Setup ---

    Scaffold(
        topBar = {
            // NOTE: HomeHeader colors are now white to contrast with the animated background
            HomeHeader(
                title = "Cognify",
                subtitle = "Boost Your Brain Power"
            )
        },
        // Set Scaffold container color to transparent so the Box background is visible
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        // --- 2. Apply Animated Brush to a Full-Screen Box ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBrush) // The animated background!
                .padding(paddingValues), // Apply padding from Scaffold
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Main Content Area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    WelcomeCard(email = email, primaryColor = primaryColor, secondaryColor = secondaryColor)

                    Spacer(modifier = Modifier.height(32.dp))

                    PlayGamesButton(
                        onNavigateToGames = onNavigateToGames,
                        primaryColor = primaryColor
                    )
                }

                // Footer / Logout Area
                HomeFooter(onLogout = onLogout)
            }
        }
    }
}

// --- Component Breakdown (Modified Header) ---

@Composable
fun HomeHeader(title: String, subtitle: String) {
    // Removed the static background brush here since the animated brush covers the whole screen
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .animateContentSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge.copy(
                color = Color.White, // Use White for contrast against the blue/cyan gradient
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White.copy(alpha = 0.8f)
            )
        )
    }
}

// --- Component Breakdown (Unchanged - for reference) ---
@Composable
fun WelcomeCard(email: String, primaryColor: Color, secondaryColor: Color) {
    // Determine the user name for display
    val userName = email.split('@').firstOrNull()?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } ?: "User"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. Top Section (Secondary Color Accent) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(secondaryColor.copy(alpha = 0.9f))
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // User Avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = email.first().uppercase(),
                        color = secondaryColor,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Welcome Back, $userName!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White // High contrast text on color background
                    ),
                    textAlign = TextAlign.Center
                )
            }

            // --- 2. Bottom Section (Card Content) ---
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ready to challenge your mind and track your progress today?",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Add a small CTA/Stats Placeholder for future use
                Button(
                    onClick = { /* Implement quick stats view or daily goal */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor.copy(alpha = 0.1f),
                        contentColor = primaryColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text("View Daily Goal")
                }
            }
        }
    }
}
@Composable
fun PlayGamesButton(onNavigateToGames: () -> Unit, primaryColor: Color) {
    ElevatedButton(
        onClick = { onNavigateToGames() },
        colors = ButtonDefaults.buttonColors(
            containerColor = primaryColor,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(18.dp),
        elevation = ButtonDefaults.elevatedButtonElevation(8.dp),
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        Icon(Icons.Default.PlayArrow, contentDescription = "Play Icon")
        Spacer(Modifier.width(8.dp))
        Text("Start Playing", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun HomeFooter(onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Need a break?",
            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextButton(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                onLogout()
            },
            colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout Icon", modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text("Logout Securely")
        }
    }
}