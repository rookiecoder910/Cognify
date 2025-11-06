package com.example.cognify.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cognify.data.AIService
import com.example.cognify.screens.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val aiService = AIService()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        // Add welcome message
        _messages.value = listOf(
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

    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        // Add user message
        _messages.value = _messages.value + ChatMessage(
            content = userMessage,
            isUser = true
        )

        // Show typing indicator
        _isTyping.value = true

        // Get AI response
        viewModelScope.launch {
            val result = aiService.getChatResponse(userMessage)

            _isTyping.value = false

            result.onSuccess { aiResponse ->
                _messages.value = _messages.value + ChatMessage(
                    content = aiResponse,
                    isUser = false,
                    suggestions = generateSuggestions(aiResponse)
                )
                _error.value = null
            }.onFailure { exception ->
                _error.value = exception.message
                _messages.value = _messages.value + ChatMessage(
                    content = "I apologize, but I'm having trouble connecting right now. Please try again in a moment.",
                    isUser = false
                )
            }
        }
    }

    private fun generateSuggestions(response: String): List<String> {
        // Generate contextual suggestions based on response
        return when {
            response.contains("memory", ignoreCase = true) -> listOf(
                "Start Memory Game",
                "More memory tips",
                "Track my progress"
            )
            response.contains("game", ignoreCase = true) -> listOf(
                "Show all games",
                "What's my level?",
                "See my stats"
            )
            else -> listOf(
                "What else can you help with?",
                "Show my progress",
                "Suggest a game"
            )
        }
    }

    fun clearChat() {
        aiService.clearHistory()
        _messages.value = emptyList()
        _error.value = null
    }
}