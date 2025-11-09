package com.example.cognify.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Planet data class
data class Planet(
    val name: String,
    val color: Color,
    val bgHue: Float,
    val icon: ImageVector,
    val games: List<Game>
)

data class Game(
    val title: String,
    val description: String,
    val difficulty: String,
    val icon: String,
    val onClick: () -> Unit
)

// Star data for background - immutable for better performance
data class Star(
    val x: Float,
    val y: Float,
    val size: Float,
    val baseOpacity: Float,
    val twinkleSpeed: Float,
    val initialPhase: Float
)

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GamesListScreen(
    onPlayMemory: () -> Unit,
    onPlayReaction: () -> Unit,
    onPlaySudoku: () -> Unit,
    onPlaySequence: () -> Unit,
    onPlayWord: () -> Unit,
) {
    val planets = remember {
        listOf(
            Planet(
                name = "Memory",
                color = Color(0xFF00F0FF),
                bgHue = 190f,
                icon = Icons.Default.Memory,
                games = listOf(
                    Game("Memory Match", "Sharpen recall with matching pairs", "Easy", "🧩", onPlayMemory),
//                    Game("Pattern Matrix", "Remember complex patterns", "Medium", "🎯", onPlayMemory),
//                    Game("Visual Recall", "Test your visual memory", "Hard", "👁️", onPlayMemory),
                )
            ),
            Planet(
                name = "Focus",
                color = Color(0xFF00FF88),
                bgHue = 160f,
                icon = Icons.Default.Visibility,
                games = listOf(
                    Game("Zen Target", "Find targets quickly", "Easy", "🎯", onPlayReaction),
                    Game("Flow State", "Maintain concentration", "Medium", "🌊", onPlayReaction),
                    Game("Laser Mind", "Ultimate focus challenge", "Hard", "⚡", onPlayReaction),
                )
            ),
            Planet(
                name = "Logic",
                color = Color(0xFFFFD700),
                bgHue = 55f,
                icon = Icons.Default.Apps,
                games = listOf(
                    Game("Sudoku Challenge", "Solve challenging puzzles", "Easy", "⚙️", onPlaySudoku),
//                    Game("Pattern Detective", "Find logical patterns", "Medium", "🔍", onPlaySudoku),
//                    Game("Quantum Puzzles", "Advanced reasoning", "Hard", "🧠", onPlaySudoku),
                )
            ),
            Planet(
                name = "Speed",
                color = Color(0xFFFF6B35),
                bgHue = 15f,
                icon = Icons.Default.FlashOn,
                games = listOf(
                    Game("Reaction Speed", "Quick reaction challenges", "Easy", "⚡", onPlayReaction),
//                    Game("Time Attack", "Beat the clock", "Medium", "⏱️", onPlayReaction),
//                    Game("Lightning Round", "Ultimate speed test", "Hard", "🔥", onPlayReaction),
                )
            ),
            Planet(
                name = "Creativity",
                color = Color(0xFFFF10F0),
                bgHue = 305f,
                icon = Icons.Default.Star,
                games = listOf(
                    Game("Word Puzzles", "Creative word challenges", "Easy", "🎨", onPlayWord),
//                    Game("Idea Generator", "Think outside the box", "Medium", "💡", onPlayWord),
//                    Game("Dream Weaver", "Ultimate creative test", "Hard", "✨", onPlayWord),
                )
            ),
            Planet(
                name = "Problem Solving",
                color = Color(0xFF8B5CF6),
                bgHue = 260f,
                icon = Icons.Default.Extension,
                games = listOf(
                    Game("Sequence Recall", "Remember number patterns", "Easy", "🧩", onPlaySequence),
//                    Game("Strategy King", "Plan your moves ahead", "Medium", "♟️", onPlaySequence),
//                    Game("Enigma Elite", "Crack complex problems", "Hard", "🔐", onPlaySequence),
                )
            ),
        )
    }

    val pagerState = rememberPagerState()
    val currentPlanet = planets[pagerState.currentPage]

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Animated color transition
    val animatedBgColor by animateColorAsState(
        targetValue = Color(currentPlanet.bgHue / 360f, 0.7f, 0.3f, 0.2f),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "bgColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0a0e27),
                        Color(0xFF1a0b2e),
                        Color(0xFF16213e),
                        Color(0xFF000000)
                    )
                )
            )
    ) {
        // Optimized dynamic background hue overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            animatedBgColor,
                            Color.Transparent
                        ),
                        center = Offset(0.5f, 0.4f),
                        radius = 800f
                    )
                )
        )

        // Star field background - optimized
        OptimizedStarField()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Enhanced Header
            EnhancedHeader()

            // Planet Carousel
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Orbit lines decoration
                OrbitLines(currentPlanet)

                PlanetCarousel(
                    pagerState = pagerState,
                    planets = planets,
                    onPlanetClick = {
                        showBottomSheet = true
                    }
                )
            }

            // Planet Name Display with smooth transition
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                PlanetInfoDisplay(currentPlanet)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enhanced Instructions
            Text(
                text = "← Swipe to explore • Tap to discover →",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp
            )
        }

        // Bottom Sheet for Games
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF0a0e27).copy(alpha = 0.98f),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .width(40.dp)
                            .height(4.dp)
                            .background(
                                currentPlanet.color.copy(alpha = 0.5f),
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            ) {
                GameBottomSheet(
                    planet = currentPlanet,
                    onClose = {
                        scope.launch {
                            sheetState.hide()
                            showBottomSheet = false
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun EnhancedHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "COGNIFY",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 4.sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color(0xFF00F0FF), CircleShape)
                )
                Text(
                    text = "NEURAL GALAXY",
                    fontSize = 11.sp,
                    color = Color(0xFF00F0FF).copy(alpha = 0.8f),
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            // Enhanced Streak counter
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.08f),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = Color(0xFFFF6B35),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "5 Days",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Enhanced User avatar with glow
            Box(
                modifier = Modifier.size(44.dp),
                contentAlignment = Alignment.Center
            ) {
                // Glow effect
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(1.2f)
                        .blur(10.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF00F0FF).copy(alpha = 0.4f),
                                    Color(0xFF8B5CF6).copy(alpha = 0.4f)
                                )
                            ),
                            CircleShape
                        )
                )

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF00F0FF),
                                    Color(0xFF8B5CF6)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OptimizedStarField() {
    // Create stars once and reuse
    val stars = remember {
        List(120) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 2.5f + 0.5f,
                baseOpacity = Random.nextFloat() * 0.6f + 0.3f,
                twinkleSpeed = Random.nextFloat() * 0.5f + 0.01f,
                initialPhase = Random.nextFloat() * 6.28f
            )
        }
    }

    var time by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(50)
            time += 0.05f
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        stars.forEach { star ->
            val phase = star.initialPhase + time * star.twinkleSpeed
            val twinkleOpacity = star.baseOpacity + sin(phase) * 0.3f

            drawCircle(
                color = Color.White.copy(alpha = twinkleOpacity),
                radius = star.size,
                center = Offset(star.x * size.width, star.y * size.height)
            )
        }
    }
}

