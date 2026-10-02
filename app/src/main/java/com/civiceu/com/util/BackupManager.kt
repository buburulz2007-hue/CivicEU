package com.civiceu.com.util

import android.content.Context
import android.util.Log
import com.civiceu.com.data.ReportRepository
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object BackupManager {
    private const val TAG = "BackupManager"
    private const val BACKUP_FILE_NAME = "civic_secure_backup.enc"
    private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"

    /**
     * Exports all local reports to an encrypted backup file using a user passphrase.
     */
    fun exportBackup(context: Context, passphrase: String): File? {
        return try {
            val reports = ReportRepository.reports.value
            if (reports.isEmpty()) return null

            val dataBuilder = StringBuilder()
            reports.forEach { r ->
                dataBuilder.append("ID:${r.id}|TITLE:${r.title}|CAT:${r.category}|CODE:${r.trackingCode}|DATE:${r.timestamp}\n")
            }
            val rawData = dataBuilder.toString().toByteArray(Charsets.UTF_8)

            val digest = MessageDigest.getInstance("SHA-256")
            val keyBytes = digest.digest(passphrase.toByteArray(Charsets.UTF_8))
            val secretKey = SecretKeySpec(keyBytes, "AES")

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val encryptedBytes = cipher.doFinal(rawData)

            val backupFile = File(context.getExternalFilesDir(null), BACKUP_FILE_NAME)
            FileOutputStream(backupFile).use { fos ->
                fos.write(encryptedBytes)
            }
            backupFile
        } catch (e: Exception) {
            Log.e(TAG, "Error creating encrypted backup", e)
            null
        }
    }
}
