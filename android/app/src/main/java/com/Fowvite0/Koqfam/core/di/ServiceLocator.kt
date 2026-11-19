package com.Fowvite0.Koqfam.core.di

import android.content.Context
import com.Fowvite0.Koqfam.data.local.AscentPreferences
import com.Fowvite0.Koqfam.data.repository.AscentRepositoryImpl
import com.Fowvite0.Koqfam.data.repository.LevelRepositoryImpl
import com.Fowvite0.Koqfam.domain.repository.AscentRepository
import com.Fowvite0.Koqfam.domain.repository.LevelRepository

object ServiceLocator {

    private var store: AscentPreferences? = null
    private var ascent: AscentRepository? = null
    private var levels: LevelRepository? = null

    fun init(context: Context) {
        if (store != null) return
        val preferences = AscentPreferences(context.applicationContext)
        store = preferences
        ascent = AscentRepositoryImpl(preferences)
        levels = LevelRepositoryImpl(preferences)
    }

    fun factory(args: RunArgs = RunArgs()): AppViewModelFactory {
        val ascentRepo = requireNotNull(ascent)
        val levelRepo = requireNotNull(levels)
        return AppViewModelFactory(ascentRepo, levelRepo, args)
    }
}
