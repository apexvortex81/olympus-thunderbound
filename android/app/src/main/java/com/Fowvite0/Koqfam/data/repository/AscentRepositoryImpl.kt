package com.Fowvite0.Koqfam.data.repository

import com.Fowvite0.Koqfam.data.local.AscentPreferences
import com.Fowvite0.Koqfam.domain.model.AscentProgress
import com.Fowvite0.Koqfam.domain.repository.AscentRepository

class AscentRepositoryImpl(private val store: AscentPreferences) : AscentRepository {

    override fun progress(): AscentProgress = AscentProgress(
        bestHeightMeters = store.readInt(AscentPreferences.KEY_BEST_HEIGHT),
        relicsTotal = store.readInt(AscentPreferences.KEY_RELICS_TOTAL),
        floorsCleared = store.readInt(AscentPreferences.KEY_FLOORS_CLEARED),
        telegraphAssist = store.readBoolean(AscentPreferences.KEY_ASSIST_TELEGRAPH),
        swapDashSides = store.readBoolean(AscentPreferences.KEY_ASSIST_SWAP)
    )

    override fun recordRun(floorIndex: Int, heightMeters: Int, relics: Int, cleared: Boolean) {
        if (heightMeters > store.readInt(AscentPreferences.KEY_BEST_HEIGHT)) {
            store.writeInt(AscentPreferences.KEY_BEST_HEIGHT, heightMeters)
        }
        if (relics > 0) {
            store.writeInt(
                AscentPreferences.KEY_RELICS_TOTAL,
                store.readInt(AscentPreferences.KEY_RELICS_TOTAL) + relics
            )
        }
        if (floorIndex > 0) {
            val bestKey = AscentPreferences.PREFIX_FLOOR_BEST + floorIndex
            if (heightMeters > store.readInt(bestKey)) store.writeInt(bestKey, heightMeters)
            val relicKey = AscentPreferences.PREFIX_FLOOR_RELICS + floorIndex
            if (relics > store.readInt(relicKey)) store.writeInt(relicKey, relics)
            if (cleared && floorIndex > store.readInt(AscentPreferences.KEY_FLOORS_CLEARED)) {
                store.writeInt(AscentPreferences.KEY_FLOORS_CLEARED, floorIndex)
            }
        }
    }

    override fun floorBest(floorIndex: Int): Int =
        store.readInt(AscentPreferences.PREFIX_FLOOR_BEST + floorIndex)

    override fun floorRelics(floorIndex: Int): Int =
        store.readInt(AscentPreferences.PREFIX_FLOOR_RELICS + floorIndex)

    override fun setTelegraphAssist(enabled: Boolean) {
        store.writeBoolean(AscentPreferences.KEY_ASSIST_TELEGRAPH, enabled)
    }

    override fun setSwapDashSides(enabled: Boolean) {
        store.writeBoolean(AscentPreferences.KEY_ASSIST_SWAP, enabled)
    }
}
