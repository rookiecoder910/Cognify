package com.example.cognify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cognify.data.GameSession
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameStatsViewModel(
    private val userId: String
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _sessions = MutableStateFlow<List<GameSession>>(emptyList())
    val sessions: StateFlow<List<GameSession>> = _sessions

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    init {
        fetchGameStats()
    }

    /** 🔹 Add one dummy game session (for testing Firestore integration) */
    fun addDummyData(gameName: String = "Memory Match") {
        val dummy = GameSession(
            sessionId = "session_${System.currentTimeMillis()}",
            userId = userId,
            gameName = gameName,
            startTime = System.currentTimeMillis() - 60000,
            endTime = System.currentTimeMillis(),
            totalScore = (70..100).random(),
            correctAnswers = (5..10).random(),
            wrongAnswers = (0..3).random(),
            avgReactionTime = Random.nextDouble(0.8, 1.8),
            difficultyLevel = (1..3).random(),
            date = "2025-11-06"
        )

        db.collection("game_stats")
            .document(userId)
            .collection("stats")
            .add(dummy)
            .addOnSuccessListener {
                fetchGameStats() // refresh after adding
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    /** 🔹 Fetch all sessions from Firestore for the user */
    private fun fetchGameStats() {
        viewModelScope.launch {
            db.collection("game_stats")
                .document(userId)
                .collection("stats")
                .get()
                .addOnSuccessListener { result ->
                    val list = result.documents.mapNotNull { it.toObject(GameSession::class.java) }
                    _sessions.value = list.sortedByDescending { it.endTime }
                    _loading.value = false
                }
                .addOnFailureListener {
                    _loading.value = false
                }
        }
    }
}
