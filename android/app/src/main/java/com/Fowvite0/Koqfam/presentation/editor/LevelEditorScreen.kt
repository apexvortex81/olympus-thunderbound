package com.Fowvite0.Koqfam.presentation.editor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Fowvite0.Koqfam.R
import com.Fowvite0.Koqfam.core.config.GameConfig
import com.Fowvite0.Koqfam.core.di.ServiceLocator
import com.Fowvite0.Koqfam.core.theme.BasaltStone
import com.Fowvite0.Koqfam.core.theme.BoltIndigo
import com.Fowvite0.Koqfam.core.theme.BoltWhite
import com.Fowvite0.Koqfam.core.theme.CeremonialGold
import com.Fowvite0.Koqfam.core.theme.DangerCrimson
import com.Fowvite0.Koqfam.core.theme.GoldHairline
import com.Fowvite0.Koqfam.core.theme.IndigoHairline
import com.Fowvite0.Koqfam.core.theme.RaisedGlass
import com.Fowvite0.Koqfam.core.theme.SurfaceBasalt
import com.Fowvite0.Koqfam.core.theme.TextSecondary
import com.Fowvite0.Koqfam.core.theme.TextTertiary
import com.Fowvite0.Koqfam.core.ui.SecondaryButton
import com.Fowvite0.Koqfam.core.ui.StormBackdrop
import com.Fowvite0.Koqfam.domain.model.TemplePiece

private val EditorOverlay = listOf(
    Color(0xE6050510),
    Color(0xF20A1628),
    Color(0xF2050510)
)

private val Tools = listOf(
    TemplePiece.BASALT to R.string.tool_basalt,
    TemplePiece.DRIFT to R.string.tool_drift,
    TemplePiece.BOLT to R.string.tool_bolt,
    TemplePiece.WIND to R.string.tool_wind,
    TemplePiece.RELIC to R.string.tool_relic,
    TemplePiece.GATE to R.string.tool_gate,
    TemplePiece.EMPTY to R.string.tool_erase
)

@Composable
fun LevelEditorRoute(
    onTestRun: (Int) -> Unit,
    onBack: () -> Unit
) {
    val editorViewModel: LevelEditorViewModel =
        viewModel(key = "editor", factory = ServiceLocator.factory())
    val state by editorViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(editorViewModel) {
        editorViewModel.events.collect { slot -> onTestRun(slot) }
    }

    LevelEditorScreen(
        state = state,
        onTool = editorViewModel::selectTool,
        onSlot = editorViewModel::selectSlot,
        onCell = editorViewModel::tapCell,
        onClear = editorViewModel::clearBoard,
        onSave = editorViewModel::saveTemple,
        onTest = editorViewModel::testRun,
        onBack = onBack
    )
}

