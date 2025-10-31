package com.example.cognify.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ProgressData(
    val cognitiveScore: Int,
    val gamesPlayed: Int,
    val currentStreak: Int,
    val averageAccuracy: Int,
    val weeklyProgress: List<Int>,
    val recentActivities: List<ActivityItem>
)

data class ActivityItem(
    val gameName: String,
    val score: Int,
    val date: String,
    val icon: String
)

@Composable
fun ProgressDashboardScreen(onBack: () -> Unit) {
    val primaryColor = Color(0xFF6366F1)
    val secondaryColor = Color(0xFF06B6D4)
    val accentColor = Color(0xFFF59E0B)
    val successColor = Color(0xFF10B981)

    val animationColors = remember {
        listOf(
            Color(0xFF6366F1),
            Color(0xFF818CF8),
            Color(0xFF06B6D4),
            Color(0xFF0EA5E9)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "Background")
    val xOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "xOffset"
    )
    val yOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "yOffset"
    )

    val animatedBrush = Brush.linearGradient(
        colors = animationColors,
        start = Offset(xOffset * 0.5f, yOffset * 0.5f),
        end = Offset(xOffset * 1.5f, yOffset * 1.5f)
    )

    // Sample data
    val progressData = remember {
        ProgressData(
            cognitiveScore = 85,
            gamesPlayed = 127,
            currentStreak = 7,
            averageAccuracy = 92,
            weeklyProgress = listOf(75, 78, 82, 80, 85, 88, 85),
            recentActivities = listOf(
                ActivityItem("Memory Match", 95, "Today, 2:30 PM", "🧩"),
                ActivityItem("Sudoku", 88, "Today, 11:00 AM", "🔢"),
                ActivityItem("Reaction Test", 92, "Yesterday", "⚡"),
                ActivityItem("Memory Match", 90, "Yesterday", "🧩")
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBrush)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 20.dp, horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Header
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(primaryColor.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = primaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Your Progress",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = primaryColor
                                )
                            )
                            Text(
                                text = "Keep up the great work!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        IconButton(
                            onClick = { /* Share report */ },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(successColor.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share",
                                tint = successColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            item {
                // Cognitive Score Card
                CognitiveScoreCard(
                    score = progressData.cognitiveScore,
                    primaryColor = primaryColor,
                    successColor = successColor
                )
            }

            item {
                // Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Games",
                        value = progressData.gamesPlayed.toString(),
                        icon = Icons.Default.PlayArrow,
                        color = primaryColor
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Streak",
                        value = "${progressData.currentStreak} days",
                        icon = Icons.Default.LocalFireDepartment,
                        color = accentColor
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Accuracy",
                        value = "${progressData.averageAccuracy}%",
                        icon = Icons.Default.CheckCircle,
                        color = successColor
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Rank",
                        value = "Gold",
                        icon = Icons.Default.EmojiEvents,
                        color = Color(0xFFF59E0B)
                    )
                }
            }

            item {
                // Weekly Progress Chart
                WeeklyProgressCard(
                    weeklyData = progressData.weeklyProgress,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor
                )
            }

            item {
                // Recent Activities
                RecentActivitiesCard(
                    activities = progressData.recentActivities,
                    primaryColor = primaryColor
                )
            }
        }
    }
}

@Composable
fun CognitiveScoreCard(
    score: Int,
    primaryColor: Color,
    successColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Score")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.1f),
                            successColor.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(primaryColor, successColor)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = score.toString(),
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = primaryColor,
                                    fontSize = 48.sp
                                )
                            )
                            Text(
                                text = "Score",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Gray,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                Text(
                    text = "Cognitive Health Score",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = successColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "+5 points this week",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = successColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
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

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun WeeklyProgressCard(
    weeklyData: List<Int>,
    primaryColor: Color,
    secondaryColor: Color
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val maxScore = weeklyData.maxOrNull() ?: 100

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly Progress",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1F2937)
                    )
                )
                Icon(
                    Icons.Default.BarChart,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Simple bar chart
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                weeklyData.forEachIndexed { index, score ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height((score.toFloat() / maxScore * 120).dp)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(primaryColor, secondaryColor)
                                    )
                                )
                        )
                        Text(
                            text = days[index],
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentActivitiesCard(
    activities: List<ActivityItem>,
    primaryColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Activities",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1F2937)
                    )
                )
                TextButton(onClick = { /* View all */ }) {
                    Text(
                        "View All",
                        color = primaryColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            activities.forEach { activity ->
                ActivityRow(activity = activity, primaryColor = primaryColor)
                if (activity != activities.last()) {
                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFF1F5F9)
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityRow(
    activity: ActivityItem,
    primaryColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = activity.icon, fontSize = 24.sp)
            }

            Column {
                Text(
                    text = activity.gameName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                )
                Text(
                    text = activity.date,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray
                    )
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = primaryColor.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "${activity.score}%",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor
                )
            )
        }
    }
}