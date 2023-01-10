package com.example.appfinalproject_10130492

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.appfinalproject_10130492.data.Assignment

class NotificationReceiver(val channelID: String): BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val service = NotificationService(context)
        service.showNotification(channelID)
    }
    private fun setAlarm(assignment: Assignment, context: Context){
        
    }
}