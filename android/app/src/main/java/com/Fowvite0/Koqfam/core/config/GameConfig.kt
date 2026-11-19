package com.Fowvite0.Koqfam.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L

    const val LANES = 7
    const val TICK_MS = 16L
    const val TICK_SECONDS = 0.016f

    const val GRAVITY = 42f
    const val MAX_FALL = 26f
    const val DASH_VY = 15f
    const val DASH_VX = 9.2f
    const val DASH_TRAIL_SECONDS = 0.22f
    const val AIR_DRAG = 0.93f

    const val MAX_CHARGES = 3
    const val CHARGE_REFILL_SECONDS = 1.1f
    const val MAX_BUBBLES = 2
    const val SHIELD_SECONDS = 2.5f

    const val WIND_ACCEL = 62f
    const val WIND_MAX_VY = 13f

    const val TELEGRAPH_SECONDS = 0.65f
    const val TELEGRAPH_ASSIST_SECONDS = 1.0f
    const val STRIKE_SECONDS = 0.35f
    const val BEAM_HALF_HEIGHT = 0.3f

    const val TIDE_BEGIN_ROWS = -7f
    const val TIDE_RISE_ROWS = 0.11f

    const val VISIBLE_ROWS = 13f
    const val CAMERA_LERP = 0.1f
    const val CAMERA_LEAD = 1.4f
    const val DEATH_DROP_ROWS = 6.8f

    const val HERO_WIDTH = 0.55f
    const val FOOTING_SPEED = 2.6f
    const val LANDING_SLACK = 0.36f
    const val HERO_HEIGHT = 0.72f
    const val RELIC_RADIUS = 0.62f
    const val DISSOLVE_SECONDS = 0.6f
    const val FLASH_SECONDS = 0.12f
    const val HINT_SECONDS = 6f

    const val METERS_PER_ROW = 3
    const val RELIC_SCORE = 120
    const val FLOOR_CLEAR_BONUS = 500

    const val SENTINEL_RING_PERIOD = 2.4f
    const val SENTINEL_RING_SPEED = 2.6f
    const val SENTINEL_RING_BAND = 0.4f
    const val SENTINEL_HITS_TO_CLEAR = 3
    const val SENTINEL_CORE_RADIUS = 0.9f
    const val SENTINEL_COOLDOWN = 1.2f

    const val FLOOR_COUNT = 9
    const val TEMPLE_SIZE = 3
    const val RELIC_PER_FLOOR = 3

    const val EDITOR_COLS = 7
    const val EDITOR_ROWS = 14
    const val EDITOR_MAX_PIECES = 40
    const val CUSTOM_SLOTS = 3

    const val DASH_REACH_LANES = 1.9f
    const val DASH_RISE_ROWS = 2f
    const val MAX_DASH_REACH_ROWS = 2.3f
    const val MAX_DASH_REACH_LANES = 3f
    const val GENERATION_ATTEMPTS = 20
    const val LANE_MARGIN = 1.15f
}
