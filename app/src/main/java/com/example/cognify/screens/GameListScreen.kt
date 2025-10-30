package com.example.cognify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Face

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesListScreen(
    onPlayMemory: () -> Unit,
    onPlayReaction: () -> Unit
) {
    // Define a clear, appealing color palette for the list
    val primaryColor = Color(0xFF1E88E5) // Blue 600
    val accentColor = Color(0xFFFF9800) // Orange 500 for secondary elements
    val backgroundColor = Color(0xFFF0F4F8) // Very light blue/gray background

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
                    containerColor = primaryColor,
                )
            )
        },
        containerColor = backgroundColor // Use the custom background color
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Select a game to boost your cognitive skills!",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.Gray),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // --- Memory Match Game Card ---
            item {
                GameCard(
                    title = "Memory Match",
                    description = "Sharpen your recall by finding matching pairs.",
                    icon = Icons.Default.Face,
                    iconTint = Color(0xFF4CAF50), // Green for Memory
                    onClick = onPlayMemory
                )
            }

            // --- Reaction Test Game Card ---
            item {
                GameCard(
                    title = "Reaction Test",
                    description = "Test your speed! Click as soon as the screen changes.",
                    icon = Icons.Default.Build,
                    iconTint = Color(0xFFFFC107), // Yellow/Amber for Reaction
                    onClick = onPlayReaction
                )
            }

            // You can add more games here...
            /*
            item {
                GameCard(
                    title = "Number Sequence",
                    description = "Follow complex patterns to improve focus.",
                    icon = Icons.Default.Filter1, // Example Icon
                    iconTint = accentColor,
                    onClick = { /* onPlaySequence() */ }
                )
            }
            */
        }
    }
}

// --- Reusable Game Card Component for Professional Look ---

@Composable
fun GameCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Area
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    // Subtle background for the icon area
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "$title Icon",
                    tint = iconTint,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Content Area
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}