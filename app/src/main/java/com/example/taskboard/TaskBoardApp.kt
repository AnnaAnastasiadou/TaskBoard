package com.example.taskboard

import android.app.Application
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaskBoardApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()      // Finds DB/Prefs reads on Main thread
                    .detectDiskWrites()     // Finds DB/Prefs writes on Main thread
                    .detectNetwork()        // Finds API calls on Main thread (Critical!)
                    .penaltyLog()           // Logs the violation to Logcat
//                    .penaltyDeath()         // Crashes app to show you exactly where the lag is
                    .build()
            )

            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects() // Finds unclosed DB/Streams
                    .penaltyLog()
                    .build()
            )
        }
    }
}