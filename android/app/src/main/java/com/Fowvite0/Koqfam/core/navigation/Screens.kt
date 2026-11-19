package com.Fowvite0.Koqfam.core.navigation

import com.Fowvite0.Koqfam.domain.model.Outcome

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object Levels : Screen
    data object Editor : Screen
    data object HowTo : Screen

    data class Game(
        val floorIndex: Int,
        val customSlot: Int = -1,
        val attempt: Int = 0
    ) : Screen

    data class GameOver(
        val outcome: Outcome,
        val heightMeters: Int,
        val relics: Int,
        val floorIndex: Int,
        val customSlot: Int,
        val attempt: Int
    ) : Screen
}