@Composable
fun OrbitLines(planet: Planet) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbit")

    val dashPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dashPhase"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        // Draw three concentric orbit circles
        listOf(0.25f, 0.35f, 0.45f).forEachIndexed { index, scale ->
            val radius = size.minDimension * scale
            drawCircle(
                color = planet.color.copy(alpha = 0.08f - index * 0.02f),
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(
                    width = 1f,
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(10f, 15f),
                        phase = dashPhase + index * 5f
                    )
                )
            )
        }
    }
}

@Composable
fun PlanetInfoDisplay(planet: Planet) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Planet name with enhanced styling
        Text(
            text = "PLANET ${planet.name.uppercase()}",
            fontSize = 34.sp,
            fontWeight = FontWeight.Black,
            color = planet.color,
            lineHeight = 40.sp,
                    letterSpacing = 3.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(0.95f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Enhanced subtitle with decorative elements
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(planet.color.copy(alpha = 0.6f), CircleShape)
            )
            Text(
                text = "${planet.games.size} Cognitive Challenges",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Medium
            )
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(planet.color.copy(alpha = 0.6f), CircleShape)
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PlanetCarousel(
    pagerState: PagerState,
    planets: List<Planet>,
    onPlanetClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            count = planets.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 100.dp)
        ) { page ->
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffset
            val planet = planets[page]

            PlanetView(
                planet = planet,
                pageOffset = pageOffset,
                isSelected = page == pagerState.currentPage,
                onClick = onPlanetClick
            )
        }
    }
}

