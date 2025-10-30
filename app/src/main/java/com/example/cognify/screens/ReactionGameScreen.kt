package com.example.cognify.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cognify.data.ReactionState
import kotlinx.coroutines.delay
import kotlin.random.Random


@Composable
fun ReactionGameScreen(onBack: () -> Unit) {
    var gameState by remember { mutableStateOf<ReactionState>(ReactionState.Initial) }
    var gameTrigger by remember { mutableStateOf(false) }

    // Animated scale for pulsing effect
    val pulseScale by animateFloatAsState(
        targetValue = if (gameState is ReactionState.Go) 1.05f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "Pulse Scale"
    )

    // Background gradient colors
    val bgColor: Color by animateColorAsState(
        targetValue = when (gameState) {
            ReactionState.Initial -> Color(0xFF1E1E2E)
            ReactionState.Ready -> Color(0xFFB71C1C)
            is ReactionState.Go -> Color(0xFF2E7D32)
            is ReactionState.Result -> Color(0xFF1E1E2E)
            ReactionState.TooSoon -> Color(0xFFB71C1C)
        },
        animationSpec = tween(durationMillis = 400),
        label = "Background Color"
    )

    val accentColor: Color by animateColorAsState(
        targetValue = when (gameState) {
            ReactionState.Initial -> Color(0xFF6366F1)
            ReactionState.Ready -> Color(0xFFD32F2F)
            is ReactionState.Go -> Color(0xFF66BB6A)
            is ReactionState.Result -> Color(0xFF8B5CF6)
            ReactionState.TooSoon -> Color(0xFFEF5350)
        },
        animationSpec = tween(durationMillis = 400),
        label = "Accent Color"
    )

    // --- State Handler ---
    LaunchedEffect(gameTrigger) {
        if (gameState is ReactionState.Ready) {
            val delayMs = Random.nextLong(1500, 4000)
            delay(delayMs)
            if (gameState is ReactionState.Ready) {
                gameState = ReactionState.Go(System.currentTimeMillis())
            }
        }
    }

    // --- User Input Handler ---
    val handleTap: () -> Unit = {
        when (val state = gameState) {
            ReactionState.Initial, is ReactionState.Result, ReactionState.TooSoon -> {
                gameState = ReactionState.Ready
                gameTrigger = !gameTrigger
            }
            ReactionState.Ready -> {
                gameState = ReactionState.TooSoon
            }
            is ReactionState.Go -> {
                val endTime = System.currentTimeMillis()
                val reactionTime = endTime - state.startTime
                gameState = ReactionState.Result(reactionTime)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(bgColor, bgColor.copy(alpha = 0.8f))
                )
            )
            .clickable(
                onClick = handleTap,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Reaction Test",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            // Main Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                when (val state = gameState) {
                    ReactionState.Initial -> InitialStateContent(accentColor)
                    ReactionState.Ready -> ReadyStateContent()
                    is ReactionState.Go -> GoStateContent()
                    is ReactionState.Result -> ResultStateContent(state.timeMs, accentColor)
                    ReactionState.TooSoon -> TooSoonStateContent()
                }
            }

            // Bottom spacing (no button)
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

@Composable
fun InitialStateContent(accentColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(accentColor.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = accentColor
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Test Your Reflexes",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tap anywhere to start!\nWhen the screen turns green, tap as fast as you can!",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun ReadyStateContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "WAIT...",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Black,
            fontSize = 72.sp,
            color = Color.White,
            letterSpacing = 4.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(4.dp)
                .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Wait for green...",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GoStateContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "TAP!",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Black,
            fontSize = 96.sp,
            color = Color.White,
            letterSpacing = 8.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "NOW NOW NOW!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.9f),
            letterSpacing = 2.sp
        )
    }
}

@Composable
fun ResultStateContent(timeMs: Long, accentColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(accentColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$timeMs",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Black,
                    fontSize = 56.sp,
                    color = accentColor
                )
                Text(
                    text = "ms",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = accentColor.copy(alpha = 0.7f)
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = when {
                timeMs < 200 -> "🚀 Lightning Fast!"
                timeMs < 300 -> "⚡ Excellent!"
                timeMs < 400 -> "👍 Good!"
                else -> "💪 Keep Practicing!"
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Average human reaction time: 250ms",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tap to try again",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.5f),
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }
}

@Composable
fun TooSoonStateContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "TOO SOON!",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Black,
            fontSize = 56.sp,
            color = Color.White,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "⏰",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Wait for the screen to turn green!\nTap to try again",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            lineHeight = 28.sp
        )
    }
}