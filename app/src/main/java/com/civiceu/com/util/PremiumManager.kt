package com.civiceu.com.util

import android.content.Context

object PremiumManager {
    private const val PREFS_NAME = "civic_premium_prefs"
    private const val KEY_IS_PRO = "is_pro"

    fun isPremium(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_PRO, false)
    }

    fun setPremium(context: Context, isPro: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_PRO, isPro).apply()
    }

    fun isDeadMansSwitchEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean("dead_mans_switch", false)
    }

    fun setDeadMansSwitchEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean("dead_mans_switch", enabled).apply()
    }
}
