package com.example.cognify.data.firebase

import com.example.cognify.data.GameSession

import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

object FirebaseGameAnalytics {

    private val db = FirebaseFirestore.getInstance()

    fun logGameSession(
        userId: String,
        gameName: String,
        score: Int,
        correct: Int,
        wrong: Int,
        reaction: Double,
        difficulty: Int,
        onComplete: (Boolean) -> Unit
    ) {
        val sessionId = UUID.randomUUID().toString()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = formatter.format(Date())

        val session = GameSession(
            sessionId = sessionId,
            userId = userId,
            gameName = gameName,
            startTime = System.currentTimeMillis() - 60000,
            endTime = System.currentTimeMillis(),
            totalScore = score,
            correctAnswers = correct,
            wrongAnswers = wrong,
            avgReactionTime = reaction,
            difficultyLevel = difficulty,
            date = date
        )

        db.collection("game_analysis")
            .document(userId)
            .collection(gameName)
            .document(sessionId)
            .set(session)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}
