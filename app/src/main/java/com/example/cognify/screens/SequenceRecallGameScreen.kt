package com.example.cognify.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.example.cognify.data.RoutineStep
import kotlinx.coroutines.delay



@Composable
fun SequenceRecallGameScreen(onBack: () -> Unit) {
    val primaryColor = Color(0xFF6366F1)
    val secondaryColor = Color(0xFF06B6D4)
    val successColor = Color(0xFF10B981)
    val errorColor = Color(0xFFEF4444)

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

    // Game levels with different routines
    val levels = remember {
        listOf(
            // Morning Routine
            listOf(
                RoutineStep(1, "☀️", "Wake up", 0),
                RoutineStep(2, "🪥", "Brush teeth", 1),
                RoutineStep(3, "🚿", "Take shower", 2),
                RoutineStep(4, "☕", "Have breakfast", 3),
                RoutineStep(5, "👔", "Get dressed", 4)
            ),
            // Mealtime Routine
            listOf(
                RoutineStep(1, "🧼", "Wash hands", 0),
                RoutineStep(2, "🪑", "Sit at table", 1),
                RoutineStep(3, "🍽️", "Eat meal", 2),
                RoutineStep(4, "🧃", "Drink water", 3),
                RoutineStep(5, "🧽", "Clean up", 4)
            ),
            // Bedtime Routine
            listOf(
                RoutineStep(1, "📺", "Turn off TV", 0),
                RoutineStep(2, "🪥", "Brush teeth", 1),
                RoutineStep(3, "🧴", "Apply lotion", 2),
                RoutineStep(4, "👕", "Change clothes", 3),
                RoutineStep(5, "🛏️", "Get in bed", 4),
                RoutineStep(6, "💤", "Sleep", 5)
            )
        )
    }

    var currentLevel by remember { mutableStateOf(0) }
    var gamePhase by remember { mutableStateOf(GamePhase.MEMORIZE) }
    var userOrder by remember { mutableStateOf<List<RoutineStep>>(emptyList()) }
    var availableSteps by remember { mutableStateOf<List<RoutineStep>>(emptyList()) }
    var showResult by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(10) }

    val currentRoutine = levels[currentLevel]

    LaunchedEffect(gamePhase) {
        if (gamePhase == GamePhase.MEMORIZE) {
            timeLeft = 10
            while (timeLeft > 0 && gamePhase == GamePhase.MEMORIZE) {
                delay(1000)
                timeLeft--
            }
            if (timeLeft == 0) {
                gamePhase = GamePhase.ARRANGE
                availableSteps = currentRoutine.shuffled()
            }
        }
    }

    fun checkAnswer() {
        isCorrect = userOrder.mapIndexed { index, step ->
            step.correctOrder == index
        }.all { it }

        if (isCorrect) {
            score += (currentRoutine.size * 10)
        }
        showResult = true
    }
    fun resetGame() {
        gamePhase = GamePhase.MEMORIZE
        userOrder = emptyList()
        availableSteps = emptyList()
        showResult = false
        isCorrect = false
        timeLeft = 10
    }
    fun nextLevel() {
        if (currentLevel < levels.size - 1) {
            currentLevel++
            resetGame()
        }
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBrush)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
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
                            text = "Daily Routine",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = primaryColor
                            )
                        )
                        Text(
                            text = "Level ${currentLevel + 1}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = secondaryColor.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "⭐ $score",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = secondaryColor
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Instructions
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (gamePhase) {
                        GamePhase.MEMORIZE -> {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Memorize the order: $timeLeft seconds",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = primaryColor
                                    )
                                )
                            }
                        }
                        GamePhase.ARRANGE -> {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.SwapVert,
                                    contentDescription = null,
                                    tint = successColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Arrange in correct order",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = successColor
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Game area
            when (gamePhase) {
                GamePhase.MEMORIZE -> {
                    MemorizePhase(
                        routine = currentRoutine,
                        primaryColor = primaryColor
                    )
                }
                GamePhase.ARRANGE -> {
                    ArrangePhase(
                        userOrder = userOrder,
                        availableSteps = availableSteps,
                        onStepSelected = { step ->
                            userOrder = userOrder + step
                            availableSteps = availableSteps - step
                        },
                        onStepRemoved = { step ->
                            availableSteps = availableSteps + step
                            userOrder = userOrder - step
                        },
                        onSubmit = { checkAnswer() },
                        primaryColor = primaryColor,
                        successColor = successColor
                    )
                }
            }
        }

        // Result overlay
        if (showResult) {
            ResultOverlay(
                isCorrect = isCorrect,
                score = currentRoutine.size * 10,
                onNext = {
                    showResult = false
                    nextLevel()
                },
                onRetry = {
                    showResult = false
                    resetGame()
                },
                isLastLevel = currentLevel == levels.size - 1,
                primaryColor = primaryColor,
                successColor = successColor,
                errorColor = errorColor
            )
        }
    }
}

