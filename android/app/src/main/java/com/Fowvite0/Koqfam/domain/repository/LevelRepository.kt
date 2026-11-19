package com.Fowvite0.Koqfam.domain.repository

import com.Fowvite0.Koqfam.domain.model.CustomTemple
import com.Fowvite0.Koqfam.domain.model.FloorTuning

interface LevelRepository {
    fun tuning(floorIndex: Int): FloorTuning
    fun temples(): List<CustomTemple>
    fun temple(slot: Int): CustomTemple?
    fun saveTemple(temple: CustomTemple)
    fun draft(): CustomTemple?
    fun saveDraft(temple: CustomTemple)
}
