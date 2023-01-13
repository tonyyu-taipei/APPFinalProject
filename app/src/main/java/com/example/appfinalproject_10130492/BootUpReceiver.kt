package com.example.appfinalproject_10130492

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService

class BootUpReceiver(): BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == "android.intent.action.BOOT_COMPLETED"){
            AlarmService.alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val restartAlarm = AlarmService(context)
            restartAlarm.restartAlarm()
        }

    }
}