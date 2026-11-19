package com.Fowvite0.Koqfam.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.Fowvite0.Koqfam.domain.model.Outcome
import com.Fowvite0.Koqfam.domain.repository.AscentRepository
import com.Fowvite0.Koqfam.domain.repository.LevelRepository
import com.Fowvite0.Koqfam.domain.usecase.GenerateFloorUseCase
import com.Fowvite0.Koqfam.domain.usecase.ScoreRunUseCase
import com.Fowvite0.Koqfam.domain.usecase.StepClimbUseCase
import com.Fowvite0.Koqfam.domain.usecase.ValidateTempleUseCase
import com.Fowvite0.Koqfam.presentation.app.AppViewModel
import com.Fowvite0.Koqfam.presentation.editor.LevelEditorViewModel
import com.Fowvite0.Koqfam.presentation.game.GameViewModel
import com.Fowvite0.Koqfam.presentation.gameover.GameOverViewModel
import com.Fowvite0.Koqfam.presentation.howto.HowToPlayViewModel
import com.Fowvite0.Koqfam.presentation.levels.LevelSelectViewModel
import com.Fowvite0.Koqfam.presentation.menu.MenuViewModel
import com.Fowvite0.Koqfam.presentation.splash.SplashViewModel

data class RunArgs(
    val floorIndex: Int = 1,
    val customSlot: Int = -1,
    val attempt: Int = 0,
    val outcome: Outcome = Outcome.STORM,
    val heightMeters: Int = 0,
    val relics: Int = 0
)

class AppViewModelFactory(
    private val ascent: AscentRepository,
    private val levels: LevelRepository,
    private val args: RunArgs
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel()
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(ascent, levels)
            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(
                ascent = ascent,
                levels = levels,
                generateFloor = GenerateFloorUseCase(),
                stepClimb = StepClimbUseCase(),
                floorIndex = args.floorIndex,
                customSlot = args.customSlot,
                attempt = args.attempt
            )
            modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(
                ascent = ascent,
                scoreRun = ScoreRunUseCase(),
                floorIndex = args.floorIndex,
                customSlot = args.customSlot,
                outcome = args.outcome,
                heightMeters = args.heightMeters,
                relics = args.relics
            )
            modelClass.isAssignableFrom(LevelSelectViewModel::class.java) ->
                LevelSelectViewModel(ascent, levels)
            modelClass.isAssignableFrom(LevelEditorViewModel::class.java) ->
                LevelEditorViewModel(levels, ValidateTempleUseCase())
            modelClass.isAssignableFrom(HowToPlayViewModel::class.java) ->
                HowToPlayViewModel(ascent)
            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }
}
