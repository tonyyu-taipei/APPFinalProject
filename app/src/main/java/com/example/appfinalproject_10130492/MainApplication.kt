package com.example.appfinalproject_10130492

import android.app.Application
import com.google.android.material.color.DynamicColors

open class MainApplication: Application() {
    override fun onCreate() {
        DynamicColors.applyToActivitiesIfAvailable(this)
        super.onCreate()
    }
}