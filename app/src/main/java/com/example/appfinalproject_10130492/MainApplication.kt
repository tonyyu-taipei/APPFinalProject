package com.example.appfinalproject_10130492

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.google.android.material.color.DynamicColors

open class MainApplication: Application() {
    override fun onCreate() {
        DynamicColors.applyToActivitiesIfAvailable(this)
        super.onCreate()
        createNotificationChannel()

    }
    private fun createNotificationChannel(){
        val channelDue = NotificationChannel(NotificationService.DUE_CHANNEL_ID,resources.getString(R.string.due_notification),NotificationManager.IMPORTANCE_HIGH)
        channelDue.description = resources.getString(R.string.due_notification_channel_description)

        val channelLate = NotificationChannel(NotificationService.LATE_CHANNEL_ID,resources.getString(R.string.late_notification),NotificationManager.IMPORTANCE_DEFAULT)
        channelLate.description = resources.getString(R.string.late_notification_channel_description)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channelDue)
        notificationManager.createNotificationChannel(channelLate)
    }
}