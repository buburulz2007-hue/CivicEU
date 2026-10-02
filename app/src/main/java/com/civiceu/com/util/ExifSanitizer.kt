package com.civiceu.com.util

import android.content.Context
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream

object ExifSanitizer {
    private const val TAG = "ExifSanitizer"

    private val EXIF_TAGS_TO_REMOVE = listOf(
        ExifInterface.TAG_GPS_LATITUDE,
        ExifInterface.TAG_GPS_LONGITUDE,
        ExifInterface.TAG_GPS_ALTITUDE,
        ExifInterface.TAG_GPS_LATITUDE_REF,
        ExifInterface.TAG_GPS_LONGITUDE_REF,
        ExifInterface.TAG_GPS_ALTITUDE_REF,
        ExifInterface.TAG_GPS_TIMESTAMP,
        ExifInterface.TAG_GPS_DATESTAMP,
        ExifInterface.TAG_GPS_PROCESSING_METHOD,
        ExifInterface.TAG_GPS_SPEED,
        ExifInterface.TAG_GPS_SPEED_REF,
        ExifInterface.TAG_MAKE,
        ExifInterface.TAG_MODEL,
        ExifInterface.TAG_SOFTWARE,
        ExifInterface.TAG_DATETIME,
        ExifInterface.TAG_DATETIME_ORIGINAL,
        ExifInterface.TAG_DATETIME_DIGITIZED,
        ExifInterface.TAG_SUBSEC_TIME,
        ExifInterface.TAG_SUBSEC_TIME_ORIGINAL,
        ExifInterface.TAG_SUBSEC_TIME_DIGITIZED,
        ExifInterface.TAG_ARTIST,
        ExifInterface.TAG_COPYRIGHT,
        ExifInterface.TAG_USER_COMMENT,
        ExifInterface.TAG_IMAGE_DESCRIPTION,
        ExifInterface.TAG_DEVICE_SETTING_DESCRIPTION,
    )

    /**
     * Removes EXIF metadata (GPS, camera model, date/time, software) from the provided raw bytes if it's an image.
     */
    fun stripMetadata(context: Context, rawBytes: ByteArray, mimeType: String): ByteArray {
        if (!mimeType.startsWith("image/", ignoreCase = true)) {
            return rawBytes
        }

        return try {
            val tempFile = File.createTempFile("exif_strip_", ".tmp", context.cacheDir)
            FileOutputStream(tempFile).use { fos ->
                fos.write(rawBytes)
            }

            val exif = ExifInterface(tempFile.absolutePath)
            var modified = false

            for (tag in EXIF_TAGS_TO_REMOVE) {
                if (exif.getAttribute(tag) != null) {
                    exif.setAttribute(tag, null)
                    modified = true
                }
            }

            if (modified) {
                exif.saveAttributes()
            }

            val cleanBytes = tempFile.readBytes()
            tempFile.delete()
            cleanBytes
        } catch (e: Exception) {
            Log.e(TAG, "Error stripping EXIF metadata from image", e)
            rawBytes
        }
    }
}
