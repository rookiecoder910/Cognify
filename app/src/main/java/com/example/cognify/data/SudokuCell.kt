package com.example.cognify.data

data class SudokuCell(
    val value: Int = 0,
    val isFixed: Boolean = false,
    val isError: Boolean = false,
    val isSelected: Boolean = false
)

