package com.example.cognify.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GamesListScreen(
    onPlayMemory: () -> Unit,
    onPlayReaction: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Games", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onPlayMemory, modifier = Modifier.fillMaxWidth()) {
            Text("Memory Match")
        }
        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onPlayReaction, modifier = Modifier.fillMaxWidth()) {
            Text("Reaction Test")
        }
    }
}
