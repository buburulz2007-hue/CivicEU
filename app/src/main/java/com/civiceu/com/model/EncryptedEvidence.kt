package com.civiceu.com.model

import java.util.Locale
import java.util.UUID

data class EncryptedEvidence(
    val id: String = UUID.randomUUID().toString(),
    val originalName: String,
    val mimeType: String,
    val originalSize: Long,
    val encryptedFilePath: String,
    val encryptionKeyBase64: String,
    val ivBase64: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedSize: String
        get() {
            return when {
                originalSize < 1024 -> "$originalSize B"
                originalSize < 1024 * 1024 -> "${originalSize / 1024} KB"
                else -> String.format(Locale.getDefault(), "%.2f MB", originalSize / (1024.0 * 1024.0))
            }
        }

    val isImage: Boolean
        get() = mimeType.startsWith("image/", ignoreCase = true)

    val isAudio: Boolean
        get() = mimeType.startsWith("audio/", ignoreCase = true)

    val isPdf: Boolean
        get() = mimeType.equals("application/pdf", ignoreCase = true) || originalName.endsWith(".pdf", ignoreCase = true)
}