@Composable
fun LevelEditorScreen(
    state: LevelEditorUiState,
    onTool: (TemplePiece) -> Unit,
    onSlot: (Int) -> Unit,
    onCell: (Int, Int) -> Unit,
    onClear: () -> Unit,
    onSave: () -> Unit,
    onTest: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val slab = ImageBitmap.imageResource(R.drawable.sprite_2)
    val orb = ImageBitmap.imageResource(R.drawable.sprite_3)
    val relic = ImageBitmap.imageResource(R.drawable.sprite_4)
    val selectedLabel = stringResource(R.string.sd_tool_selected)

    Box(modifier = modifier.fillMaxSize()) {
        StormBackdrop(overlay = EditorOverlay, artScale = 1.12f)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.editor_title),
                style = MaterialTheme.typography.headlineSmall,
                color = BoltWhite
            )
            Text(
                text = stringResource(R.string.editor_hint),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Tools.forEach { entry ->
                    ToolChip(
                        labelRes = entry.second,
                        selected = state.tool == entry.first,
                        selectedLabel = selectedLabel,
                        onClick = { onTool(entry.first) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val frame = 10.dp
                val cellByWidth = (maxWidth - frame * 2) / GameConfig.EDITOR_COLS
                val cellByHeight = (maxHeight - frame * 2) / GameConfig.EDITOR_ROWS
                val cell = if (cellByWidth < cellByHeight) cellByWidth else cellByHeight
                val boardWidth = cell * GameConfig.EDITOR_COLS + frame * 2
                val boardHeight = cell * GameConfig.EDITOR_ROWS + frame * 2

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceBasalt,
                    border = BorderStroke(2.dp, IndigoHairline),
                    modifier = Modifier.size(boardWidth, boardHeight)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .pointerInput(state.tool, state.temple) {
                                detectTapGestures { offset ->
                                    val cellPx = size.width.toFloat() / GameConfig.EDITOR_COLS
                                    val col = (offset.x / cellPx).toInt()
                                    val row = (offset.y / cellPx).toInt()
                                    if (col in 0 until GameConfig.EDITOR_COLS &&
                                        row in 0 until GameConfig.EDITOR_ROWS
                                    ) {
                                        onCell(col, row)
                                    }
                                }
                            }
                    ) {
                        val cellPx = size.width / GameConfig.EDITOR_COLS
                        for (row in 0 until GameConfig.EDITOR_ROWS) {
                            for (col in 0 until GameConfig.EDITOR_COLS) {
                                val left = col * cellPx
                                val top = row * cellPx
                                drawRect(
                                    color = Color(0x1AEAF2FF),
                                    topLeft = Offset(left + 1f, top + 1f),
                                    size = Size(cellPx - 2f, cellPx - 2f),
                                    style = Stroke(width = 1f)
                                )
                                when (state.temple.at(col, row)) {
                                    TemplePiece.BASALT -> drawCellSprite(
                                        slab,
                                        left,
                                        top,
                                        cellPx
                                    )

                                    TemplePiece.DRIFT -> {
                                        drawCellSprite(slab, left, top, cellPx)
                                        drawRect(
                                            color = CeremonialGold,
                                            topLeft = Offset(left + 3f, top + cellPx * 0.78f),
                                            size = Size(cellPx - 6f, 3f)
                                        )
                                    }

                                    TemplePiece.BOLT -> drawCellSprite(orb, left, top, cellPx)
                                    TemplePiece.RELIC -> drawCellSprite(relic, left, top, cellPx)
                                    TemplePiece.WIND -> drawRect(
                                        color = Color(0x664169E1),
                                        topLeft = Offset(left + 2f, top + 2f),
                                        size = Size(cellPx - 4f, cellPx - 4f)
                                    )

                                    TemplePiece.GATE -> drawRect(
                                        color = Color(0x99E8C06A),
                                        topLeft = Offset(left + 2f, top + 2f),
                                        size = Size(cellPx - 4f, cellPx - 4f)
                                    )

                                    TemplePiece.EMPTY -> Unit
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(
                    R.string.piece_count,
                    state.temple.pieceCount,
                    GameConfig.EDITOR_MAX_PIECES
                ),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            ValidationLine(state = state)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(GameConfig.CUSTOM_SLOTS) { slot ->
                    SlotChip(
                        slot = slot,
                        selected = state.slot == slot,
                        selectedLabel = selectedLabel,
                        onClick = { onSlot(slot) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SecondaryButton(
                    textRes = R.string.test_run,
                    onClick = onTest,
                    enabled = state.validation.valid,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    textRes = R.string.save_temple,
                    onClick = onSave,
                    enabled = state.validation.valid,
                    gold = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SecondaryButton(
                    textRes = R.string.clear_board,
                    onClick = onClear,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    textRes = R.string.back,
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ValidationLine(state: LevelEditorUiState) {
    val validation = state.validation
    when {
        state.savedSlot >= 0 -> Text(
            text = stringResource(R.string.editor_saved, state.savedSlot + 1),
            style = MaterialTheme.typography.labelSmall,
            color = CeremonialGold,
            textAlign = TextAlign.Center
        )

        validation.valid -> Text(
            text = stringResource(R.string.editor_valid),
            style = MaterialTheme.typography.labelSmall,
            color = BoltIndigo,
            textAlign = TextAlign.Center
        )

        validation.missingGate -> Text(
            text = stringResource(R.string.editor_no_gate),
            style = MaterialTheme.typography.labelSmall,
            color = DangerCrimson,
            textAlign = TextAlign.Center
        )

        validation.missingLedge -> Text(
            text = stringResource(R.string.editor_no_ledge),
            style = MaterialTheme.typography.labelSmall,
            color = DangerCrimson,
            textAlign = TextAlign.Center
        )

        else -> Text(
            text = stringResource(R.string.editor_break, validation.breakRow),
            style = MaterialTheme.typography.labelSmall,
            color = DangerCrimson,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ToolChip(
    labelRes: Int,
    selected: Boolean,
    selectedLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Color(0x33E8C06A) else RaisedGlass,
        border = BorderStroke(1.dp, if (selected) GoldHairline else IndigoHairline),
        modifier = modifier
            .height(52.dp)
            .semantics { if (selected) stateDescription = selectedLabel }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick)
        ) {
            Text(
                text = stringResource(labelRes),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    letterSpacing = 0.sp
                ),
                color = if (selected) CeremonialGold else TextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SlotChip(
    slot: Int,
    selected: Boolean,
    selectedLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Color(0x334169E1) else RaisedGlass,
        border = BorderStroke(1.dp, if (selected) IndigoHairline else BasaltStone),
        modifier = modifier
            .height(48.dp)
            .semantics { if (selected) stateDescription = selectedLabel }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick)
        ) {
            Text(
                text = stringResource(R.string.editor_slots, slot + 1),
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) BoltWhite else TextTertiary,
                maxLines = 1
            )
        }
    }
}

private fun DrawScope.drawCellSprite(
    image: ImageBitmap,
    left: Float,
    top: Float,
    cell: Float
) {
    val ratio = image.width.toFloat() / image.height.toFloat()
    val target = cell * 0.86f
    val drawHeight = if (ratio >= 1f) target / ratio else target
    val drawWidth = drawHeight * ratio
    val offsetX = left + (cell - drawWidth) / 2f
    val offsetY = top + (cell - drawHeight) / 2f
    drawImage(
        image = image,
        dstOffset = IntOffset(offsetX.toInt(), offsetY.toInt()),
        dstSize = IntSize(drawWidth.toInt().coerceAtLeast(1), drawHeight.toInt().coerceAtLeast(1))
    )
}
