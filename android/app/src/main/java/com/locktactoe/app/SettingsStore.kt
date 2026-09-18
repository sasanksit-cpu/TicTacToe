package com.locktactoe.app

import android.content.Context

class SettingsStore(context: Context) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun isLockScreenEnabled(): Boolean = prefs.getBoolean(KEY_ENABLED, false)

    fun setLockScreenEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    companion object {
        private const val KEY_ENABLED = "lock_screen_enabled"
    }
}
