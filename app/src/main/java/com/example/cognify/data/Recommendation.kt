package com.example.cognify.data

data class Recommendation(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val difficulty: String,
    val estimatedTime: Int,
    val benefits: List<String>
)
