package com.example.cognify.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle

import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cognify.data.GameLevel
import kotlinx.coroutines.delay
import kotlin.random.Random

val gameLevels = listOf(
    // Level 1: 2x2 (4 cards) - Beginner
    GameLevel(2, 2, listOf("🍎", "🍌")),

    // Level 2: 3x2 (6 cards) - Easy
    GameLevel(3, 2, listOf("🐶", "🐱", "🐭")),

    // Level 3: 3x4 (12 cards) - Medium
    GameLevel(3, 4, listOf("⚽", "🏀", "🏈", "⚾", "🎾", "🏐")),

    // Level 4: 4x4 (16 cards) - Hard
    GameLevel(4, 4, listOf("🌸", "🌺", "🌻", "🌷", "🌹", "🏵️", "💐", "🌼")),

    // Level 5: 5x4 (20 cards) - Expert
    GameLevel(5, 4, listOf("🚗", "🚕", "🚙", "🚌", "🚎", "🏎️", "🚓", "🚑", "🚒", "🚐")),

    // Level 6: 6x4 (24 cards) - Master
    GameLevel(6, 4, listOf("🎸", "🎹", "🥁", "🎺", "🎻", "🎷", "🪕", "🎼", "🎵", "🎶", "🎤", "🎧"))
)

@Composable
fun MemoryGameScreen(onBack: () -> Unit) {
    var currentLevelIndex by remember { mutableStateOf(0) }
    val currentLevel = gameLevels[currentLevelIndex]

    key(currentLevelIndex) {
        GameContent(
            level = currentLevel,
            levelNumber = currentLevelIndex + 1,
            onLevelComplete = {
                if (currentLevelIndex < gameLevels.size - 1) {
                    currentLevelIndex++
                }
            },
            onRestart = { currentLevelIndex = 0 },
            onBack = onBack
        )
    }
}

