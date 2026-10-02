package com.civiceu.com.util

import android.content.Context
import android.util.Base64
import android.util.Log
import com.civiceu.com.model.UserProfile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object UserProfileManager {
    private const val TAG = "UserProfileManager"
    private const val FILE_NAME = "user_profile.enc"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val LOCAL_KEY_SEED = "CivicEncryptedProfileSecretKey_2026"
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 128

    private fun getSecretKey(): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(LOCAL_KEY_SEED.toByteArray(Charsets.UTF_8))
        return SecretKeySpec(keyBytes, "AES")
    }

    /**
     * Saves user profile encrypted on local storage.
     */
    fun saveProfile(context: Context, profile: UserProfile) {
        try {
            val rawString = "${profile.fullName}\t${profile.email}\t${profile.address}\t${profile.phone}"
            val iv = ByteArray(GCM_IV_LENGTH)
            SecureRandom().nextBytes(iv)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), gcmSpec)
            val encryptedBytes = cipher.doFinal(rawString.toByteArray(Charsets.UTF_8))

            val file = File(context.filesDir, FILE_NAME)
            FileOutputStream(file).use { fos ->
                fos.write(iv) // write 12 bytes IV first
                fos.write(encryptedBytes)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving encrypted user profile", e)
        }
    }

    /**
     * Reads user profile decrypted from local storage.
     */
    fun getProfile(context: Context): UserProfile {
        return try {
            val file = File(context.filesDir, FILE_NAME)
            if (!file.exists() || file.length() <= GCM_IV_LENGTH) return UserProfile()

            val fileBytes = FileInputStream(file).use { it.readBytes() }
            val iv = fileBytes.copyOfRange(0, GCM_IV_LENGTH)
            val encryptedBytes = fileBytes.copyOfRange(GCM_IV_LENGTH, fileBytes.size)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), gcmSpec)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            val decryptedString = String(decryptedBytes, Charsets.UTF_8)

            val parts = decryptedString.split("\t")
            UserProfile(
                fullName = parts.getOrNull(0) ?: "",
                email = parts.getOrNull(1) ?: "",
                address = parts.getOrNull(2) ?: "",
                phone = parts.getOrNull(3) ?: ""
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error reading encrypted user profile", e)
            UserProfile()
        }
    }

    /**
     * Clears user profile file.
     */
    fun clearProfile(context: Context) {
        try {
            val file = File(context.filesDir, FILE_NAME)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting user profile file", e)
        }
    }
}
