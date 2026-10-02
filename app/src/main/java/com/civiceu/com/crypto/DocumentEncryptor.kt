package com.civiceu.com.crypto

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import com.civiceu.com.model.EncryptedEvidence
import com.civiceu.com.util.ExifSanitizer
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.SecureRandom
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object DocumentEncryptor {
    private const val TAG = "DocumentEncryptor"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val AES_KEY_SIZE = 256
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 128
    private const val ENCRYPTED_DIR = "encrypted_evidence"
    private const val TEMP_DIR = "decrypted_temp"

    /**
     * Generates a new 256-bit AES secret key.
     */
    fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(AES_KEY_SIZE)
        return keyGenerator.generateKey()
    }

    /**
     * Encrypts the file at [uri] on-device using AES-256-GCM.
     * Writes encrypted bytes directly to local private storage.
     */
    fun encryptFile(context: Context, uri: Uri): EncryptedEvidence? {
        return try {
            val contentResolver = context.contentResolver
            val (originalName, originalSize, mimeType) = getFileInfo(context, uri)

            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val rawBytes = inputStream.use { it.readBytes() }
            val sanitizedBytes = ExifSanitizer.stripMetadata(context, rawBytes, mimeType)

            val secretKey = generateKey()
            val keyBytes = secretKey.encoded
            val keyBase64 = Base64.encodeToString(keyBytes, Base64.NO_WRAP)

            val iv = ByteArray(GCM_IV_LENGTH)
            SecureRandom().nextBytes(iv)
            val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

            val encryptedBytes = cipher.doFinal(sanitizedBytes)

            val dir = File(context.filesDir, ENCRYPTED_DIR)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val fileId = UUID.randomUUID().toString()
            val encryptedFile = File(dir, "$fileId.enc")
            FileOutputStream(encryptedFile).use { fos ->
                fos.write(encryptedBytes)
            }

            EncryptedEvidence(
                id = fileId,
                originalName = originalName,
                mimeType = mimeType,
                originalSize = if (originalSize > 0) originalSize else rawBytes.size.toLong(),
                encryptedFilePath = encryptedFile.absolutePath,
                encryptionKeyBase64 = keyBase64,
                ivBase64 = ivBase64,
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error encrypting document", e)
            null
        }
    }

    /**
     * Decrypts the given [evidence] using the specified [keyBase64] (or evidence key).
     * Returns raw decrypted byte array or null if decryption fails.
     */
    fun decryptFile(evidence: EncryptedEvidence, keyBase64: String? = null): ByteArray? {
        return try {
            val keyStr = keyBase64 ?: evidence.encryptionKeyBase64
            val keyBytes = Base64.decode(keyStr, Base64.NO_WRAP)
            val secretKey = SecretKeySpec(keyBytes, "AES")

            val ivBytes = Base64.decode(evidence.ivBase64, Base64.NO_WRAP)
            val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, ivBytes)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

            val encryptedFile = File(evidence.encryptedFilePath)
            if (!encryptedFile.exists()) return null

            val encryptedBytes = FileInputStream(encryptedFile).use { it.readBytes() }
            cipher.doFinal(encryptedBytes)
        } catch (e: Exception) {
            Log.e(TAG, "Error decrypting document", e)
            null
        }
    }

    /**
     * Decrypts [evidence] and saves it to a temporary file in cache directory for viewing/playback.
     */
    fun decryptToTempFile(context: Context, evidence: EncryptedEvidence, keyBase64: String? = null): File? {
        val decryptedBytes = decryptFile(evidence, keyBase64) ?: return null
        return try {
            val tempDir = File(context.cacheDir, TEMP_DIR)
            if (!tempDir.exists()) {
                tempDir.mkdirs()
            }
            // Clean up old temp files
            tempDir.listFiles()?.forEach { file ->
                if ((System.currentTimeMillis() - file.lastModified()) > (30 * 60 * 1000)) {
                    file.delete()
                }
            }

            val extension = if (evidence.originalName.contains(".")) {
                "." + evidence.originalName.substringAfterLast(".")
            } else ""

            val tempFile = File(tempDir, "temp_${evidence.id}$extension")
            FileOutputStream(tempFile).use { fos ->
                fos.write(decryptedBytes)
            }
            tempFile
        } catch (e: Exception) {
            Log.e(TAG, "Error creating decrypted temp file", e)
            null
        }
    }

    /**
     * Copies the encryption key to the system clipboard.
     */
    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }

    /**
     * Extracts original file metadata (name, size, MIME type) from Uri.
     */
    private fun getFileInfo(context: Context, uri: Uri): Triple<String, Long, String> {
        var name = "evidence_${System.currentTimeMillis()}"
        var size = 0L
        var mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"

        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (nameIndex != -1) {
                        val displayName = cursor.getString(nameIndex)
                        if (!displayName.isNullOrBlank()) {
                            name = displayName
                        }
                    }
                    if (sizeIndex != -1) {
                        size = cursor.getLong(sizeIndex)
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not resolve file info from URI", e)
        }

        if (mimeType == "application/octet-stream") {
            when {
                name.endsWith(".pdf", ignoreCase = true) -> mimeType = "application/pdf"
                name.endsWith(".jpg", ignoreCase = true) || name.endsWith(".jpeg", ignoreCase = true) -> mimeType = "image/jpeg"
                name.endsWith(".png", ignoreCase = true) -> mimeType = "image/png"
                name.endsWith(".mp3", ignoreCase = true) -> mimeType = "audio/mpeg"
                name.endsWith(".m4a", ignoreCase = true) || name.endsWith(".aac", ignoreCase = true) -> mimeType = "audio/aac"
                name.endsWith(".wav", ignoreCase = true) -> mimeType = "audio/wav"
            }
        }

        return Triple(name, size, mimeType)
    }
}
