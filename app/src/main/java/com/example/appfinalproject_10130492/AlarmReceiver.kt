package com.example.appfinalproject_10130492

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import com.example.appfinalproject_10130492.data.Assignment
import java.util.*
class AlarmReceiver :BroadcastReceiver(){
    private val uDate = java.text.SimpleDateFormat("yyyy-MM-dd  aa hh:mm", Locale.TAIWAN)
    override fun onReceive(context: Context, intent: Intent) {
        AlarmService.alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        Log.i("Notification","Alarm Received")

        val assignment =
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getSerializableExtra("assignment",Assignment::class.java)
            else
                intent.getSerializableExtra("assignment") as? Assignment

        Log.i("Notification","Received Assignment: $assignment")
        val channelID: String? = intent.getStringExtra("CHANNEL_ID")
        Log.i("Notification","The Channel ID $channelID")
        val id = assignment?.id ?: return
        val notificationService = NotificationService(context)
        notificationService.notificationId = id
        notificationService.assignmentName = assignment.title
        notificationService.dueDateString = uDate.format(Date(assignment.dueDate))
        Log.i("channelID","NotificationServiceInfo $notificationService")

        if(channelID != null){
            notificationService.showNotification(channelID)
        }

    }

}