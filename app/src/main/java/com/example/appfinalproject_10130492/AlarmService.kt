package com.example.appfinalproject_10130492

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.appfinalproject_10130492.data.Assignment
import com.example.appfinalproject_10130492.databases.SettingDB

class AlarmService(val assignment: Assignment, val context: Context) {
    fun setAlarm(assignment: Assignment){
        if(!isAlarmInitialized())
            return
        val settingDB = SettingDB(context)
        val setting = settingDB.read()
        val id = assignment.id

        if(id == null){
            Log.e("Notification","Can't set the notification: ID is NULL")
            return
        }
        val mainActivityIntent = Intent(context,MainActivity::class.java)

        val maPendingIntent = PendingIntent.getActivity(context,id,mainActivityIntent,
            PendingIntent.FLAG_IMMUTABLE)



        fun dueNotiCalc(fromInMillis: Long, toInMillis: Long): Long{
            return ((toInMillis - fromInMillis) * setting.duePercentage*0.01 + fromInMillis).toLong()
        }

        if(setting.toggleDue == 1) {
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra("CHANNEL_ID",NotificationService.DUE_CHANNEL_ID)
            intent.putExtra("assignment",assignment)
            val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_IMMUTABLE)

            val clockInfo = AlarmManager.AlarmClockInfo(dueNotiCalc(assignment.assignedDate,assignment.dueDate),maPendingIntent)
            alarmManager.setAlarmClock(clockInfo,pendingIntent)
        }
        if(setting.toggleLate == 1){
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra("CHANNEL_ID",NotificationService.LATE_CHANNEL_ID)
            Log.i("Notification","Alarm Set: $assignment")
            intent.putExtra("assignment",assignment)
            val pendingIntent = PendingIntent.getBroadcast(context, id+1000, intent, PendingIntent.FLAG_IMMUTABLE)

            val clockInfo = AlarmManager.AlarmClockInfo(assignment.dueDate,maPendingIntent)
            alarmManager.setAlarmClock(clockInfo,pendingIntent)
        }



    }
    companion object{
        fun isAlarmInitialized(): Boolean{
            return this::alarmManager.isInitialized
        }
        lateinit var alarmManager: AlarmManager
    }
}