@Composable
fun PlanetView(
    planet: Planet,
    pageOffset: Float,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isClicked by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = when {
            isClicked -> 1.35f
            isSelected -> 1f
            else -> 0.65f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "PlanetScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (abs(pageOffset) < 0.5f) 1f else 0.35f,
        animationSpec = tween(400),
        label = "PlanetAlpha"
    )

    LaunchedEffect(isClicked) {
        if (isClicked) {
            delay(500)
            isClicked = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (isSelected) {
                    isClicked = true
                    onClick()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        EnhancedPlanet(
            planet = planet,
            alpha = alpha,
            isSelected = isSelected
        )
    }
}

@Composable
fun EnhancedPlanet(
    planet: Planet,
    alpha: Float,
    isSelected: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "PlanetRotation")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    Box(
        modifier = Modifier
            .size(170.dp)
            .offset(y = floatOffset.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            // Outer soft glow (wider, subtle)
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .clip(CircleShape)
                    .background(planet.color.copy(alpha = 0.25f))
                    .blur(30.dp) // smoother, wider diffusion
            )

            // Middle glow (pulsing core)
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(glowPulse) // gentle breathing animation
                    .clip(CircleShape)
                    .background(planet.color.copy(alpha = 0.4f))
                    .blur(18.dp)
            )
        }

        // Planet body with enhanced gradient
        Box(
            modifier = Modifier
                .size(145.dp)
                .rotate(rotation)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            planet.color.copy(alpha = 0.5f * alpha),
                            planet.color.copy(alpha = 0.3f * alpha),
                            planet.color.copy(alpha = 0.15f * alpha),
                            planet.color.copy(alpha = 0.05f * alpha)
                        ),
                        center = Offset(0.4f, 0.4f)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Surface texture with improved randomization
            val texturePositions = remember {
                List(8) {
                    Pair(
                        Random.nextInt(-50, 50).dp,
                        Random.nextInt(-50, 50).dp
                    )
                }
            }

            texturePositions.forEach { (offsetX, offsetY) ->
                Box(
                    modifier = Modifier
                        .size(Random.nextInt(20, 40).dp)
                        .offset(x = offsetX, y = offsetY)
                        .blur(10.dp)
                        .background(planet.color.copy(alpha = 0.12f), CircleShape)
                )
            }

            // Enhanced shine effect
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.35f * alpha),
                                Color.White.copy(alpha = 0.15f * alpha),
                                Color.Transparent
                            ),
                            center = Offset(0.35f, 0.35f),
                            radius = 0.6f
                        )
                    )
            )
        }

        // Planet icon with glow
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Icon glow
            Icon(
                imageVector = planet.icon,
                contentDescription = planet.name,
                tint = planet.color.copy(alpha = 0.3f),
                modifier = Modifier
                    .size(70.dp)
                    .blur(8.dp)
            )

            // Main icon
            Icon(
                imageVector = planet.icon,
                contentDescription = planet.name,
                tint = Color.White,
                modifier = Modifier
                    .size(62.dp)
                    .zIndex(1f)
            )
        }

        // Enhanced orbiting particles for selected planet
        if (isSelected) {
            repeat(3) { i ->
                val orbitRotation by infiniteTransition.animateFloat(
                    initialValue = i * 120f,
                    targetValue = 360f + i * 120f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3500, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "orbit$i"
                )

                val orbitRadius = 85f
                val offsetX = orbitRadius * cos(Math.toRadians(orbitRotation.toDouble())).toFloat()
                val offsetY = orbitRadius * sin(Math.toRadians(orbitRotation.toDouble())).toFloat()

                Box(
                    modifier = Modifier
                        .offset(x = offsetX.dp, y = offsetY.dp)
                        .size(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Particle glow
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .blur(4.dp)
                            .background(planet.color.copy(alpha = 0.6f), CircleShape)
                    )
                    // Particle core
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(planet.color, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun GameBottomSheet(
    planet: Planet,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // Enhanced Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 28.dp, top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Icon glow
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .blur(12.dp)
                            .background(
                                planet.color.copy(alpha = 0.4f),
                                RoundedCornerShape(14.dp)
                            )
                    )

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        planet.color.copy(alpha = 0.5f),
                                        planet.color.copy(alpha = 0.25f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = planet.icon,
                            contentDescription = null,
                            tint = planet.color,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = "${planet.name} Training",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = planet.color,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Choose your challenge",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Games list with enhanced spacing
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 36.dp)
        ) {
            itemsIndexed(planet.games) { index, game ->
                var isVisible by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    delay(index * 100L)
                    isVisible = true
                }

                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(animationSpec = tween(300))
                ) {
                    EnhancedGameCard(game = game, accentColor = planet.color)
                }
            }
        }
    }
}

@Composable
fun EnhancedGameCard(
    game: Game,
    accentColor: Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                isPressed = true
                game.onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.06f)
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with enhanced gradient
            Box(
                modifier = Modifier.size(70.dp),
                contentAlignment = Alignment.Center
            ) {
                // Icon glow
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .blur(10.dp)
                        .background(
                            accentColor.copy(alpha = 0.3f),
                            RoundedCornerShape(14.dp)
                        )
                )

                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = 0.5f),
                                    accentColor.copy(alpha = 0.25f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = game.icon,
                        fontSize = 36.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(18.dp))

            // Info section with improved typography
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.3.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = game.description,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Enhanced difficulty badge with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(getDifficultyColor(game.difficulty), CircleShape)
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = getDifficultyColor(game.difficulty).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = game.difficulty.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = getDifficultyColor(game.difficulty),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }

            // Enhanced play button with glow
            Box(
                modifier = Modifier.size(54.dp),
                contentAlignment = Alignment.Center
            ) {
                // Button glow
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .blur(8.dp)
                        .background(
                            accentColor.copy(alpha = 0.4f),
                            RoundedCornerShape(14.dp)
                        )
                )

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    accentColor,
                                    accentColor.copy(alpha = 0.85f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

fun getDifficultyColor(difficulty: String): Color {
    return when (difficulty.lowercase()) {
        "easy" -> Color(0xFF00FF88)
        "medium" -> Color(0xFFFFD700)
        "hard" -> Color(0xFFFF6B35)
        else -> Color.White
    }
}