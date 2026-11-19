package com.Fowvite0.Koqfam.presentation.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Fowvite0.Koqfam.core.config.GameConfig
import com.Fowvite0.Koqfam.domain.model.CustomTemple
import com.Fowvite0.Koqfam.domain.model.TemplePiece
import com.Fowvite0.Koqfam.domain.model.TempleValidation
import com.Fowvite0.Koqfam.domain.repository.LevelRepository
import com.Fowvite0.Koqfam.domain.usecase.ValidateTempleUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LevelEditorUiState(
    val temple: CustomTemple = CustomTemple.empty(
        0,
        GameConfig.EDITOR_COLS,
        GameConfig.EDITOR_ROWS
    ),
    val tool: TemplePiece = TemplePiece.BASALT,
    val slot: Int = 0,
    val validation: TempleValidation = TempleValidation(false, missingGate = true),
    val savedSlot: Int = -1,
    val loaded: Boolean = false
)

class LevelEditorViewModel(
    private val levels: LevelRepository,
    private val validateTemple: ValidateTempleUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LevelEditorUiState())
    val state: StateFlow<LevelEditorUiState> = _state.asStateFlow()

    private val testRuns = Channel<Int>(Channel.BUFFERED)
    val events: Flow<Int> = testRuns.receiveAsFlow()

    init {
        viewModelScope.launch {
            val draft = levels.draft()
                ?: CustomTemple.empty(0, GameConfig.EDITOR_COLS, GameConfig.EDITOR_ROWS)
            _state.update {
                it.copy(
                    temple = draft,
                    validation = validateTemple(draft),
                    loaded = true
                )
            }
        }
    }

    fun selectTool(piece: TemplePiece) {
        _state.update { it.copy(tool = piece, savedSlot = -1) }
    }

    fun selectSlot(slot: Int) {
        _state.update { it.copy(slot = slot, savedSlot = -1) }
    }

    fun tapCell(col: Int, row: Int) {
        val current = _state.value
        val existing = current.temple.at(col, row)
        val next = when {
            current.tool == TemplePiece.EMPTY -> TemplePiece.EMPTY
            existing == current.tool -> TemplePiece.EMPTY
            else -> current.tool
        }
        if (next != TemplePiece.EMPTY &&
            existing == TemplePiece.EMPTY &&
            current.temple.pieceCount >= GameConfig.EDITOR_MAX_PIECES
        ) {
            return
        }
        val updated = current.temple.with(col, row, next)
        _state.update {
            it.copy(
                temple = updated,
                validation = validateTemple(updated),
                savedSlot = -1
            )
        }
        viewModelScope.launch { levels.saveDraft(updated) }
    }

    fun clearBoard() {
        val empty = CustomTemple.empty(
            _state.value.slot,
            GameConfig.EDITOR_COLS,
            GameConfig.EDITOR_ROWS
        )
        _state.update {
            it.copy(temple = empty, validation = validateTemple(empty), savedSlot = -1)
        }
        viewModelScope.launch { levels.saveDraft(empty) }
    }

    fun saveTemple() {
        val current = _state.value
        if (!current.validation.valid) return
        val stored = current.temple.copy(slot = current.slot)
        viewModelScope.launch {
            levels.saveTemple(stored)
            levels.saveDraft(stored)
            _state.update { it.copy(savedSlot = current.slot) }
        }
    }

    fun testRun() {
        val current = _state.value
        if (!current.validation.valid) return
        val stored = current.temple.copy(slot = current.slot)
        viewModelScope.launch {
            levels.saveTemple(stored)
            levels.saveDraft(stored)
            testRuns.trySend(current.slot)
        }
    }
}
