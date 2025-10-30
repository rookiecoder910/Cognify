package com.example.cognify.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cognify.screens.ReactionState.*
import kotlinx.coroutines.delay
import kotlin.random.Random

// --- Game States ---
sealed class ReactionState {
    data object Initial : ReactionState() // Waiting for the user to press START
    data object Ready : ReactionState()   // Screen is RED, waiting for delay to end
    data class Go(val startTime: Long) : ReactionState() // Screen is GREEN, waiting for tap
    data class Result(val timeMs: Long) : ReactionState() // Game finished successfully
    data object TooSoon : ReactionState() // User tapped too early
}

@Composable
fun ReactionGameScreen(onBack: () -> Unit) {
    var gameState by remember { mutableStateOf<ReactionState>(ReactionState.Initial) }
    var gameTrigger by remember { mutableStateOf(false) } // Trigger for the random delay

    // Define colors for states
    val bgColor: Color by animateColorAsState(
        targetValue = when (gameState) {
            ReactionState.Initial, is ReactionState.Result -> MaterialTheme.colorScheme.background
            ReactionState.Ready -> Color(0xFFC62828) // Deep Red
            is ReactionState.Go -> Color(0xFF4CAF50)  // Deep Green
            ReactionState.TooSoon -> Color(0xFFC62828) // Deep Red
            is ReactionState.Result -> TODO()
        },
        animationSpec = tween(durationMillis = 300), label = "Background Color Change"
    )

    // --- State Handler (Core Game Logic) ---
    LaunchedEffect(gameTrigger) {
        if (gameState is ReactionState.Ready) {
            val delayMs = Random.nextLong(1000, 3000) // Slightly shorter max delay
            delay(delayMs)

            // Check if the state hasn't changed (user didn't tap too soon)
            if (gameState is ReactionState.Ready) {
                gameState = ReactionState.Go(System.currentTimeMillis())
            }
        }
    }

    // --- User Input Handler ---
    val handleTap: () -> Unit = {
        when (val state = gameState) {
            ReactionState.Initial, is ReactionState.Result, ReactionState.TooSoon -> {
                // Start the game
                gameState = ReactionState.Ready
                gameTrigger = !gameTrigger
            }
            ReactionState.Ready -> {
                // Tapped too soon!
                gameState = ReactionState.TooSoon
            }
            is ReactionState.Go -> {
                // Successful reaction!
                val endTime = System.currentTimeMillis()
                val reactionTime = endTime - state.startTime
                gameState = Result(reactionTime)
            }

            is ReactionState.Result -> TODO()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            // Use a FAB for the primary action (Start/Try Again)
            if (gameState !is ReactionState.Ready && gameState !is ReactionState.Go) {
                ExtendedFloatingActionButton(
                    onClick = handleTap,
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Start") },
                    text = {
                        Text(
                            text = if (gameState is ReactionState.Initial) "Start Test" else "Tap to Restart",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        }
    ) { paddingValues ->
        // Main Clickable Area (Full Screen)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                // Use interactionSource = null to remove the ripple effect for a better test feel
                .clickable(
                    onClick = handleTap,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
                .padding(paddingValues)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            // --- Content Based on State ---
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                when (val state = gameState) {
                    ReactionState.Initial -> InitialStateContent()
                    ReactionState.Ready -> ReadyStateContent()
                    is ReactionState.Go -> GoStateContent()
                    is ReactionState.Result -> ResultStateContent(timeMs = state.timeMs)
                    ReactionState.TooSoon -> TooSoonStateContent()
                }

                // Back Button (Always visible but styled subtly)
                Spacer(modifier = Modifier.height(60.dp)) // Push content up slightly
                TextButton(onClick = onBack) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        Spacer(Modifier.width(4.dp))
                        Text("Game List")
                    }
                }
            }
        }
    }
}

// --- State-Specific Composable Functions ---

@Composable
fun InitialStateContent() {
    Text(
        text = "Reaction Test",
        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Tap the 'Start Test' button below to begin.",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
        textAlign = TextAlign.Center
    )
}

@Composable
fun ReadyStateContent() {
    Text(
        text = "WAIT",
        style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black, fontSize = 96.sp),
        color = Color.White
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Wait for the screen to turn GREEN, then tap!",
        style = MaterialTheme.typography.titleLarge,
        color = Color.White.copy(alpha = 0.9f),
        textAlign = TextAlign.Center
    )
}

@Composable
fun GoStateContent() {
    Text(
        text = "GO!",
        style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black, fontSize = 96.sp),
        color = Color.Black
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Tap NOW!",
        style = MaterialTheme.typography.titleLarge,
        color = Color.Black.copy(alpha = 0.8f),
        textAlign = TextAlign.Center
    )
}

@Composable
fun ResultStateContent(timeMs: Long) {
    Text(
        text = "${timeMs}ms",
        style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black, fontSize = 96.sp),
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Your Reaction Time",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun TooSoonStateContent() {
    Text(
        text = "TOO SOON!",
        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
        color = Color.White
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Wait for the screen to turn green. Tap anywhere to try again.",
        style = MaterialTheme.typography.titleLarge,
        color = Color.White.copy(alpha = 0.9f),
        textAlign = TextAlign.Center
    )
}