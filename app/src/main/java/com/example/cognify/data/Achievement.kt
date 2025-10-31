package com.example.cognify.data

import com.example.cognify.screens.Rarity

data class Achievement(
    val id: Int,
    val title: String,
    val description: String,
    val emoji: String,
    val isUnlocked: Boolean,
    val progress: Int,
    val maxProgress: Int,
    val rarity: Rarity
)
