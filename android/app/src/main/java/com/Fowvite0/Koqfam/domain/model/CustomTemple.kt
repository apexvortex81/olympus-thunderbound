package com.Fowvite0.Koqfam.domain.model

enum class TemplePiece(val token: Char) {
    EMPTY('.'),
    BASALT('B'),
    DRIFT('D'),
    BOLT('Z'),
    WIND('W'),
    RELIC('R'),
    GATE('G');

    companion object {
        fun fromToken(token: Char): TemplePiece =
            entries.firstOrNull { it.token == token } ?: EMPTY
    }
}

data class CustomTemple(
    val slot: Int,
    val cols: Int,
    val rows: Int,
    val cells: List<TemplePiece>
) {
    val pieceCount: Int get() = cells.count { it != TemplePiece.EMPTY }

    fun at(col: Int, row: Int): TemplePiece {
        val index = row * cols + col
        return if (index in cells.indices) cells[index] else TemplePiece.EMPTY
    }

    fun with(col: Int, row: Int, piece: TemplePiece): CustomTemple {
        val index = row * cols + col
        if (index !in cells.indices) return this
        val next = cells.toMutableList()
        next[index] = piece
        return copy(cells = next)
    }

    companion object {
        fun empty(slot: Int, cols: Int, rows: Int): CustomTemple =
            CustomTemple(slot, cols, rows, List(cols * rows) { TemplePiece.EMPTY })
    }
}
