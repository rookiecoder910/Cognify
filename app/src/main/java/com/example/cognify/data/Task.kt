package com.example.cognify.data

data class Task(
    val id: String = "",
    val type: String = "", // booster, challenge, recommendation
    val gameType: String = "",
    val title: String = "",
    val description: String = "",
    val status: String = "pending",
    val priority: String = "medium",
    val dueDate: String = "",
    val aiGenerated: Boolean = false,
    val reason: String = ""
)