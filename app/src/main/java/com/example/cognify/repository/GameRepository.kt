package com.example.cognify.repository

import com.example.cognify.data.GameSession
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class GameRepository {
    private val db = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())


    suspend fun saveGameSession(session: GameSession): Result<String> {
        return try {
            val sessionWithDate = session.copy(
                date = dateFormat.format(Date(session.endTime))
            )

            val docRef = db.collection("game_stats")
                .document(session.userId)
                .collection("sessions")
                .document(session.sessionId)

            docRef.set(sessionWithDate).await()
            Result.success(session.sessionId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    fun getGameSessions(userId: String, gameName: String): Flow<List<GameSession>> = callbackFlow {
        val listener = db.collection("game_stats")
            .document(userId)
            .collection("sessions")
            .whereEqualTo("gameName", gameName)
            .orderBy("endTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val sessions = snapshot?.documents?.mapNotNull {
                    it.toObject(GameSession::class.java)
                } ?: emptyList()

                trySend(sessions)
            }

        awaitClose { listener.remove() }
    }


    fun getAllSessions(userId: String): Flow<List<GameSession>> = callbackFlow {
        val listener = db.collection("game_stats")
            .document(userId)
            .collection("sessions")
            .orderBy("endTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val sessions = snapshot?.documents?.mapNotNull {
                    it.toObject(GameSession::class.java)
                } ?: emptyList()

                trySend(sessions)
            }

        awaitClose { listener.remove() }
    }
}