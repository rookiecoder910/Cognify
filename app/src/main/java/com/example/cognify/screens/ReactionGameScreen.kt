package com.example.cognify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun ReactionGameScreen(onBack: () -> Unit) {
    var color by remember { mutableStateOf(Color.Red) }
    var startTime by remember { mutableStateOf(0L) }
    var reactionTime by remember { mutableStateOf<Long?>(null) }
    var waiting by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Reaction Test", style = MaterialTheme.typography.headlineSmall)
            androidx.compose.material3.TextButton(onClick = onBack) { Text("Back") }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(color)
                .clickable {
                    if (!waiting && color == Color.Green) {
                        reactionTime = System.currentTimeMillis() - startTime
                    }
                }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            reactionTime = null
            waiting = true
            color = Color.Red
            // random delay
            val delayMs = Random.nextLong(1000, 3500)
            androidx.compose.runtime.LaunchedEffect(Unit) {
                delay(delayMs)
                color = Color.Green
                startTime = System.currentTimeMillis()
                waiting = false
            }
        }) {
            Text("Start")
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Reaction: ${reactionTime?.toString() ?: "--"} ms")
    }
}
