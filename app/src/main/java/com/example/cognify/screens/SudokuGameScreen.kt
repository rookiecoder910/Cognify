package com.example.cognify.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh

import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.window.Dialog
import com.example.cognify.data.SudokuCell
import kotlinx.coroutines.delay
import kotlin.random.Random

// Enhanced puzzle generation with multiple difficulty levels
fun generatePuzzle(difficulty: Int): List<SudokuCell> {
    val solutions = listOf(
        // Easy puzzle
        listOf(
            5, 3, 4, 6, 7, 8, 9, 1, 2,
            6, 7, 2, 1, 9, 5, 3, 4, 8,
            1, 9, 8, 3, 4, 2, 5, 6, 7,
            8, 5, 9, 7, 6, 1, 4, 2, 3,
            4, 2, 6, 8, 5, 3, 7, 9, 1,
            7, 1, 3, 9, 2, 4, 8, 5, 6,
            9, 6, 1, 5, 3, 7, 2, 8, 4,
            2, 8, 7, 4, 1, 9, 6, 3, 5,
            3, 4, 5, 2, 8, 6, 1, 7, 9
        ),
        // Medium puzzle
        listOf(
            8, 3, 5, 4, 1, 6, 9, 2, 7,
            2, 9, 6, 8, 5, 7, 4, 3, 1,
            4, 1, 7, 2, 9, 3, 6, 5, 8,
            5, 6, 9, 1, 3, 4, 7, 8, 2,
            1, 2, 3, 6, 7, 8, 5, 4, 9,
            7, 4, 8, 5, 2, 9, 1, 6, 3,
            6, 5, 2, 7, 8, 1, 3, 9, 4,
            9, 8, 1, 3, 4, 5, 2, 7, 6,
            3, 7, 4, 9, 6, 2, 8, 1, 5
        ),
        // Hard puzzle
        listOf(
            7, 8, 4, 1, 5, 9, 3, 2, 6,
            5, 3, 9, 6, 7, 2, 8, 4, 1,
            1, 2, 6, 4, 3, 8, 7, 5, 9,
            9, 5, 7, 8, 2, 1, 4, 6, 3,
            4, 6, 1, 3, 9, 7, 5, 8, 2,
            3, 8, 2, 5, 4, 6, 9, 1, 7,
            8, 7, 5, 2, 1, 4, 6, 3, 9,
            6, 1, 3, 9, 8, 5, 2, 7, 4,
            2, 4, 9, 7, 6, 3, 1, 5, 8
        )
    )

    val solution = solutions[difficulty.coerceIn(0, 2)]
    val cellsToRemove = when (difficulty) {
        0 -> 35 // Easy
        1 -> 45 // Medium
        else -> 55 // Hard
    }

    val indicesToRemove = (0 until 81).shuffled(Random(System.currentTimeMillis())).take(cellsToRemove)

    return solution.mapIndexed { index, value ->
        val shouldRemove = index in indicesToRemove
        SudokuCell(
            value = if (shouldRemove) 0 else value,
            isFixed = !shouldRemove
        )
    }
}

fun validateSudoku(cells: List<SudokuCell>): Pair<Boolean, List<Int>> {
    val errorIndices = mutableListOf<Int>()

    // Check rows
    for (row in 0 until 9) {
        val values = (0 until 9).map { col -> cells[row * 9 + col].value }.filter { it != 0 }
        if (values.size != values.toSet().size) {
            (0 until 9).forEach { col ->
                val idx = row * 9 + col
                if (cells[idx].value != 0 && values.count { it == cells[idx].value } > 1) {
                    errorIndices.add(idx)
                }
            }
        }
    }

    // Check columns
    for (col in 0 until 9) {
        val values = (0 until 9).map { row -> cells[row * 9 + col].value }.filter { it != 0 }
        if (values.size != values.toSet().size) {
            (0 until 9).forEach { row ->
                val idx = row * 9 + col
                if (cells[idx].value != 0 && values.count { it == cells[idx].value } > 1) {
                    errorIndices.add(idx)
                }
            }
        }
    }

    // Check 3x3 boxes
    for (boxRow in 0 until 3) {
        for (boxCol in 0 until 3) {
            val values = mutableListOf<Int>()
            val indices = mutableListOf<Int>()
            for (r in 0 until 3) {
                for (c in 0 until 3) {
                    val idx = (boxRow * 3 + r) * 9 + (boxCol * 3 + c)
                    indices.add(idx)
                    if (cells[idx].value != 0) values.add(cells[idx].value)
                }
            }
            if (values.size != values.toSet().size) {
                indices.forEach { idx ->
                    if (cells[idx].value != 0 && values.count { it == cells[idx].value } > 1) {
                        errorIndices.add(idx)
                    }
                }
            }
        }
    }

    val isComplete = cells.all { it.value != 0 } && errorIndices.isEmpty()
    return Pair(isComplete, errorIndices.distinct())
}

