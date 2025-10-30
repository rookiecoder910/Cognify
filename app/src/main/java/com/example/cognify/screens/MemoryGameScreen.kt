package com.example.cognify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun MemoryGameScreen(onBack: () -> Unit) {
    // small 2x2 grid demo for hackathon
    val base = listOf("🐶", "🐱", "🐶", "🐱").shuffled(Random(System.currentTimeMillis()))
    var revealed by remember { mutableStateOf(List(base.size) { false }) }
    var matched by remember { mutableStateOf(List(base.size) { false }) }
    var firstIndex by remember { mutableStateOf(-1) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Memory Match", style = MaterialTheme.typography.headlineSmall)
            androidx.compose.material3.TextButton(onClick = onBack) { Text("Back") }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Column {
            for (row in 0 until 2) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    for (col in 0 until 2) {
                        val idx = row * 2 + col
                        Card(
                            modifier = Modifier.size(140.dp).padding(8.dp).clickable(enabled = !matched[idx]) {
                                if (revealed[idx] || matched[idx]) return@clickable
                                val newRevealed = revealed.toMutableList()
                                newRevealed[idx] = true
                                revealed = newRevealed.toList()

                                if (firstIndex == -1) {
                                    firstIndex = idx
                                } else {
                                    if (base[firstIndex] == base[idx]) {
                                        val newMatched = matched.toMutableList()
                                        newMatched[firstIndex] = true
                                        newMatched[idx] = true
                                        matched = newMatched.toList()
                                    } else {
                                        // flip back after short delay
                                        androidx.compose.runtime.LaunchedEffect(Unit) {
                                            kotlinx.coroutines.delay(700)
                                            revealed = revealed.mapIndexed { i, _ -> matched[i] }
                                        }
                                    }
                                    firstIndex = -1
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors()
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                if (revealed[idx] || matched[idx]) {
                                    Text(base[idx], style = MaterialTheme.typography.headlineMedium)
                                } else {
                                    Text("?", style = MaterialTheme.typography.headlineMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
