package com.example.cognify.data

sealed class ReactionState {
    data object Initial : ReactionState()
    data object Ready : ReactionState()
    data class Go(val startTime: Long) : ReactionState()
    data class Result(val timeMs: Long) : ReactionState()
    data object TooSoon : ReactionState()
}