@Composable
fun GameContent(
    level: GameLevel,
    levelNumber: Int,
    onLevelComplete: () -> Unit,
    onRestart: () -> Unit,
    onBack: () -> Unit
) {
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

    val totalCards = level.size
    val pairs = totalCards / 2
    val theme = level.theme.shuffled(Random(System.currentTimeMillis())).take(pairs)
    val base = remember { (theme + theme).shuffled(Random(System.currentTimeMillis())) }

    var revealed by remember { mutableStateOf(List(totalCards) { false }) }
    var matched by remember { mutableStateOf(List(totalCards) { false }) }
    var firstIndex by remember { mutableStateOf(-1) }
    var moves by remember { mutableStateOf(0) }
    var canClick by remember { mutableStateOf(false) }
    var timeElapsed by remember { mutableStateOf(0) }
    var isGameActive by remember { mutableStateOf(true) }
    var showPreview by remember { mutableStateOf(true) }

    val isGameComplete = matched.all { it }
    val matchedPairs = matched.count { it } / 2

    // Preview all cards for 3 seconds at start
    LaunchedEffect(Unit) {
        revealed = List(totalCards) { true }
        delay(1500)
        revealed = List(totalCards) { false }
        showPreview = false
        canClick = true
    }

    // Timer
    LaunchedEffect(isGameActive, isGameComplete, showPreview) {
        if (isGameActive && !isGameComplete && !showPreview) {
            while (true) {
                delay(1000)
                timeElapsed++
            }
        }
    }

    LaunchedEffect(revealed) {
        val revealedButNotMatchedIndices = revealed.mapIndexedNotNull { index, isRevealed ->
            if (isRevealed && !matched[index]) index else null
        }

        if (revealedButNotMatchedIndices.size == 2) {
            canClick = false
            delay(1000)

            val index1 = revealedButNotMatchedIndices[0]
            val index2 = revealedButNotMatchedIndices[1]

            if (base[index1] == base[index2]) {
                matched = matched.toMutableList().apply {
                    this[index1] = true
                    this[index2] = true
                }.toList()
            } else {
                revealed = revealed.mapIndexed { i, r ->
                    if (i == index1 || i == index2) false else r
                }
            }

            firstIndex = -1
            canClick = true
        }

        if (isGameComplete && isGameActive) {
            isGameActive = false
            delay(800)
            onLevelComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Card with level badge
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = primaryColor
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "LEVEL $levelNumber",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        letterSpacing = 1.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${level.rows}×${level.cols} Grid • ${pairs} Pairs",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        IconButton(
                            onClick = onRestart,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Restart",
                                tint = accentColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enhanced Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EnhancedStatCard(
                    modifier = Modifier.weight(1f),
                    label = "Moves",
                    value = moves.toString(),
                    icon = Icons.Default.CheckCircle,
                    color = primaryColor,
                    accentColor = Color(0xFF818CF8)
                )
                EnhancedStatCard(
                    modifier = Modifier.weight(1f),
                    label = "Time",
                    value = "${timeElapsed}s",
                    icon = Icons.Default.Build,
                    color = secondaryColor,
                    accentColor = Color(0xFF22D3EE)
                )
                EnhancedStatCard(
                    modifier = Modifier.weight(1f),
                    label = "Pairs",
                    value = "$matchedPairs/$pairs",
                    icon = Icons.Default.Face,
                    color = successColor,
                    accentColor = Color(0xFF34D399)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Game Grid with progress indicator
            Box(modifier = Modifier.weight(1f)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(level.cols),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(base) { idx, emoji ->
                        PremiumMemoryCard(
                            emoji = emoji,
                            isRevealed = revealed[idx],
                            isMatched = matched[idx],
                            canFlip = canClick && !matched[idx] && (firstIndex == -1 || firstIndex != idx),
                            primaryColor = primaryColor,
                            successColor = successColor,
                            isPreview = showPreview,
                            onClick = {
                                if (!revealed[idx] && canClick && firstIndex != idx && !showPreview) {
                                    moves++
                                    revealed = revealed.toMutableList().apply { this[idx] = true }.toList()
                                    if (firstIndex == -1) {
                                        firstIndex = idx
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        if (isGameComplete) {
            PremiumLevelCompleteOverlay(
                level = levelNumber,
                moves = moves,
                time = timeElapsed,
                totalLevels = gameLevels.size,
                onNextLevel = {
                    if (levelNumber < gameLevels.size) {
                        onLevelComplete()
                    } else {
                        onBack()
                    }
                },
                primaryColor = primaryColor,
                successColor = successColor
            )
        }
    }
}

@Composable
fun EnhancedStatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    accentColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "StatIcon")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                color.copy(alpha = 0.2f),
                                accentColor.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier
                        .size(24.dp)
                        .scale(scale)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = color,
                    fontSize = 20.sp
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun PremiumMemoryCard(
    emoji: String,
    isRevealed: Boolean,
    isMatched: Boolean,
    canFlip: Boolean,
    primaryColor: Color,
    successColor: Color,
    isPreview: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val scale by animateFloatAsState(
        targetValue = when {
            isMatched -> 1.08f
            isRevealed -> 1.02f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isMatched) 360f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotation"
    )

    val cardColor = when {
        isMatched -> Brush.linearGradient(
            colors = listOf(
                successColor.copy(alpha = 0.3f),
                successColor.copy(alpha = 0.2f)
            )
        )
        isRevealed || isPreview -> Brush.linearGradient(
            colors = listOf(Color.White, Color(0xFFF8FAFC))
        )
        else -> Brush.linearGradient(
            colors = listOf(
                primaryColor,
                primaryColor.copy(alpha = 0.9f)
            )
        )
    }

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .rotate(if (isMatched) rotation else 0f)
            .clickable(
                enabled = canFlip && !isPreview,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(if (isRevealed || isMatched) 4.dp else 10.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(cardColor)
        ) {
            AnimatedContent(
                targetState = if (isRevealed || isMatched || isPreview) emoji else "?",
                transitionSpec = {
                    (fadeIn(animationSpec = tween(400)) + scaleIn(
                        initialScale = 0.8f,
                        animationSpec = tween(400)
                    )).togetherWith(
                        fadeOut(animationSpec = tween(200)) + scaleOut(
                            targetScale = 0.8f,
                            animationSpec = tween(200)
                        )
                    )
                },
                label = "CardFlip"
            ) { content ->
                Text(
                    text = content,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = if (content == "?") 42.sp else 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (content == "?") Color.White else Color.Black
                    )
                )
            }

            if (isMatched) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                        .offset((-8).dp, 8.dp)
                        .clip(CircleShape)
                        .background(successColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Preview overlay with countdown
            if (isPreview) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.05f))
                )
            }
        }
    }
}

@Composable
fun PremiumLevelCompleteOverlay(
    level: Int,
    moves: Int,
    time: Int,
    totalLevels: Int,
    onNextLevel: () -> Unit,
    primaryColor: Color,
    successColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Celebration")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(24.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "🎉",
                    fontSize = 72.sp,
                    modifier = Modifier
                        .scale(scale)
                        .rotate(rotation)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Level $level Complete!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = successColor,
                        fontSize = 28.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (level < totalLevels) {
                    Text(
                        text = "Amazing work! Ready for more?",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = "🏆 You conquered all levels! 🏆",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = primaryColor,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PremiumScoreItem(
                        label = "Moves",
                        value = moves.toString(),
                        color = primaryColor
                    )
                    PremiumScoreItem(
                        label = "Time",
                        value = "${time}s",
                        color = Color(0xFF06B6D4)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onNextLevel,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (level < totalLevels) successColor else primaryColor
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(vertical = 18.dp),
                    elevation = ButtonDefaults.buttonElevation(8.dp)
                ) {
                    Text(
                        text = if (level < totalLevels) "Continue to Level ${level + 1}" else "Back to Menu",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumScoreItem(label: String, value: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = color,
                    fontSize = 32.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}