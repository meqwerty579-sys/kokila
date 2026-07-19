package com.example

import android.app.Application
import com.example.di.AppContainer
import com.example.core.release.ReleaseValidator

class KokilaApplication : Application() {
    
    // Lazy initialization of AppContainer to avoid overhead on Cold Starts
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
        
        // Asynchronously run release validation on startup to compile and save report
        Thread {
            try {
                ReleaseValidator.validateAndGenerateReport(this)
            } catch (e: Exception) {
                android.util.Log.e("KokilaApplication", "Failed to run release validation on startup", e)
            }
        }.start()
    }
}
