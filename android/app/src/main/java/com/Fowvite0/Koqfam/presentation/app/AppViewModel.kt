package com.Fowvite0.Koqfam.presentation.app

import androidx.lifecycle.ViewModel
import com.Fowvite0.Koqfam.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val backStack = mutableListOf<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canPop = MutableStateFlow(false)
    val canPop: StateFlow<Boolean> = _canPop.asStateFlow()

    fun navigate(target: Screen) {
        backStack.add(_screen.value)
        _screen.value = target
        syncStack()
    }

    fun replace(target: Screen) {
        _screen.value = target
        syncStack()
    }

    fun back() {
        val previous = backStack.removeLastOrNull() ?: return
        _screen.value = previous
        syncStack()
    }

    fun toMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
        syncStack()
    }

    private fun syncStack() {
        _canPop.value = backStack.isNotEmpty()
    }
}
