package com.example.cognify.data



data class GameSession(
    val sessionId: String = "",
    val userId: String = "",
    val gameName: String = "",

    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val totalScore: Int = 0,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val avgReactionTime: Double = 0.0,
    val difficultyLevel: Int = 1,
    val date: String = "",
    // Memory Game specific fields
    val moves: Int = 0,
    val timeTaken: Int = 0,
    val level: Int = 0,
    val matchedPairs: Int = 0,
    val totalPairs: Int = 0
)
