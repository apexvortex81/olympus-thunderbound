package com.Fowvite0.Koqfam.domain.repository

import com.Fowvite0.Koqfam.domain.model.AscentProgress

interface AscentRepository {
    fun progress(): AscentProgress
    fun recordRun(floorIndex: Int, heightMeters: Int, relics: Int, cleared: Boolean)
    fun floorBest(floorIndex: Int): Int
    fun floorRelics(floorIndex: Int): Int
    fun setTelegraphAssist(enabled: Boolean)
    fun setSwapDashSides(enabled: Boolean)
}
