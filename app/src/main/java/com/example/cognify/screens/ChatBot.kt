package com.example.cognify.screens



import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Message data class
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestions: List<String> = emptyList()
)

@Composable
fun AIAssistantScreen(
    onBack: () -> Unit
) {
    val primaryColor = Color(0xFF6366F1)
    val secondaryColor = Color(0xFF06B6D4)
    val accentColor = Color(0xFFF59E0B)
    val successColor = Color(0xFF10B981)

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

    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var userInput by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Initial welcome message
    LaunchedEffect(Unit) {
        delay(500)
        messages = listOf(
            ChatMessage(
                content = "Hello! I'm your Cognify AI Assistant. I'm here to help you with brain training, answer questions, and provide cognitive health tips. How can I assist you today?",
                isUser = false,
                suggestions = listOf(
                    "How do I improve memory?",
                    "What games should I play?",
                    "Tips for better focus",
                    "Track my progress"
                )
            )
        )
    }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        // Add user message
        messages = messages + ChatMessage(content = message, isUser = true)
        userInput = ""

        // Show typing indicator
        isTyping = true

        // Simulate AI response
        coroutineScope.launch {
            delay(1500)
            isTyping = false

            val aiResponse = generateAIResponse(message)
            messages = messages + aiResponse

            // Auto-scroll to bottom
            delay(100)
            listState.animateScrollToItem(messages.size - 1)
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
            ChatHeader(
                onBack = onBack,
                primaryColor = primaryColor,
                successColor = successColor
            )

            // Messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
                    ) {
                        if (message.isUser) {
                            UserMessageBubble(message = message, primaryColor = primaryColor)
                        } else {
                            AIMessageBubble(
                                message = message,
                                secondaryColor = secondaryColor,
                                onSuggestionClick = { suggestion ->
                                    sendMessage(suggestion)
                                }
                            )
                        }
                    }
                }

                // Typing indicator
                if (isTyping) {
                    item {
                        TypingIndicator(secondaryColor = secondaryColor)
                    }
                }
            }

            // Input area
            ChatInputArea(
                userInput = userInput,
                onInputChange = { userInput = it },
                onSend = { sendMessage(userInput) },
                primaryColor = primaryColor,
                accentColor = accentColor
            )
        }
    }
}

@Composable
private fun ChatHeader(
    onBack: () -> Unit,
    primaryColor: Color,
    successColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
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
                    .size(48.dp)
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.2f),
                                    successColor.copy(alpha = 0.2f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column {
                    Text(
                        text = "AI Assistant",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryColor
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(successColor)
                        )
                        Text(
                            text = "Online",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            IconButton(
                onClick = { /* Clear chat */ },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.1f))
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun UserMessageBubble(
    message: ChatMessage,
    primaryColor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = primaryColor
            ),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = 20.dp,
                bottomEnd = 4.dp
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = formatTime(message.timestamp),
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun AIMessageBubble(
    message: ChatMessage,
    secondaryColor: Color,
    onSuggestionClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(secondaryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = secondaryColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 20.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    ),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = message.content,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color(0xFF1F2937),
                                fontWeight = FontWeight.Medium,
                                lineHeight = 24.sp
                            )
                        )
                    }
                }

                // Suggestions
                if (message.suggestions.isNotEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Suggested questions:",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(start = 4.dp)
                        )

                        message.suggestions.forEach { suggestion ->
                            SuggestionChip(
                                suggestion = suggestion,
                                onClick = { onSuggestionClick(suggestion) },
                                secondaryColor = secondaryColor
                            )
                        }
                    }
                }

                Text(
                    text = formatTime(message.timestamp),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun SuggestionChip(
    suggestion: String,
    onClick: () -> Unit,
    secondaryColor: Color
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = suggestion,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun TypingIndicator(secondaryColor: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(secondaryColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.SmartToy,
                contentDescription = null,
                tint = secondaryColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 20.dp,
                bottomStart = 20.dp,
                bottomEnd = 20.dp
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(3) { index ->
                    val infiniteTransition = rememberInfiniteTransition(label = "dot$index")
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 0.5f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = index * 200),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scale"
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .background(secondaryColor)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatInputArea(
    userInput: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    primaryColor: Color,
    accentColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Voice input button
            IconButton(
                onClick = { /* Voice input */ },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.1f))
            ) {
                Icon(
                    Icons.Default.Mic,
                    contentDescription = "Voice input",
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Text input field
            TextField(
                value = userInput,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "Ask me anything...",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.Gray
                        )
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyLarge,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                maxLines = 4
            )

            // Send button
            IconButton(
                onClick = onSend,
                enabled = userInput.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (userInput.isNotBlank()) primaryColor else Color.Gray.copy(alpha = 0.2f)
                    )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (userInput.isNotBlank()) Color.White else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// Helper function to format timestamp
private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// AI Response Generator (You can integrate with actual AI API later)
private fun generateAIResponse(userMessage: String): ChatMessage {
    val responses = mapOf(
        "memory" to ChatMessage(
            content = "Great question! To improve memory, I recommend:\n\n1. Play our Memory Match game regularly\n2. Get 7-8 hours of quality sleep\n3. Practice mindfulness meditation\n4. Stay physically active\n5. Eat brain-healthy foods like fish, nuts, and berries\n\nWould you like to start a memory game now?",
            isUser = false,
            suggestions = listOf("Start Memory Game", "More brain tips", "Track my progress")
        ),
        "game" to ChatMessage(
            content = "I'd recommend starting with these games based on your goals:\n\n🧩 Memory Match - Improves visual memory\n🔢 Number Sequence - Enhances working memory\n🎯 Pattern Recognition - Boosts problem-solving\n\nAll games adapt to your skill level. Which one interests you?",
            isUser = false,
            suggestions = listOf("Play Memory Match", "Show all games", "See my stats")
        ),
        "focus" to ChatMessage(
            content = "Here are proven tips to enhance focus:\n\n✨ Practice the Pomodoro Technique (25 min work, 5 min break)\n🧘 Try our Focus Training games\n💧 Stay hydrated throughout the day\n📱 Minimize digital distractions\n🎵 Use ambient background music\n\nWould you like to try a focus-building exercise?",
            isUser = false,
            suggestions = listOf("Start Focus Game", "Set a timer", "Learn more")
        ),
        "progress" to ChatMessage(
            content = "I can help you track your cognitive training progress! Your dashboard shows:\n\n📊 Games played\n⭐ Points earned\n📈 Skill improvement trends\n🏆 Achievements unlocked\n\nWould you like to view your detailed progress report?",
            isUser = false,
            suggestions = listOf("View Progress", "See Achievements", "Set new goals")
        )
    )

    // Find best matching response
    val key = responses.keys.find { userMessage.contains(it, ignoreCase = true) }
    return key?.let { responses[it] } ?: ChatMessage(
        content = "I'm here to help with:\n\n🧠 Brain training tips\n🎮 Game recommendations\n📊 Progress tracking\n🎯 Cognitive health advice\n\nWhat would you like to know more about?",
        isUser = false,
        suggestions = listOf(
            "How do I improve memory?",
            "What games should I play?",
            "Tips for better focus",
            "Track my progress"
        )
    )
}