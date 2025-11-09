package com.example.cognify.data
//game levels
data class GameLevel(val rows: Int, val cols: Int, val theme: List<String>) {
    val size: Int = rows * cols
}

