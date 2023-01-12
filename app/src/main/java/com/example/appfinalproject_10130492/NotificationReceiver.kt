package com.example.appfinalproject_10130492

import android.app.AlarmManager
import android.app.Notification
import android.app.TaskInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.appfinalproject_10130492.data.Assignment
import com.example.appfinalproject_10130492.databases.AssignmentsDB

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val service = NotificationService(context)
        val assignmentDB = AssignmentsDB(context)
        val id = intent?.getIntExtra("NOTIFICATION_ID",-1)
        Log.i("Notification",id.toString())
        if( id != null && id != -1){
            val assignment = assignmentDB.read(id)
            assignment?.finished = 1
            if (assignment != null) {
                assignmentDB.update(assignment)
                service.cancelNotification(id)
            }
        }
    }

}