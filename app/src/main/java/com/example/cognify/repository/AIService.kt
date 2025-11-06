package com.example.cognify.data

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.cognify.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AIService {
    private val openAI = OpenAI(token = BuildConfig.OPENAI_API_KEY)

    // Conversation history for context
    private val conversationHistory = mutableListOf<ChatMessage>()

    init {
        // System prompt to define AI behavior
        conversationHistory.add(
            ChatMessage(
                role = ChatRole.System,
                content = """
                    You are a helpful AI assistant for Cognify, a cognitive training app for Alzheimer's patients.
                    Your role is to:
                    - Provide encouragement and support
                    - Answer questions about brain health and memory
                    - Suggest appropriate games based on user needs
                    - Give tips for improving cognitive function
                    - Be patient, empathetic, and use simple language
                    - Keep responses concise (2-3 sentences max)
                """.trimIndent()
            )
        )
    }

    suspend fun getChatResponse(userMessage: String): Result<String> {
        return try {
            // Add user message to history
            conversationHistory.add(
                ChatMessage(
                    role = ChatRole.User,
                    content = userMessage
                )
            )

            val chatCompletionRequest = ChatCompletionRequest(
                model = ModelId("gpt-3.5-turbo"),
                messages = conversationHistory,
                maxTokens = 150,
                temperature = 0.7
            )

            val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
            val aiResponse = completion.choices.firstOrNull()?.message?.content
                ?: "I'm here to help! Could you please rephrase that?"

            // Add AI response to history
            conversationHistory.add(
                ChatMessage(
                    role = ChatRole.Assistant,
                    content = aiResponse
                )
            )

            // Keep only last 10 messages to avoid token limits
            if (conversationHistory.size > 11) { // 1 system + 10 messages
                conversationHistory.subList(1, conversationHistory.size - 10).clear()
            }

            Result.success(aiResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Streaming response (optional, for typing effect)
    fun getChatResponseStream(userMessage: String): Flow<String> = flow {
        try {
            conversationHistory.add(
                ChatMessage(role = ChatRole.User, content = userMessage)
            )

            val request = ChatCompletionRequest(
                model = ModelId("gpt-3.5-turbo"),
                messages = conversationHistory,
                maxTokens = 150
            )

            openAI.chatCompletions(request).collect { chunk ->
                val delta = chunk.choices.firstOrNull()?.delta?.content
                if (delta != null) {
                    emit(delta)
                }
            }
        } catch (e: Exception) {
            emit("Error: ${e.message}")
        }
    }

    fun clearHistory() {
        conversationHistory.clear()
        conversationHistory.add(
            ChatMessage(
                role = ChatRole.System,
                content = "You are a helpful AI assistant for Cognify..."
            )
        )
    }
}