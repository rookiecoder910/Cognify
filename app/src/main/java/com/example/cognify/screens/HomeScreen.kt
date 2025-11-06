package com.example.cognify.screens

import androidx.compose.animation.core.*
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.TrendingUp

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToGames: () -> Unit,
    onNavigateToProgress: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToCaregiver: () -> Unit = {},
    onNavigateToGameStats: () -> Unit = {}
) {
    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email ?: "Guest"
    var showMenuDialog by remember { mutableStateOf(false) }

    // Enhanced color palette
    val primaryColor = Color(0xFF6366F1) // Indigo
    val lightPrimary = Color(0xFF818CF8)
    val secondaryColor = Color(0xFF06B6D4) // Cyan
    val accentColor = Color(0xFFF59E0B) // Amber

    val animationColors = remember {
        listOf(
            Color(0xFF6366F1),
            Color(0xFF818CF8),
            Color(0xFF06B6D4),
            Color(0xFF0EA5E9)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "GradientAnimation")

    val xOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "xOffset"
    )

    val yOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "yOffset"
    )

    val animatedBrush = Brush.linearGradient(
        colors = animationColors,
        start = Offset(xOffset * 0.5f, yOffset * 0.5f),
        end = Offset(xOffset * 1.5f, yOffset * 1.5f)
    )

    Scaffold(
        topBar = {
            HomeHeader(
                title = "Cognify",
                subtitle = "Elevate Your Mind"
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showMenuDialog = true },
                containerColor = Color.White,
                contentColor = primaryColor,
                elevation = FloatingActionButtonDefaults.elevation(12.dp)
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBrush)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AnimatedWelcomeCard(
                        email = email,
                        secondaryColor = secondaryColor,
                        accentColor = accentColor
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Quick Access Menu
                    QuickAccessMenu(
                        onNavigateToProgress = onNavigateToProgress,
                        onNavigateToAchievements = onNavigateToAchievements,
                        onNavigateToCaregiver = onNavigateToCaregiver,
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor,
                        accentColor = accentColor

                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // New Stats Cards
                    StatsRow(primaryColor = primaryColor, accentColor = accentColor)

                    Spacer(modifier = Modifier.height(32.dp))

                    PlayGamesButton(
                        onNavigateToGames = onNavigateToGames,
                        primaryColor = primaryColor
                    )
                }

                HomeFooter(onLogout = onLogout)
            }
        }

        // Full Screen Menu Dialog
        if (showMenuDialog) {
            FullScreenMenu(
                onDismiss = { showMenuDialog = false },
                onNavigateToProgress = {
                    showMenuDialog = false
                    onNavigateToProgress()
                },
                onNavigateToAchievements = {
                    showMenuDialog = false
                    onNavigateToAchievements()
                },
                onNavigateToCaregiver = {
                    showMenuDialog = false
                    onNavigateToCaregiver()
                },
                onNavigateToGames = {
                    showMenuDialog = false
                    onNavigateToGames()
                },
                primaryColor = primaryColor,
                secondaryColor = secondaryColor,
                accentColor = accentColor,
                onNavigateToGameStats = {
                    showMenuDialog = false; onNavigateToGameStats() },
            )
        }
    }
}

@Composable
fun FullScreenMenu(
    onDismiss: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToCaregiver: () -> Unit,
    onNavigateToGames: () -> Unit,
    onNavigateToGameStats: () -> Unit,
    primaryColor: Color,
    secondaryColor: Color,
    accentColor: Color
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Quick Menu",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryColor
                    )
                )

                Text(
                    text = "Navigate to any section",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
                    )
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                MenuOption(
                    title = "Start Training",
                    description = "Play cognitive games",
                    icon = Icons.Default.PlayArrow,
                    color = primaryColor,
                    onClick = onNavigateToGames
                )

                MenuOption(
                    title = "View Progress",
                    description = "Track your improvement",
                    icon = Icons.Default.TrendingUp,
                    color = primaryColor,
                    onClick = onNavigateToProgress
                )

                MenuOption(
                    title = "Achievements",
                    description = "See your rewards",
                    icon = Icons.Default.EmojiEvents,
                    color = accentColor,
                    onClick = onNavigateToAchievements
                )

                MenuOption(
                    title = "Caregiver Portal",
                    description = "Share with caregivers",
                    icon = Icons.Default.People,
                    color = secondaryColor,
                    onClick = onNavigateToCaregiver
                )
                MenuOption(
                    title = "Game Insights",
                    description = "View session analytics & performance",
                    icon = Icons.Default.Games,
                    color = Color(0xFF3B82F6),
                    onClick = onNavigateToGameStats
                )


                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MenuOption(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray
                    )
                )
            }
        }
    }
}


@Composable
fun AnimatedWelcomeCard(
    email: String,
    secondaryColor: Color,
    accentColor: Color
) {
    val userName = email.split('@').firstOrNull()
        ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        ?: "User"

    val infiniteTransition = rememberInfiniteTransition(label = "IconAnimation")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                secondaryColor.copy(alpha = 0.3f),
                                accentColor.copy(alpha = 0.2f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = secondaryColor,
                    modifier = Modifier
                        .size(56.dp)
                        .scale(scale)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Welcome back,",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = secondaryColor
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ready to train?",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun QuickAccessMenu(
    onNavigateToProgress: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToCaregiver: () -> Unit,

    primaryColor: Color,
    secondaryColor: Color,
    accentColor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Quick Access",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(start = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessCard(
                modifier = Modifier.weight(1f),
                title = "Progress",
                icon = Icons.Default.TrendingUp,
                color = primaryColor,
                onClick = onNavigateToProgress
            )

            QuickAccessCard(
                modifier = Modifier.weight(1f),
                title = "Rewards",
                icon = Icons.Default.EmojiEvents,
                color = accentColor,
                onClick = onNavigateToAchievements
            )

            QuickAccessCard(
                modifier = Modifier.weight(1f),
                title = "Caregiver",
                icon = Icons.Default.People,
                color = secondaryColor,
                onClick = onNavigateToCaregiver
            )

        }
    }
}

@Composable
fun QuickAccessCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
fun StatsRow(primaryColor: Color, accentColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.EmojiEvents,
            value = "0",
            label = "Games",
            color = primaryColor
        )

        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Star,
            value = "0",
            label = "Stars",
            color = accentColor
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "StatAnimation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "rotation"
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier
                    .size(32.dp)
                    .rotate(rotation)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun HomeHeader(title: String, subtitle: String) {
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
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 42.sp,
                letterSpacing = (-0.5).sp
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        )
    }
}

@Composable
fun PlayGamesButton(onNavigateToGames: () -> Unit, primaryColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "ButtonPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    ElevatedButton(
        onClick = { onNavigateToGames() },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = primaryColor
        ),
        shape = RoundedCornerShape(28.dp),
        elevation = ButtonDefaults.elevatedButtonElevation(12.dp),
        contentPadding = PaddingValues(horizontal = 40.dp, vertical = 20.dp),
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .scale(scale)
    ) {
        Icon(
            Icons.Default.PlayArrow,
            contentDescription = "Play Icon",
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            "Start Training",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
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
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextButton(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                onLogout()
            },
            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Logout Icon",
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                "Logout Securely",
                fontWeight = FontWeight.Medium
            )
        }
    }
}