enum class GamePhase {
    MEMORIZE, ARRANGE
}

@Composable
fun MemorizePhase(
    routine: List<RoutineStep>,
    primaryColor: Color
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(routine) { index, step ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(primaryColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF8FAFC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = step.emoji, fontSize = 36.sp)
                    }

                    Text(
                        text = step.action,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ArrangePhase(
    userOrder: List<RoutineStep>,
    availableSteps: List<RoutineStep>,
    onStepSelected: (RoutineStep) -> Unit,
    onStepRemoved: (RoutineStep) -> Unit,
    onSubmit: () -> Unit,
    primaryColor: Color,
    successColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // User's arrangement
        Text(
            text = "Your Order:",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (userOrder.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .border(
                                2.dp,
                                primaryColor.copy(alpha = 0.3f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tap items below to add",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray
                            )
                        )
                    }
                } else {
                    userOrder.forEachIndexed { index, step ->
                        ArrangedStepCard(
                            step = step,
                            index = index,
                            onRemove = { onStepRemoved(step) },
                            primaryColor = primaryColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Available steps
        Text(
            text = "Available Steps:",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(availableSteps.size) { index ->
                val step = availableSteps[index]
                AvailableStepCard(
                    step = step,
                    onClick = { onStepSelected(step) },
                    primaryColor = primaryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSubmit,
            enabled = userOrder.size == availableSteps.size + userOrder.size,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = successColor
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(vertical = 18.dp)
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Check Answer",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ArrangedStepCard(
    step: RoutineStep,
    index: Int,
    onRemove: () -> Unit,
    primaryColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = primaryColor.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(primaryColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Text(text = step.emoji, fontSize = 24.sp)

                Text(
                    text = step.action,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F2937)
                    )
                )
            }

            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = Color(0xFFEF4444)
                )
            }
        }
    }
}

@Composable
fun AvailableStepCard(
    step: RoutineStep,
    onClick: () -> Unit,
    primaryColor: Color
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8FAFC)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = step.emoji, fontSize = 28.sp)
            }

            Text(
                text = step.action,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                ),
                modifier = Modifier.weight(1f)
            )

            Icon(
                Icons.Default.Add,
                contentDescription = "Add",
                tint = primaryColor
            )
        }
    }
}

@Composable
fun ResultOverlay(
    isCorrect: Boolean,
    score: Int,
    onNext: () -> Unit,
    onRetry: () -> Unit,
    isLastLevel: Boolean,
    primaryColor: Color,
    successColor: Color,
    errorColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Result")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
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
                .fillMaxWidth(0.9f)
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
                    text = if (isCorrect) "🎉" else "💪",
                    fontSize = 72.sp,
                    modifier = Modifier.scale(scale)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (isCorrect) "Perfect!" else "Try Again",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isCorrect) successColor else errorColor,
                        fontSize = 28.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isCorrect)
                        "You remembered the routine correctly!"
                    else
                        "Let's practice the order again",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                )

                if (isCorrect) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = successColor.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "+$score points",
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = successColor
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                if (isCorrect && !isLastLevel) {
                    Button(
                        onClick = onNext,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = successColor
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(vertical = 18.dp)
                    ) {
                        Text(
                            "Next Level",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                } else if (!isCorrect) {
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(vertical = 18.dp)
                    ) {
                        Text(
                            "Try Again",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}