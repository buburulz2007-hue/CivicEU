package com.civiceu.com

import android.app.Application
import com.civiceu.com.data.CrashReporter

class CivicApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the crash reporter
        CrashReporter.init(this)
    }
}
