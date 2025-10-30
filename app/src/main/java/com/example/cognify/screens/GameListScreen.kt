package com.example.cognify.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.LocalIndication // Import for modern ripple
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesListScreen(
    onPlayMemory: () -> Unit,
    onPlayReaction: () -> Unit,
    onPlaySudoku: () -> Unit
) {
    // Define a clear, appealing color palette for the list
    val primaryColor = Color(0xFF1E88E5)
    val secondaryColor = Color(0xFF00ACC1) // Using this for the animation too
    val accentColor = Color(0xFF8BC34A) // A new accent for the animation

    // --- Animated Background Effect Setup ---
    val animationColors = remember { listOf(
        primaryColor.copy(alpha = 0.6f),
        secondaryColor.copy(alpha = 0.7f),
        accentColor.copy(alpha = 0.5f)
    )}

    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundBlobAnimation")

    // Animate multiple offsets for a more "wavy" effect
    val x1Offset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(tween(25000, easing = LinearEasing), RepeatMode.Reverse), label = "x1"
    )
    val y1Offset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing), RepeatMode.Reverse), label = "y1"
    )
    val x2Offset by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0f, animationSpec = infiniteRepeatable(tween(30000, easing = LinearEasing), RepeatMode.Reverse), label = "x2"
    )
    val y2Offset by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0f, animationSpec = infiniteRepeatable(tween(22000, easing = LinearEasing), RepeatMode.Reverse), label = "y2"
    )

    // Create a radial gradient that moves and scales
    val animatedBrush = Brush.radialGradient(
        colors = animationColors,
        center = Offset(x1Offset * 2000, y1Offset * 2000), // Larger offsets for movement
        radius = (1f - x2Offset) * 1500 + 500 // Animate radius for a "breathing" effect
    )
    // --- End Animated Background Effect Setup ---

    // --- Animated Text Logic ---
    val motivationalTexts = remember {
        listOf(
            "Select a game to boost your cognitive skills!",
            "Challenge your memory, speed, and logic!",
            "Track your progress and stay sharp today!"
        )
    }
    var textIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            textIndex = (textIndex + 1) % motivationalTexts.size
        }
    }
    // --- End Animated Text Logic ---

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Game Catalog",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor, // Keep TopAppBar solid for clarity
                )
            )
        },
        containerColor = Color.Transparent, // Make Scaffold transparent to see background Box
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBrush) // Apply the animated brush here
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    AnimatedContent(
                        targetState = motivationalTexts[textIndex],
                        transitionSpec = {
                            (slideInVertically { height -> height } + fadeIn()).togetherWith(
                                slideOutVertically { height -> -height } + fadeOut()
                            ).using(
                                SizeTransform(clip = false)
                            )
                        }, label = "Motivational Text Animation"
                    ) { targetText ->
                        Text(
                            targetText,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White, // Text color needs to contrast with animated background
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp, top = 8.dp)
                        )
                    }
                }

                // Game Cards (Keep background white to stand out against animation)
                item {
                    GameCard(
                        title = "Sudoku Challenge",
                        description = "Boost logic and problem-solving with number puzzles.",
                        icon = Icons.Default.DateRange,
                        iconTint = Color(0xFF673AB7),
                        onClick = onPlaySudoku
                    )
                }
                item {
                    GameCard(
                        title = "Memory Match",
                        description = "Sharpen your recall by finding matching pairs.",
                        icon = Icons.Default.Face,
                        iconTint = Color(0xFF4CAF50),
                        onClick = onPlayMemory
                    )
                }
                item {
                    GameCard(
                        title = "Reaction Test",
                        description = "Test your speed! Click as soon as the screen changes.",
                        icon = Icons.Default.Build,
                        iconTint = Color(0xFFFFC107),
                        onClick = onPlayReaction
                    )
                }
            }
        }
    }
}

// --- Reusable Game Card Component (Using LocalIndication for modern ripple) ---
@Composable
fun GameCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animate the card's elevation: higher when not pressed, slightly lower when pressed
    val cardElevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 8.dp,
        animationSpec = tween(150), label = "CardElevationAnimation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)) // Increased corner radius for a softer look
            .clickable(
                interactionSource = interactionSource,
                // Using the recommended default indication (resolves the deprecation warning)
                indication = LocalIndication.current,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        // Use the animated elevation
        elevation = CardDefaults.cardElevation(cardElevation)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp) // Increased padding for more white space
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(64.dp) // Slightly larger icon container
                    .clip(CircleShape) // Use a smooth circle shape
                    // Use a more opaque background color
                    .background(iconTint.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "$title Icon",
                    tint = iconTint,
                    modifier = Modifier.size(38.dp) // Slightly larger icon size
                )
            }

            Spacer(modifier = Modifier.width(20.dp)) // Increased horizontal spacing

            // --- 2. Text Content Area ---
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold, // Use ExtraBold for better emphasis
                        fontSize = 20.sp
                    ),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray // Use DarkGray for slightly better readability
                )
            }
        }
    }
}