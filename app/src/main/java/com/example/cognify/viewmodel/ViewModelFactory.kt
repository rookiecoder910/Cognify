package com.example.cognify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameStatsViewModelFactory(
    private val userId: String,
    private val gameName: String = "Memory Match"
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameStatsViewModel::class.java)) {
            return GameStatsViewModel(userId, gameName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}