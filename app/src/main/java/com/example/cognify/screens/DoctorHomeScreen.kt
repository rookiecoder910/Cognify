package com.example.cognify.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DoctorHomeScreen(
    onLogout: () -> Unit,
    onNavigateToPatients: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email ?: "Doctor"

    // Theme colors
    val primaryColor = Color(0xFF6366F1)
    val secondaryColor = Color(0xFF06B6D4)
    val accentColor = Color(0xFFF59E0B)

    // Animated gradient background
    val colors = remember { listOf(Color(0xFF6366F1), Color(0xFF818CF8), Color(0xFF06B6D4), Color(0xFF0EA5E9)) }
    val infiniteTransition = rememberInfiniteTransition(label = "DoctorBG")
    val x by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "xOffset"
    )
    val y by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "yOffset"
    )
    val backgroundBrush = Brush.linearGradient(
        colors = colors,
        start = Offset(x * 0.5f, y * 0.5f),
        end = Offset(x * 1.5f, y * 1.5f)
    )

    Scaffold(
        topBar = {
            DoctorHeader(title = "Cognify Doctor", subtitle = "Monitor & Guide Smarter")
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Welcome Section
                DoctorWelcomeCard(email, secondaryColor, accentColor)

                Spacer(Modifier.height(24.dp))

                // Quick Actions
                DoctorQuickMenu(
                    onNavigateToPatients = onNavigateToPatients,
                    onNavigateToReports = onNavigateToReports,
                    onNavigateToAlerts = onNavigateToAlerts,
                    onNavigateToSettings = onNavigateToSettings,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    accentColor = accentColor
                )

                Spacer(Modifier.height(28.dp))

                // Dashboard stats
                DoctorStatsRow(primaryColor = primaryColor, accentColor = accentColor)

                Spacer(Modifier.height(36.dp))

                // Reports button
                ElevatedButton(
                    onClick = onNavigateToReports,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = primaryColor
                    ),
                    shape = RoundedCornerShape(28.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(12.dp),
                    contentPadding = PaddingValues(horizontal = 40.dp, vertical = 20.dp),
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    Icon(Icons.Default.Assessment, contentDescription = "Reports", modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("View Reports", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(32.dp))

                DoctorFooter(onLogout)
            }
        }
    }
}

@Composable
fun DoctorHeader(title: String, subtitle: String) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 36.sp
            )
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
fun DoctorWelcomeCard(email: String, secondary: Color, accent: Color) {
    val name = email.split('@').firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Doctor"

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(secondary.copy(alpha = 0.3f), accent.copy(alpha = 0.3f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MedicalServices, contentDescription = null, tint = secondary, modifier = Modifier.size(42.dp))
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text("Welcome,", color = Color.Gray, fontSize = 14.sp)
                Text(
                    text = "Dr. $name",
                    color = secondary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                )
                Text(
                    "Empower your patients today!",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun DoctorQuickMenu(
    onNavigateToPatients: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToSettings: () -> Unit,
    primaryColor: Color,
    secondaryColor: Color,
    accentColor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Quick Actions",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            QuickActionCard1("Patients", Icons.Default.People, primaryColor, onNavigateToPatients, Modifier.weight(1f))
            QuickActionCard1("Reports", Icons.Default.Assessment, secondaryColor, onNavigateToReports, Modifier.weight(1f))
            QuickActionCard1("Alerts", Icons.Default.Notifications, accentColor, onNavigateToAlerts, Modifier.weight(1f))
            QuickActionCard1("Settings", Icons.Default.Settings, Color.Gray, onNavigateToSettings, Modifier.weight(1f))
        }
    }
}

@Composable
fun QuickActionCard1(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = color)
            }
            Spacer(Modifier.height(8.dp))
            Text(title, color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
fun DoctorStatsRow(primaryColor: Color, accentColor: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        DoctorStatCard(Icons.Default.People, "12", "Patients", primaryColor, Modifier.weight(1f))
        DoctorStatCard(Icons.Default.MonitorHeart, "4", "Critical Alerts", accentColor, Modifier.weight(1f))
    }
}

@Composable
fun DoctorStatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val rotation by rememberInfiniteTransition(label = "statAnim").animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ), label = "rotation"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(30.dp).rotate(rotation))
            Spacer(Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.Bold, color = color, fontSize = 22.sp)
            Text(label, color = Color.Gray, fontSize = 13.sp)
        }
    }
}

@Composable
fun DoctorFooter(onLogout: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("End of shift?", color = Color.White.copy(alpha = 0.8f))
        TextButton(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                onLogout()
            },
            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Logout Securely", fontWeight = FontWeight.Medium)
        }
    }
}