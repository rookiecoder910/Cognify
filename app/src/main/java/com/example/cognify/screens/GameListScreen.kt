package com.example.cognify.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Memory
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun GamesListScreen(
    onPlayMemory: () -> Unit,
    onPlayReaction: () -> Unit,
    onPlaySudoku: () -> Unit,

) {
    // Modern color palette matching HomeScreen
    val primaryColor = Color(0xFF6366F1) // Indigo
    val lightPrimary = Color(0xFF818CF8)
    val secondaryColor = Color(0xFF06B6D4) // Cyan
    val accentColor = Color(0xFFF59E0B) // Amber

    val animationColors = remember {
        listOf(
            Color(0xFFD999AF),
            Color(0xFFD47E9B),
            Color(0xFFCE4C78),
            Color(0xFFEF86AB)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundAnimation")

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

    val motivationalTexts = remember {
        listOf(
            "Choose Your Challenge",
            "Train Your Mind Daily",
            "Push Your Limits"
        )
    }
    var textIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3500)
            textIndex = (textIndex + 1) % motivationalTexts.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBrush)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AnimatedContent(
                    targetState = motivationalTexts[textIndex],
                    transitionSpec = {
                        (slideInVertically { height -> height } + fadeIn(
                            animationSpec = tween(600)
                        )).togetherWith(
                            slideOutVertically { height -> -height } + fadeOut(
                                animationSpec = tween(600)
                            )
                        ).using(
                            SizeTransform(clip = false)
                        )
                    }, label = "MotivationalText"
                ) { targetText ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = targetText,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Available Games",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    ),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 4.dp)
                )
            }

            item {
                GameCard(
                    title = "Sudoku Challenge",
                    description = "Solve challenging Sudoku puzzles",
                    icon = Icons.Default.Apps,
                    iconTint = Color(0xFF6172CD),
                    backgroundColor = Color(0xFFA4D0F3),
                    accentColor = Color(0xFFFFE6E6),
                    onClick = onPlaySudoku
                )
            }

            item {
                GameCard(
                    title = "Memory Match",
                    description = "Sharpen recall with matching pairs",
                    icon = Icons.Default.Memory,
                    iconTint = Color(0xFFEF764F), // Green
                    accentColor = Color(0xFFECE3E3),
                    onClick = onPlayMemory,
                    backgroundColor = Color(0xFFE8DEBB),
                )
            }

            item {
                GameCard(
                    title = "Reaction Test",
                    description = "Lightning-fast reflexes training",
                    icon = Icons.Default.FlashAuto,
                    iconTint = Color(0xFFF59E0B), // Amber
                    accentColor = Color(0xFFF6E2A0),
                    onClick = onPlayReaction,
                    backgroundColor = Color(0xFFEFCA59),
                )
            }
        }
    }
}

@Composable
fun GameCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconTint: Color,
    accentColor: Color,
    onClick: () -> Unit,
    backgroundColor: Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val cardElevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 12.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "CardElevation"
    )

    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "CardScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "IconAnimation")
    val iconRotation by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "iconRotation"
    )



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(cardScale)
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(
            containerColor =backgroundColor
        ),
        elevation = CardDefaults.cardElevation(cardElevation)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                iconTint.copy(alpha = 0.2f),
                                accentColor.copy(alpha = 0.15f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "$title Icon",
                    tint = iconTint,
                    modifier = Modifier
                        .size(40.dp)
                        .rotate(iconRotation)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        letterSpacing = (-0.3).sp
                    ),
                    color = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.2.sp
                    ),
                    color = Color(0xFF6B7280)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Play",
                    tint = iconTint,
                    modifier = Modifier
                        .size(28.dp)

                )
            }
        }
    }
}