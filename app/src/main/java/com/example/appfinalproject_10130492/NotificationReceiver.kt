package com.example.appfinalproject_10130492

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.appfinalproject_10130492.databases.AssignmentsDB
import com.example.appfinalproject_10130492.databases.NotificationsDB

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val service = NotificationService(context)
        val assignmentDB = AssignmentsDB(context)
        val alarmService = AlarmService(context)
        val notificationsDB = NotificationsDB(context)
        val id = intent?.getIntExtra("NOTIFICATION_ID",-1)
        Log.i("Notification",id.toString())
        if( id != null && id != -1){
            val assignment = assignmentDB.read(id)
            assignment?.finished = 1
            if (assignment != null) {
                assignmentDB.update(assignment)
                service.cancelNotification(id)
                assignment.id?.let { notificationsDB.deleteOne(it) }
                alarmService.cancelSpecificAlarm(assignment)

            }
        }
    }

}