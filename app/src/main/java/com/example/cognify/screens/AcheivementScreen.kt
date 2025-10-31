package com.example.cognify.screens



import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cognify.data.Achievement


enum class Rarity {
    BRONZE, SILVER, GOLD, PLATINUM
}

@Composable
fun AchievementsScreen(onBack: () -> Unit) {
    val primaryColor = Color(0xFF6366F1)
    val secondaryColor = Color(0xFF06B6D4)
    val goldColor = Color(0xFFF59E0B)
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

    // Sample achievements
    val achievements = remember {
        listOf(
            Achievement(1, "First Steps", "Complete your first game", "👶", true, 1, 1, Rarity.BRONZE),
            Achievement(2, "Week Warrior", "7 day login streak", "🔥", true, 7, 7, Rarity.SILVER),
            Achievement(3, "Memory Master", "Win 10 memory games", "🧠", true, 10, 10, Rarity.GOLD),
            Achievement(4, "Speed Demon", "Perfect score in Reaction Test", "⚡", false, 3, 5, Rarity.GOLD),
            Achievement(5, "Sudoku Solver", "Complete 20 Sudoku puzzles", "🔢", false, 12, 20, Rarity.SILVER),
            Achievement(6, "Perfectionist", "Get 100% accuracy 5 times", "💯", false, 2, 5, Rarity.PLATINUM),
            Achievement(7, "Early Bird", "Play before 8 AM", "🌅", true, 1, 1, Rarity.BRONZE),
            Achievement(8, "Night Owl", "Play after 10 PM", "🦉", false, 0, 1, Rarity.BRONZE),
            Achievement(9, "Comeback King", "Improve score by 20 points", "📈", false, 8, 20, Rarity.GOLD),
            Achievement(10, "Champion", "Reach cognitive score of 90", "🏆", false, 85, 90, Rarity.PLATINUM)
        )
    }

    val unlockedCount = achievements.count { it.isUnlocked }
    val totalAchievements = achievements.size

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
                                text = "Achievements",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = primaryColor
                                )
                            )
                            Text(
                                text = "$unlockedCount / $totalAchievements Unlocked",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        Box(modifier = Modifier.size(44.dp))
                    }
                }
            }

            item {
                // Progress Overview
                AchievementProgressCard(
                    unlockedCount = unlockedCount,
                    totalCount = totalAchievements,
                    primaryColor = primaryColor,
                    goldColor = goldColor
                )
            }

            item {
                Text(
                    text = "Your Badges",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )
            }

            items(achievements) { achievement ->
                AchievementCard(
                    achievement = achievement,
                    primaryColor = primaryColor,
                    goldColor = goldColor
                )
            }
        }
    }
}

@Composable
fun AchievementProgressCard(
    unlockedCount: Int,
    totalCount: Int,
    primaryColor: Color,
    goldColor: Color
) {
    val progress = unlockedCount.toFloat() / totalCount.toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            goldColor.copy(alpha = 0.1f),
                            primaryColor.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🏆",
                fontSize = 56.sp
            )

            Text(
                text = "Achievement Progress",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1F2937)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = unlockedCount.toString(),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryColor
                        )
                    )
                    Text(
                        text = "Unlocked",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = goldColor
                        )
                    )
                    Text(
                        text = "Complete",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray
                        )
                    )
                }
            }

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF1F5F9))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(primaryColor, goldColor)
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun AchievementCard(
    achievement: Achievement,
    primaryColor: Color,
    goldColor: Color
) {
    val rarityColor = when (achievement.rarity) {
        Rarity.BRONZE -> Color(0xFFCD7F32)
        Rarity.SILVER -> Color(0xFFC0C0C0)
        Rarity.GOLD -> goldColor
        Rarity.PLATINUM -> Color(0xFFE5E4E2)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "Achievement${achievement.id}")
    val scale = if (achievement.isUnlocked) {
        val scaleAnimation by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "scale"
        )
        scaleAnimation
    } else {
        1f
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) Color.White else Color.White.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(if (achievement.isUnlocked) 8.dp else 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        if (achievement.isUnlocked) {
                            Brush.linearGradient(
                                colors = listOf(
                                    rarityColor.copy(alpha = 0.3f),
                                    rarityColor.copy(alpha = 0.1f)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.Gray.copy(alpha = 0.2f),
                                    Color.Gray.copy(alpha = 0.1f)
                                )
                            )
                        }
                    )
                    .then(
                        if (achievement.rarity == Rarity.GOLD || achievement.rarity == Rarity.PLATINUM) {
                            Modifier.border(2.dp, rarityColor.copy(alpha = 0.5f), CircleShape)
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = achievement.emoji,
                    fontSize = if (achievement.isUnlocked) 36.sp else 28.sp,
                    modifier = Modifier.then(
                        if (!achievement.isUnlocked) {
                            Modifier.graphicsLayer(alpha = 0.3f)
                        } else {
                            Modifier
                        }
                    )
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = achievement.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (achievement.isUnlocked) Color(0xFF1F2937) else Color.Gray
                        )
                    )

                    if (achievement.isUnlocked && achievement.rarity != Rarity.BRONZE) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = rarityColor.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = achievement.rarity.name,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = rarityColor,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (achievement.isUnlocked) Color.Gray else Color.Gray.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                )

                if (!achievement.isUnlocked) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progress",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Gray,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = "${achievement.progress}/${achievement.maxProgress}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = primaryColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFFF1F5F9))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(achievement.progress.toFloat() / achievement.maxProgress.toFloat())
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(primaryColor)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Modifier.graphicsLayer(alpha: Float): Modifier {
    return this.then(androidx.compose.ui.Modifier.scale(1f))
}