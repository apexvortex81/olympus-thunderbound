package com.Fowvite0.Koqfam.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Fowvite0.Koqfam.core.di.ServiceLocator
import com.Fowvite0.Koqfam.core.navigation.Screen
import com.Fowvite0.Koqfam.core.theme.BackgroundDeep
import com.Fowvite0.Koqfam.presentation.editor.LevelEditorRoute
import com.Fowvite0.Koqfam.presentation.game.GameRoute
import com.Fowvite0.Koqfam.presentation.gameover.GameOverRoute
import com.Fowvite0.Koqfam.presentation.howto.HowToPlayRoute
import com.Fowvite0.Koqfam.presentation.levels.LevelSelectRoute
import com.Fowvite0.Koqfam.presentation.menu.MenuRoute
import com.Fowvite0.Koqfam.presentation.splash.SplashRoute

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = ServiceLocator.factory())
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canPop by appViewModel.canPop.collectAsStateWithLifecycle()

    BackHandler(enabled = canPop) { appViewModel.back() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep)
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                if (targetState is Screen.Game) {
                    (slideInVertically(tween(360)) { it / 6 } + fadeIn(tween(360)))
                        .togetherWith(fadeOut(tween(240)))
                } else {
                    fadeIn(tween(420)).togetherWith(fadeOut(tween(300)))
                }
            },
            label = "screen-host"
        ) { current ->
            when (current) {
                Screen.Splash -> SplashRoute(
                    onFinished = { appViewModel.replace(Screen.Menu) }
                )

                Screen.Menu -> MenuRoute(
                    onPlay = { floor -> appViewModel.navigate(Screen.Game(floor)) },
                    onLevels = { appViewModel.navigate(Screen.Levels) },
                    onEditor = { appViewModel.navigate(Screen.Editor) },
                    onHowTo = { appViewModel.navigate(Screen.HowTo) }
                )

                Screen.Levels -> LevelSelectRoute(
                    onFloor = { floor -> appViewModel.navigate(Screen.Game(floor)) },
                    onTemple = { slot -> appViewModel.navigate(Screen.Game(0, slot)) },
                    onEditor = { appViewModel.replace(Screen.Editor) },
                    onBack = { appViewModel.back() }
                )

                Screen.Editor -> LevelEditorRoute(
                    onTestRun = { slot -> appViewModel.navigate(Screen.Game(0, slot)) },
                    onBack = { appViewModel.back() }
                )

                Screen.HowTo -> HowToPlayRoute(
                    onBack = { appViewModel.back() }
                )

                is Screen.Game -> GameRoute(
                    floorIndex = current.floorIndex,
                    customSlot = current.customSlot,
                    attempt = current.attempt,
                    onFinished = { outcome, height, relics ->
                        appViewModel.replace(
                            Screen.GameOver(
                                outcome = outcome,
                                heightMeters = height,
                                relics = relics,
                                floorIndex = current.floorIndex,
                                customSlot = current.customSlot,
                                attempt = current.attempt
                            )
                        )
                    },
                    onMenu = { appViewModel.toMenu() }
                )

                is Screen.GameOver -> GameOverRoute(
                    outcome = current.outcome,
                    heightMeters = current.heightMeters,
                    relics = current.relics,
                    floorIndex = current.floorIndex,
                    customSlot = current.customSlot,
                    onAgain = {
                        appViewModel.replace(
                            Screen.Game(
                                floorIndex = current.floorIndex,
                                customSlot = current.customSlot,
                                attempt = current.attempt + 1
                            )
                        )
                    },
                    onNextFloor = { next ->
                        appViewModel.replace(Screen.Game(floorIndex = next))
                    },
                    onMenu = { appViewModel.toMenu() }
                )
            }
        }
    }
}
