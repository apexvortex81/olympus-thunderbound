package com.Fowvite0.Koqfam.data.repository

import com.Fowvite0.Koqfam.core.config.GameConfig
import com.Fowvite0.Koqfam.data.local.AscentPreferences
import com.Fowvite0.Koqfam.data.sample.SampleData
import com.Fowvite0.Koqfam.domain.model.CustomTemple
import com.Fowvite0.Koqfam.domain.model.FloorTuning
import com.Fowvite0.Koqfam.domain.model.TemplePiece
import com.Fowvite0.Koqfam.domain.repository.LevelRepository

class LevelRepositoryImpl(private val store: AscentPreferences) : LevelRepository {

    override fun tuning(floorIndex: Int): FloorTuning = SampleData.tuning(floorIndex)

    override fun temples(): List<CustomTemple> =
        (0 until GameConfig.CUSTOM_SLOTS).mapNotNull { temple(it) }

    override fun temple(slot: Int): CustomTemple? {
        val raw = store.readString(AscentPreferences.PREFIX_CUSTOM + slot) ?: return null
        return decode(slot, raw)
    }

    override fun saveTemple(temple: CustomTemple) {
        store.writeString(AscentPreferences.PREFIX_CUSTOM + temple.slot, encode(temple))
    }

    override fun draft(): CustomTemple? {
        val raw = store.readString(AscentPreferences.KEY_EDITOR_DRAFT)
            ?: return SampleData.firstTemple
        return decode(0, raw)
    }

    override fun saveDraft(temple: CustomTemple) {
        store.writeString(AscentPreferences.KEY_EDITOR_DRAFT, encode(temple))
    }

    private fun encode(temple: CustomTemple): String {
        val tokens = temple.cells.map { it.token }.joinToString(separator = "")
        return "${temple.cols}:${temple.rows}:$tokens"
    }

    private fun decode(slot: Int, raw: String): CustomTemple? {
        val parts = raw.split(":")
        if (parts.size != 3) return null
        val cols = parts[0].toIntOrNull() ?: return null
        val rows = parts[1].toIntOrNull() ?: return null
        val tokens = parts[2]
        if (tokens.length != cols * rows) return null
        return CustomTemple(slot, cols, rows, tokens.map { TemplePiece.fromToken(it) })
    }
}
