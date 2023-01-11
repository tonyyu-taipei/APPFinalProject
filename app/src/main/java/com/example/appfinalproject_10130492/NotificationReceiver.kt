package com.example.appfinalproject_10130492

import android.app.TaskInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.appfinalproject_10130492.data.Assignment
import com.example.appfinalproject_10130492.databases.AssignmentsDB

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val service = NotificationService(context)
        val assignmentDB = AssignmentsDB(context)

    }

}