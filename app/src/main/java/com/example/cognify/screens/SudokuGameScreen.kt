package com.example.cognify.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.cognify.data.SudokuCell


// --- Game Logic Stub (Requires separate ViewModel/Logic) ---
// Note: Actual Sudoku generation and validation logic is complex and should be moved
// to a ViewModel or dedicated class for a production app.

fun generatePuzzle(difficulty: Int): List<SudokuCell> {
    // 9x9 grid, 81 cells total. This is a simplified starting board for demonstration.
    val initialBoard = listOf(
        5, 3, 0, 0, 7, 0, 0, 0, 0,
        6, 0, 0, 1, 9, 5, 0, 0, 0,
        0, 9, 8, 0, 0, 0, 0, 6, 0,
        8, 0, 0, 0, 6, 0, 0, 0, 3,
        4, 0, 0, 8, 0, 3, 0, 0, 1,
        7, 0, 0, 0, 2, 0, 0, 0, 6,
        0, 6, 0, 0, 0, 0, 2, 8, 0,
        0, 0, 0, 4, 1, 9, 0, 0, 5,
        0, 0, 0, 0, 8, 0, 0, 7, 9
    )

    return initialBoard.map { value ->
        SudokuCell(
            value = value,
            isFixed = value != 0
        )
    }
}

// --- Main Composable ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuGameScreen(onBack: () -> Unit) {
    // Current state of the 81 cells
    var cells by remember { mutableStateOf(generatePuzzle(1)) }
    // Index of the currently selected cell (0 to 80)
    var selectedIndex by remember { mutableStateOf(-1) }
    // State to control the visibility of the number input dialog
    var showNumberPad by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sudoku Challenge") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // --- 1. Sudoku Grid ---
            SudokuGrid(
                cells = cells,
                selectedIndex = selectedIndex,
                onCellClick = { index ->
                    // Only allow clicking on cells that are not fixed
                    if (!cells[index].isFixed) {
                        // Deselect all, then select the new one
                        cells = cells.mapIndexed { i, cell ->
                            cell.copy(isSelected = i == index)
                        }
                        selectedIndex = index
                        showNumberPad = true // Open the number pad on click
                    }
                }
            )

            // --- 2. Number Input Dialog ---
            if (showNumberPad && selectedIndex != -1) {
                NumberInputDialog(
                    onDismiss = { showNumberPad = false },
                    onNumberSelected = { number ->
                        cells = cells.toMutableList().apply {
                            this[selectedIndex] = this[selectedIndex].copy(value = number)
                        }.toList()
                        showNumberPad = false
                    }
                )
            }

            // Placeholder for controls and stats (Timer, Reset, Hints)
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Difficulty: Easy",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// --- Sudoku Grid Composable ---

@Composable
fun ColumnScope.SudokuGrid(
    cells: List<SudokuCell>,
    selectedIndex: Int,
    onCellClick: (Int) -> Unit
) {
    // Use AspectRatio to ensure the grid is square
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .weight(1f, fill = false) // Allow grid to size based on AspectRatio
            .padding(top = 16.dp)
            .border(3.dp, MaterialTheme.colorScheme.onBackground)
    ) {
        // 9 Rows
        repeat(9) { row ->
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                // 9 Columns
                repeat(9) { col ->
                    val index = row * 9 + col
                    val cell = cells[index]

                    SudokuCellView(
                        cell = cell,
                        onCellClick = { onCellClick(index) },
                        // Check for 3x3 block border
                        isHeavyBorder = (col + 1) % 3 == 0 && col != 8
                    )
                }
            }
        }
    }
}

// --- Sudoku Cell Composable ---

@Composable
fun RowScope.SudokuCellView(
    cell: SudokuCell,
    onCellClick: () -> Unit,
    isHeavyBorder: Boolean
) {
    val borderColor = if ((cell.value != 0) && cell.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
    val cellWeight = 1f

    Box(
        modifier = Modifier
            .weight(cellWeight)
            .fillMaxHeight()
            .background(
                when {
                    cell.isFixed -> MaterialTheme.colorScheme.surfaceVariant
                    cell.isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    else -> MaterialTheme.colorScheme.background
                }
            )
            .border(
                width = if (isHeavyBorder) 3.dp else 1.dp,
                color = if (isHeavyBorder) MaterialTheme.colorScheme.onBackground else borderColor
            )
            .clickable(enabled = !cell.isFixed, onClick = onCellClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (cell.value == 0) "" else cell.value.toString(),
            fontSize = 20.sp,
            fontWeight = if (cell.isFixed) FontWeight.Bold else FontWeight.SemiBold,
            color = if (cell.isFixed) MaterialTheme.colorScheme.onSurfaceVariant
            else if (cell.isError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onBackground
        )
    }
}

// --- Number Input Dialog Composable ---

@Composable
fun NumberInputDialog(
    onDismiss: () -> Unit,
    onNumberSelected: (Int) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Select Number",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // 3x3 Grid for numbers 1-9
                Column {
                    (0..2).forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            (1..3).forEach { col ->
                                val number = row * 3 + col
                                NumberInputButton(
                                    number = number,
                                    onNumberSelected = onNumberSelected
                                )
                            }
                        }
                    }
                }

                // Erase/Clear Button
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { onNumberSelected(0) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Erase")
                }
            }
        }
    }
}

@Composable
fun NumberInputButton(number: Int, onNumberSelected: (Int) -> Unit) {
    Button(
        onClick = { onNumberSelected(number) },
        modifier = Modifier.size(50.dp).padding(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(number.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}