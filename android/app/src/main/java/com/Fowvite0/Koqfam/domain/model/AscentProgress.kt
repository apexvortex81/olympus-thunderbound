package com.Fowvite0.Koqfam.domain.model

data class AscentProgress(
    val bestHeightMeters: Int = 0,
    val relicsTotal: Int = 0,
    val floorsCleared: Int = 0,
    val telegraphAssist: Boolean = false,
    val swapDashSides: Boolean = false
) {
    val highestUnlockedFloor: Int get() = minOf(floorsCleared + 1, 9)
    val templesCleared: Int get() = floorsCleared / 3
}

data class FloorTile(
    val index: Int,
    val unlocked: Boolean,
    val cleared: Boolean,
    val boss: Boolean,
    val bestHeightMeters: Int,
    val relicsFound: Int
)
