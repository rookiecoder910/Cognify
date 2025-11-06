package com.example.cognify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cognify.data.GameSession
import com.example.cognify.repository.GameRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

data class GameStatistics(
    val totalSessions: Int = 0,
    val bestScore: Int = 0,
    val averageScore: Int = 0,
    val averageAccuracy: Int = 0,
    val totalTimePlayed: Int = 0,
    val averageMoves: Int = 0
)

class GameStatsViewModel(
    private val userId: String,
    private val gameName: String
) : ViewModel() {

    private val repository = GameRepository()

    private val _sessions = MutableStateFlow<List<GameSession>>(emptyList())
    val sessions: StateFlow<List<GameSession>> = _sessions.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _gameStatistics = MutableStateFlow(GameStatistics())
    val gameStatistics: StateFlow<GameStatistics> = _gameStatistics.asStateFlow()

    init {
        fetchGameStats()
    }

    private fun fetchGameStats() {
        viewModelScope.launch {
            _loading.value = true

            repository.getGameSessions(userId, gameName)
                .catch { e ->
                    e.printStackTrace()
                    _loading.value = false
                }
                .collect { sessionList ->
                    _sessions.value = sessionList
                    updateStatistics(sessionList)
                    _loading.value = false
                }
        }
    }

    private fun updateStatistics(sessions: List<GameSession>) {
        if (sessions.isEmpty()) {
            _gameStatistics.value = GameStatistics()
            return
        }

        val totalSessions = sessions.size
        val bestScore = sessions.maxOfOrNull { it.totalScore } ?: 0
        val averageScore = sessions.map { it.totalScore }.average().toInt()

        val averageAccuracy = sessions.mapNotNull { session ->
            val total = session.correctAnswers + session.wrongAnswers
            if (total > 0) (session.correctAnswers * 100.0 / total) else null
        }.average().takeIf { !it.isNaN() }?.toInt() ?: 0

        val totalTimePlayed = sessions.sumOf { it.timeTaken }
        val averageMoves = sessions.map { it.moves }.average().toInt()

        _gameStatistics.value = GameStatistics(
            totalSessions = totalSessions,
            bestScore = bestScore,
            averageScore = averageScore,
            averageAccuracy = averageAccuracy,
            totalTimePlayed = totalTimePlayed,
            averageMoves = averageMoves
        )
    }

    fun addDummyData() {
        viewModelScope.launch {
            val dummy = GameSession(
                sessionId = "session_${System.currentTimeMillis()}",
                userId = userId,
                gameName = gameName,
                startTime = System.currentTimeMillis() - 60000,
                endTime = System.currentTimeMillis(),
                totalScore = (500..1000).random(),
                correctAnswers = (8..12).random(),
                wrongAnswers = (2..6).random(),
                avgReactionTime = Random.nextDouble(0.8, 2.0),
                difficultyLevel = (1..6).random(),
                date = "",
                moves = (20..40).random(),
                timeTaken = (30..120).random(),
                level = (1..6).random(),
                matchedPairs = (6..12).random(),
                totalPairs = 12
            )

            repository.saveGameSession(dummy)
        }
    }
}