@Composable
fun SudokuGameScreen(onBack: () -> Unit) {
    var difficulty by remember { mutableStateOf(0) }
    var cells by remember { mutableStateOf(generatePuzzle(difficulty)) }
    var selectedIndex by remember { mutableStateOf(-1) }
    var showNumberPad by remember { mutableStateOf(false) }
    var timeElapsed by remember { mutableStateOf(0) }
    var isGameActive by remember { mutableStateOf(true) }
    var showVictory by remember { mutableStateOf(false) }
    var mistakes by remember { mutableStateOf(0) }

    // Validate and check for errors
    val (isComplete, errorIndices) = validateSudoku(cells)

    LaunchedEffect(isComplete) {
        if (isComplete) {
            isGameActive = false
            delay(500)
            showVictory = true
        }
    }

    // Timer
    LaunchedEffect(isGameActive) {
        if (isGameActive) {
            while (true) {
                delay(1000)
                timeElapsed++
            }
        }
    }

    val primaryColor = Color(0xFF6366F1)
    val secondaryColor = Color(0xFF06B6D4)
    val accentColor = Color(0xFFF59E0B)
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
            // Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
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
                            text = "Sudoku",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = primaryColor
                            )
                        )
                        Text(
                            text = when (difficulty) {
                                0 -> "Easy Mode"
                                1 -> "Medium Mode"
                                else -> "Hard Mode"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    IconButton(
                        onClick = {
                            cells = generatePuzzle(difficulty)
                            selectedIndex = -1
                            timeElapsed = 0
                            mistakes = 0
                            isGameActive = true
                            showVictory = false
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "New Game",
                            tint = accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SudokuStatCard(
                    modifier = Modifier.weight(1f),
                    label = "Time",
                    value = "${timeElapsed / 60}:${(timeElapsed % 60).toString().padStart(2, '0')}",
                    icon = Icons.Default.Build,
                    color = secondaryColor
                )
                SudokuStatCard(
                    modifier = Modifier.weight(1f),
                    label = "Filled",
                    value = "${cells.count { it.value != 0 }}/81",
                    icon = Icons.Default.CheckCircle,
                    color = successColor
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sudoku Grid
            EnhancedSudokuGrid(
                cells = cells.mapIndexed { index, cell ->
                    cell.copy(isError = index in errorIndices)
                },
                selectedIndex = selectedIndex,
                primaryColor = primaryColor,
                errorColor = errorColor,
                onCellClick = { index ->
                    if (!cells[index].isFixed) {
                        selectedIndex = index
                        showNumberPad = true
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Number Pad
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Quick Input",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        (1..9).forEach { number ->
                            NumberButton(
                                number = number,
                                enabled = selectedIndex != -1 && !cells[selectedIndex].isFixed,
                                primaryColor = primaryColor,
                                onClick = {
                                    if (selectedIndex != -1 && !cells[selectedIndex].isFixed) {
                                        cells = cells.toMutableList().apply {
                                            this[selectedIndex] = this[selectedIndex].copy(value = number)
                                        }
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (selectedIndex != -1 && !cells[selectedIndex].isFixed) {
                                cells = cells.toMutableList().apply {
                                    this[selectedIndex] = this[selectedIndex].copy(value = 0)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedIndex != -1 && !cells[selectedIndex].isFixed,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = errorColor.copy(alpha = 0.2f),
                            contentColor = errorColor
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Clear Cell", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (showVictory) {
            SudokuVictoryOverlay(
                time = timeElapsed,
                difficulty = difficulty,
                primaryColor = primaryColor,
                successColor = successColor,
                onNewGame = {
                    cells = generatePuzzle(difficulty)
                    selectedIndex = -1
                    timeElapsed = 0
                    mistakes = 0
                    isGameActive = true
                    showVictory = false
                },
                onBack = onBack
            )
        }
    }
}

@Composable
fun EnhancedSudokuGrid(
    cells: List<SudokuCell>,
    selectedIndex: Int,
    primaryColor: Color,
    errorColor: Color,
    onCellClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            repeat(9) { row ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    repeat(9) { col ->
                        val index = row * 9 + col
                        val cell = cells[index]

                        EnhancedSudokuCell(
                            cell = cell,
                            isSelected = index == selectedIndex,
                            primaryColor = primaryColor,
                            errorColor = errorColor,
                            onClick = { onCellClick(index) },
                            hasRightBorder = (col + 1) % 3 == 0 && col != 8,
                            hasBottomBorder = (row + 1) % 3 == 0 && row != 8
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.EnhancedSudokuCell(
    cell: SudokuCell,
    isSelected: Boolean,
    primaryColor: Color,
    errorColor: Color,
    onClick: () -> Unit,
    hasRightBorder: Boolean,
    hasBottomBorder: Boolean
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(
                when {
                    cell.isError -> errorColor.copy(alpha = 0.15f)
                    isSelected -> primaryColor.copy(alpha = 0.2f)
                    cell.isFixed -> Color(0xFFF8FAFC)
                    else -> Color.White
                }
            )
            .border(
                width = 1.dp,
                color = Color(0xFFE2E8F0)
            )
            .then(
                if (hasRightBorder) Modifier.border(
                    width = 2.dp,
                    color = primaryColor.copy(alpha = 0.3f)
                ) else Modifier
            )
            .then(
                if (hasBottomBorder) Modifier.border(
                    width = 2.dp,
                    color = primaryColor.copy(alpha = 0.3f)
                ) else Modifier
            )
            .clickable(enabled = !cell.isFixed, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (cell.value != 0) {
            Text(
                text = cell.value.toString(),
                fontSize = 22.sp,
                fontWeight = if (cell.isFixed) FontWeight.ExtraBold else FontWeight.Bold,
                color = when {
                    cell.isError -> errorColor
                    cell.isFixed -> Color(0xFF475569)
                    else -> primaryColor
                }
            )
        }
    }
}

@Composable
fun SudokuStatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = color
                    )
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
fun NumberButton(
    number: Int,
    enabled: Boolean,
    primaryColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (enabled) primaryColor.copy(alpha = 0.1f) else Color(0xFFF1F5F9))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (enabled) primaryColor else Color.Gray
        )
    }
}

@Composable
fun SudokuVictoryOverlay(
    time: Int,
    difficulty: Int,
    primaryColor: Color,
    successColor: Color,
    onNewGame: () -> Unit,
    onBack: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Victory")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
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
                    modifier = Modifier.scale(scale)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Puzzle Solved!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = successColor,
                        fontSize = 28.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = when (difficulty) {
                        0 -> "Easy Level"
                        1 -> "Medium Level"
                        else -> "Hard Level"
                    },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = primaryColor,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(28.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = primaryColor.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "${time / 60}:${(time % 60).toString().padStart(2, '0')}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = primaryColor,
                                fontSize = 40.sp
                            )
                        )
                        Text(
                            text = "Completion Time",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onNewGame,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = successColor
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(vertical = 18.dp),
                    elevation = ButtonDefaults.buttonElevation(8.dp)
                ) {
                    Text(
                        text = "New Game",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(vertical = 18.dp)
                ) {
                    Text(
                        text = "Back to Menu",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}