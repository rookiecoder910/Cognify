package com.example.cognify.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

// --- 1. Game Level Configuration ---
data class GameLevel(val rows: Int, val cols: Int, val theme: List<String>) {
    val size: Int = rows * cols
}

val gameLevels = listOf(
    // Level 1: 2x2 (4 cards)
    GameLevel(2, 2, listOf("🍎", "🍌", "🍇", "🍓", "🍉", "🍍", "🥭").take(2)),
    // Level 2: 4x3 (12 cards)
    GameLevel(4, 3, listOf("🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼").take(6)),
    // Level 3: 4x4 (16 cards)
    GameLevel(4, 4, listOf("⚽", "🏀", "🏈", "⚾", "🥎", "🎾", "🏐", "🏉", "🥏", "🎱").take(8))
)

// --- 2. Main Composable ---
@Composable
fun MemoryGameScreen(onBack: () -> Unit) {
    var currentLevelIndex by remember { mutableStateOf(0) }
    val currentLevel = gameLevels[currentLevelIndex]

    // Use a key to reset the GameContent when the level changes
    key(currentLevelIndex) {
        GameContent(
            level = currentLevel,
            onLevelComplete = {
                if (currentLevelIndex < gameLevels.size - 1) {
                    currentLevelIndex++
                } else {
                    // Game finished!
                    // Optionally navigate to a final screen or reset
                }
            },
            onRestart = { currentLevelIndex = 0 },
            onBack = onBack
        )
    }
}

// --- 3. Game Logic and UI ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameContent(
    level: GameLevel,
    onLevelComplete: () -> Unit,
    onRestart: () -> Unit,
    onBack: () -> Unit
) {
    val totalCards = level.size
    val pairs = totalCards / 2
    val theme = level.theme.shuffled(Random(System.currentTimeMillis())).take(pairs)

    // Create the card deck, shuffle it
    val base = remember { (theme + theme).shuffled(Random(System.currentTimeMillis())) }

    var revealed by remember { mutableStateOf(List(totalCards) { false }) }
    var matched by remember { mutableStateOf(List(totalCards) { false }) }
    var firstIndex by remember { mutableStateOf(-1) }
    var moves by remember { mutableStateOf(0) }
    var canClick by remember { mutableStateOf(true) }

    val isGameComplete = matched.all { it }

    // In MemoryGameScreen.kt, replace the entire LaunchedEffect block with this:

    // Logic to handle card flipping and match checking
    LaunchedEffect(revealed) {
        // Count how many cards are currently revealed (true) but not matched (false)
        val revealedButNotMatchedIndices = revealed.mapIndexedNotNull { index, isRevealed ->
            if (isRevealed && !matched[index]) index else null
        }

        if (revealedButNotMatchedIndices.size == 2) {
            canClick = false
            delay(1000) // 1 second delay to see the cards

            val index1 = revealedButNotMatchedIndices[0]
            val index2 = revealedButNotMatchedIndices[1]

            if (base[index1] == base[index2]) {
                // Match! Update 'matched' state
                matched = matched.toMutableList().apply {
                    this[index1]=true
                    this[index2]=true
                }.toList()
            } else {
                // Mismatch, flip them back
                revealed = revealed.mapIndexed { i, r ->
                    if (i == index1 || i == index2) false else r
                }
            }

            // Reset firstIndex (already handled implicitly by the new logic, but safe to keep)
            firstIndex = -1
            canClick = true
        }

        // Check for game completion after every successful match
        if (isGameComplete) {
            delay(500)
            onLevelComplete()
        }
    }

    // --- UI Structure ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Level ${gameLevels.indexOf(level) + 1} (${level.rows}x${level.cols})") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onRestart() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Restart Game")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Stats Panel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(label = "Moves", value = moves.toString(), color = MaterialTheme.colorScheme.primary)
                StatItem(label = "Matches", value = matched.count { it }.toString(), color = MaterialTheme.colorScheme.secondary)
            }

            // Game Grid (using LazyVerticalGrid for dynamic columns)
            LazyVerticalGrid(
                columns = GridCells.Fixed(level.cols),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(base) { idx, emoji ->
                    MemoryCard(
                        emoji = emoji,
                        isRevealed = revealed[idx],
                        isMatched = matched[idx],
                        canFlip = canClick && !matched[idx] && (firstIndex == -1 || firstIndex != idx),
                        onClick = {
                            if (!revealed[idx] && canClick && firstIndex != idx) {
                                moves++
                                revealed = revealed.toMutableList().apply { this[idx] = true }.toList()

                                if (firstIndex == -1) {
                                    firstIndex = idx
                                } else {
                                    val secondIndex = idx
                                    if (base[firstIndex] == base[secondIndex]) {
                                        matched = matched.toMutableList().apply {
                                            this[firstIndex] = true
                                            this[secondIndex] = true
                                        }.toList()
                                    }
                                    // The flip-back logic is now handled in the LaunchedEffect
                                }
                            }
                        }
                    )
                }
            }
        }

        // Level Complete Overlay
        if (isGameComplete) {
            LevelCompleteOverlay(level = gameLevels.indexOf(level) + 1, onNextLevel = onLevelComplete)
        }
    }
}

// --- Reusable Components ---

@Composable
fun MemoryCard(
    emoji: String,
    isRevealed: Boolean,
    isMatched: Boolean,
    canFlip: Boolean,
    onClick: () -> Unit
) {
    val cardColor = when {
        isMatched -> MaterialTheme.colorScheme.tertiaryContainer
        isRevealed -> MaterialTheme.colorScheme.surface
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .aspectRatio(1f) // Ensures cards are square
            .clickable(enabled = canFlip, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(if (isRevealed || isMatched) 2.dp else 6.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = if (isRevealed || isMatched) emoji else "?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = if (isRevealed || isMatched) 32.sp else 48.sp,
                    color = if (isRevealed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
fun LevelCompleteOverlay(level: Int, onNextLevel: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(false) {}, // Block clicks underneath
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "🎉 Level $level Cleared! 🎉",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (level < gameLevels.size) "Ready for the next challenge?" else "You beat all levels!",
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onNextLevel,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = level < gameLevels.size
                ) {
                    Text(if (level < gameLevels.size) "Continue to Level ${level + 1}" else "Finish")
                }
            }
        }
    }
}