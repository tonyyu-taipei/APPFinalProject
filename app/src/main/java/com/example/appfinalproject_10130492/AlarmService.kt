package com.example.appfinalproject_10130492

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.appfinalproject_10130492.data.Assignment
import com.example.appfinalproject_10130492.data.Notification
import com.example.appfinalproject_10130492.data.Setting
import com.example.appfinalproject_10130492.databases.AssignmentsDB
import com.example.appfinalproject_10130492.databases.NotificationsDB
import com.example.appfinalproject_10130492.databases.SettingDB

class AlarmService(val context: Context) {
    private val notificationsDB = NotificationsDB(context)
    private val assignmentsDB = AssignmentsDB(context)
    fun dueNotiCalc(fromInMillis: Long, toInMillis: Long,setting: Setting): Long{
        return ((toInMillis - fromInMillis) * setting.duePercentage*0.01 + fromInMillis).toLong()
    }
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
        /*
        Due Assignment
         */
        cancelAllAlarm(false)
            val dueTime = dueNotiCalc(assignment.assignedDate,assignment.dueDate,setting)
            var notification = Notification(-1, assignment.id!!, dueTime,NotificationService.DUE_CHANNEL_ID)
            notificationsDB.insert(notification)

        /*
        Late Assignment
         */
            notification = Notification(-1, assignment.id!!, assignment.dueDate,NotificationService.LATE_CHANNEL_ID)
            notificationsDB.insert(notification)

            restartAlarm()


    }

    /**
     * RESTART ALARM will run after rebooting the device
     */
    fun restartAlarm(){
        if(!isAlarmInitialized())
            return
        val notificationsList = notificationsDB.readAll()
        val mainActivityIntent = Intent(context,MainActivity::class.java)
        val settingDB = SettingDB(context)
        val setting = settingDB.read()
        cancelAllAlarm(false)
        for(it in notificationsList) {

            val maPendingIntent = PendingIntent.getActivity(
                context, it.assignmentID, mainActivityIntent,
                PendingIntent.FLAG_MUTABLE
            )
            val assignment = assignmentsDB.read(it.assignmentID)
            val intent = Intent(context, AlarmReceiver::class.java)

            intent.putExtra("CHANNEL_ID", it.notifyType)
            intent.putExtra("assignment", assignment)

            Log.i("AlamService", "CHANNEL_ID: ${it.notifyType}\r\nAssignment: $assignment")

            val isNotificationChannelLate = it.notifyType == NotificationService.LATE_CHANNEL_ID
            if (assignment == null || assignment?.id == null){
                continue
            }


            val pendingIntent = PendingIntent.getBroadcast(context,
            if(isNotificationChannelLate) assignment.id!! + 1000 else assignment.id!!,
            intent,
            PendingIntent.FLAG_IMMUTABLE)
            val clockInfo = AlarmManager.AlarmClockInfo(
                it.notifyDate,
                maPendingIntent
            )
            //Test if setting was set to on
            val testToggleOn: Boolean =
                (setting.toggleLate == 1 && it.notifyType == NotificationService.LATE_CHANNEL_ID) ||
                        (setting.toggleDue == 1 && it.notifyType == NotificationService.DUE_CHANNEL_ID)
            if (testToggleOn) {
                alarmManager.setAlarmClock(clockInfo, pendingIntent)
            }
        }


    }
    fun cancelSpecificAlarm(assignment: Assignment){
        if(!isAlarmInitialized())
            return
        val intent = Intent(context,AlarmReceiver::class.java)
        val matchAssignNotifications = assignment.id?.let { notificationsDB.read(it) }
        matchAssignNotifications?.forEach {
            run {
                if (it.notifyType == NotificationService.DUE_CHANNEL_ID) {
                    val pendingIntent = PendingIntent.getBroadcast(context,it.assignmentID,intent,PendingIntent.FLAG_MUTABLE)
                    alarmManager.cancel(pendingIntent)
                }
                if(it.notifyType == NotificationService.LATE_CHANNEL_ID){
                    val pendingIntent = PendingIntent.getBroadcast(context,it.assignmentID+1000,intent,PendingIntent.FLAG_MUTABLE)
                    alarmManager.cancel(pendingIntent)
                }
            }
        }
        assignment.id?.let { notificationsDB.deleteOne(it) }
    }
    fun cancelAllAlarm(alsoDeleteInDB: Boolean){
        if(!isAlarmInitialized())
            return
        val intent = Intent(context,AlarmReceiver::class.java)
        val matchAssignNotifications = notificationsDB.readAll()
        matchAssignNotifications.forEach {
            run {
                if (it.notifyType == NotificationService.DUE_CHANNEL_ID) {
                    val pendingIntent = PendingIntent.getBroadcast(context,it.assignmentID,intent,PendingIntent.FLAG_MUTABLE)
                    alarmManager.cancel(pendingIntent)
                }
                if(it.notifyType == NotificationService.LATE_CHANNEL_ID){
                    val pendingIntent = PendingIntent.getBroadcast(context,it.assignmentID+1000,intent,PendingIntent.FLAG_MUTABLE)
                    alarmManager.cancel(pendingIntent)
                }
            }
        }
        if(alsoDeleteInDB){
            notificationsDB.deleteAll()
        }

    }

    companion object{
        fun isAlarmInitialized(): Boolean{
            return this::alarmManager.isInitialized
        }
        lateinit var alarmManager: AlarmManager
    }
}