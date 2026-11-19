package com.Fowvite0.Koqfam.presentation.menu

data class MenuUiState(
    val bestHeightMeters: Int = 0,
    val relicsTotal: Int = 0,
    val templesCleared: Int = 0,
    val currentFloor: Int = 1,
    val currentTemple: Int = 1,
    val savedTemples: Int = 0,
    val loaded: Boolean = false
)
