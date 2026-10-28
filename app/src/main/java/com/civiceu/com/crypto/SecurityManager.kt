package com.civiceu.com.crypto

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.civiceu.com.data.ReportRepository
import com.civiceu.com.util.UserProfileManager
import java.io.File
import java.security.MessageDigest

object SecurityManager {
    private const val PREFS_NAME = "civic_security_prefs"
    private const val KEY_REAL_PIN_HASH = "real_pin_hash"
    private const val KEY_DECOY_PIN_HASH = "decoy_pin_hash"
    private const val TAG = "SecurityManager"

    enum class PinResult {
        SUCCESS, DECOY, FAIL, NOT_SET
    }

    private fun hashPin(pin: String): String {
        return try {
            val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray(Charsets.UTF_8))
            bytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            pin // Fallback to raw if crypto fails, highly unlikely
        }
    }

    fun hasPinSet(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.contains(KEY_REAL_PIN_HASH)
    }

    fun setPins(context: Context, realPin: String, decoyPin: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString(KEY_REAL_PIN_HASH, hashPin(realPin))
            putString(KEY_DECOY_PIN_HASH, hashPin(decoyPin))
            apply()
        }
    }

    fun clearPins(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    fun verifyPin(context: Context, pin: String): PinResult {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val realHash = prefs.getString(KEY_REAL_PIN_HASH, null)
        val decoyHash = prefs.getString(KEY_DECOY_PIN_HASH, null)

        if (realHash == null) return PinResult.NOT_SET

        val inputHash = hashPin(pin)

        return when (inputHash) {
            realHash -> PinResult.SUCCESS
            decoyHash -> PinResult.DECOY
            else -> PinResult.FAIL
        }
    }

    /**
     * Executes the military-grade Panic Wipe.
     * Clears all reports, user profile, and physical encrypted evidence from the disk.
     */
    fun executePanicWipe(context: Context) {
        Log.w(TAG, "EMERGENCY PROTOCOL ACTIVATED. Wiping all local data...")
        
        // 1. Clear In-Memory Reports
        ReportRepository.clearAll()
        
        // 2. Clear Encrypted User Profile
        UserProfileManager.clearProfile(context)
        
        // 3. Clear App Cache and Evidence Directory
        try {
            val evidenceDir = File(context.filesDir, "evidence")
            if (evidenceDir.exists()) {
                evidenceDir.deleteRecursively()
            }
            context.cacheDir.deleteRecursively()
            context.externalCacheDir?.deleteRecursively()
            
            // Re-create the evidence directory cleanly
            evidenceDir.mkdirs()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fully wipe evidence directory", e)
        }
        
        Log.w(TAG, "Wipe complete.")
    }
}
