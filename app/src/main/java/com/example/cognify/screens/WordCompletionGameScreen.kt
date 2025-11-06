package com.example.cognify.screens



import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.cognify.data.WordPuzzle
import kotlinx.coroutines.delay


@Composable
fun WordCompletionGameScreen(onBack: () -> Unit) {
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

    // Word puzzles
    val puzzles = remember {
        listOf(
            WordPuzzle("APPLE", "A red fruit that keeps the doctor away", "Food", "🍎"),
            WordPuzzle("HOUSE", "A place where you live", "Home", "🏠"),
            WordPuzzle("WATER", "You drink this every day", "Nature", "💧"),
            WordPuzzle("HAPPY", "A feeling of joy", "Emotion", "😊"),
            WordPuzzle("FAMILY", "Parents, children, grandparents", "People", "👨‍👩‍👧‍👦"),
            WordPuzzle("PHONE", "You call people with this", "Technology", "📱"),
            WordPuzzle("FLOWER", "Beautiful plant that blooms", "Nature", "🌸"),
            WordPuzzle("COFFEE", "Morning drink that wakes you up", "Drink", "☕"),
            WordPuzzle("FRIEND", "Someone you like to spend time with", "People", "🤝"),
            WordPuzzle("MUSIC", "Songs and melodies", "Entertainment", "🎵")
        )
    }

    var currentPuzzleIndex by remember { mutableStateOf(0) }
    var userInput by remember { mutableStateOf("") }
    var revealedLetters by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var score by remember { mutableStateOf(0) }
    var showHint by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    val currentPuzzle = puzzles[currentPuzzleIndex]
    val availableLetters = remember(currentPuzzleIndex) {
        val wordLetters = currentPuzzle.word.toList()
        val extraLetters = ('A'..'Z').shuffled().take(6 - wordLetters.size)
        (wordLetters + extraLetters).shuffled()
    }

    LaunchedEffect(currentPuzzleIndex) {
        // Reveal first and last letter automatically
        delay(500)
        revealedLetters = setOf(0, currentPuzzle.word.length - 1)
        userInput = ""
        showHint = false
    }

    fun checkAnswer() {
        isCorrect = userInput.equals(currentPuzzle.word, ignoreCase = true)
        if (isCorrect) {
            score += 10 + (revealedLetters.size * -2) // Bonus for using fewer hints
        }
        showResult = true
    }

    fun nextPuzzle() {
        if (currentPuzzleIndex < puzzles.size - 1) {
            currentPuzzleIndex++
            showResult = false
        }
    }

    fun useHint() {
        val unrevealedIndices = currentPuzzle.word.indices.filter { it !in revealedLetters }
        if (unrevealedIndices.isNotEmpty()) {
            revealedLetters = revealedLetters + unrevealedIndices.random()
            showHint = true
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
                            text = "Word Completion",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = primaryColor
                            )
                        )
                        Text(
                            text = "Puzzle ${currentPuzzleIndex + 1} of ${puzzles.size}",
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

            // Puzzle area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Category and Image
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentPuzzle.imageEmoji,
                            fontSize = 80.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = primaryColor.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = currentPuzzle.category,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor
                                )
                            )
                        }
                    }
                }

                // Hint
                AnimatedVisibility(
                    visible = showHint,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = currentPuzzle.hint,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF1F2937),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }

                // Word display with blanks
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
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        currentPuzzle.word.forEachIndexed { index, letter ->
                            LetterBox(
                                letter = if (index in revealedLetters) letter else '_',
                                isRevealed = index in revealedLetters,
                                userLetter = userInput.getOrNull(index),
                                primaryColor = primaryColor,
                                successColor = successColor
                            )
                            if (index < currentPuzzle.word.length - 1) {
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                }

                // Letter selection grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(availableLetters) { letter ->
                        LetterButton(
                            letter = letter,
                            isUsed = letter in userInput,
                            onClick = {
                                if (userInput.length < currentPuzzle.word.length) {
                                    userInput += letter
                                }
                            },
                            primaryColor = primaryColor
                        )
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { userInput = userInput.dropLast(1) },
                        enabled = userInput.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Icon(Icons.Default.Backspace, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Clear", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { useHint() },
                        enabled = revealedLetters.size < currentPuzzle.word.length - 2,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF59E0B)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Icon(Icons.Default.Help, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Hint", fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { checkAnswer() },
                    enabled = userInput.length == currentPuzzle.word.length,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = successColor
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(vertical = 18.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Check Answer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Result overlay
        if (showResult) {
            WordResultOverlay(
                isCorrect = isCorrect,
                correctWord = currentPuzzle.word,
                userWord = userInput,
                onNext = { nextPuzzle() },
                onRetry = {
                    showResult = false
                    userInput = ""
                },
                isLastPuzzle = currentPuzzleIndex == puzzles.size - 1,
                primaryColor = primaryColor,
                successColor = successColor,
                errorColor = errorColor
            )
        }
    }
}

@Composable
fun LetterBox(
    letter: Char,
    isRevealed: Boolean,
    userLetter: Char?,
    primaryColor: Color,
    successColor: Color
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .border(
                2.dp,
                when {
                    isRevealed -> primaryColor
                    userLetter != null -> successColor
                    else -> Color(0xFFE2E8F0)
                },
                RoundedCornerShape(12.dp)
            )
            .background(
                when {
                    isRevealed -> primaryColor.copy(alpha = 0.1f)
                    userLetter != null -> successColor.copy(alpha = 0.1f)
                    else -> Color.White
                },
                RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when {
                isRevealed -> letter.toString()
                userLetter != null -> userLetter.toString()
                else -> ""
            },
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = when {
                    isRevealed -> primaryColor
                    userLetter != null -> successColor
                    else -> Color(0xFF1F2937)
                }
            )
        )
    }
}

@Composable
fun LetterButton(
    letter: Char,
    isUsed: Boolean,
    onClick: () -> Unit,
    primaryColor: Color
) {
    Card(
        onClick = onClick,
        enabled = !isUsed,
        colors = CardDefaults.cardColors(
            containerColor = if (isUsed) Color(0xFFF1F5F9) else Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(if (isUsed) 0.dp else 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter.toString(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isUsed) Color.Gray else primaryColor
                )
            )
        }
    }
}

@Composable
fun WordResultOverlay(
    isCorrect: Boolean,
    correctWord: String,
    userWord: String,
    onNext: () -> Unit,
    onRetry: () -> Unit,
    isLastPuzzle: Boolean,
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
                    text = if (isCorrect) "🎉" else "💡",
                    fontSize = 72.sp,
                    modifier = Modifier.scale(scale)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (isCorrect) "Correct!" else "Not quite!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isCorrect) successColor else errorColor,
                        fontSize = 28.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!isCorrect) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = primaryColor.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Correct word:",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Gray
                                )
                            )
                            Text(
                                text = correctWord,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = primaryColor
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                if (isCorrect && !isLastPuzzle) {
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
                            "Next Word",
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