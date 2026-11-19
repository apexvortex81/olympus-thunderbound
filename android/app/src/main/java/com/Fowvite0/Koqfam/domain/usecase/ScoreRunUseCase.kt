package com.Fowvite0.Koqfam.domain.usecase

import com.Fowvite0.Koqfam.core.config.GameConfig

class ScoreRunUseCase {

    operator fun invoke(heightMeters: Int, relics: Int, cleared: Boolean): Int {
        val clearBonus = if (cleared) GameConfig.FLOOR_CLEAR_BONUS else 0
        return heightMeters * 10 + relics * GameConfig.RELIC_SCORE + clearBonus
    }
}
