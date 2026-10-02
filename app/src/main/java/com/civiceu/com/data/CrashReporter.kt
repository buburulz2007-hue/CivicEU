package com.civiceu.com.data

import android.content.Context
import android.os.Build
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

object CrashReporter {
    private const val LOG_FILE_NAME = "crash_logs.txt"

    fun init(context: Context) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            saveCrashReport(context, throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun saveCrashReport(context: Context, throwable: Throwable) {
        try {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            throwable.printStackTrace(pw)
            val stackTrace = sw.toString()

            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val report = """
                
                ---------- CRASH REPORT ----------
                Timestamp: $timestamp
                Device: ${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE})
                App Version: 1.3
                
                Stack Trace:
                $stackTrace
                ----------------------------------
            """.trimIndent()

            val logFile = File(context.getExternalFilesDir(null), LOG_FILE_NAME)
            FileOutputStream(logFile, true).use { output ->
                output.write(report.toByteArray())
            }
        } catch (e: Exception) {
            Log.e("CrashReporter", "Error saving crash report", e)
        }
    }

    fun getLogs(context: Context): String {
        val logFile = File(context.getExternalFilesDir(null), LOG_FILE_NAME)
        return if (logFile.exists()) {
            logFile.readText()
        } else {
            "No logs found."
        }
    }

    fun clearLogs(context: Context) {
        val logFile = File(context.getExternalFilesDir(null), LOG_FILE_NAME)
        if (logFile.exists()) {
            logFile.delete()
        }
    }
}
