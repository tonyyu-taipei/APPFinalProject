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
    private val settingDB = SettingDB(context)
    private var passInEdit = false
    fun dueNotiCalc(fromInMillis: Long, toInMillis: Long,setting: Setting): Long{
        return ((toInMillis - fromInMillis) * setting.duePercentage*0.01 + fromInMillis).toLong()
    }
    fun setAlarm(assignment: Assignment){
        if(!isAlarmInitialized()){
            return
        }
        val setting = settingDB.read()
        val id = assignment.id


        if(id == null){
            Log.e("Notification","Can't set the notification: ID is NULL")
            return
        }
        /*
        Due Assignment
         */
        val dueTime = dueNotiCalc(assignment.assignedDate,assignment.dueDate,setting)
        var notification = Notification(-1, assignment.id!!, dueTime,NotificationService.DUE_CHANNEL_ID)
        notificationsDB.insert(notification)
        startAlarm(notification,false)

        /*
        Late Assignment
         */
        notification = Notification(-1, assignment.id!!, assignment.dueDate,NotificationService.LATE_CHANNEL_ID)
        notificationsDB.insert(notification)
        startAlarm(notification,false)



    }

    /**
     * updateAlarm
     * update the existing alarm
     * @throws NullPointerException Assignment.id must not be null here
     */
    fun updateAlarm(assignment: Assignment){
        val assignmentId = assignment.id ?: throw NullPointerException()
        val notificationsList = notificationsDB.read(assignmentId)
        passInEdit = true
        cancelSpecificAlarm(assignment)

        for(notification in notificationsList){
            val notiTmp =
            if(notification.notifyType == NotificationService.LATE_CHANNEL_ID){
                notification.copy(notifyDate = assignment.dueDate)


            }else{
                notification.copy(notifyDate = dueNotiCalc(assignment.assignedDate,assignment.dueDate,settingDB.read()))
            }
            notificationsDB.update(notiTmp)
            startAlarm(notiTmp,true)
        }
    }

    /**
     * startAlarm
     * @param notification The specific Notification object that you want to start the alarm
     * @param toUpdate If you want to update the current alarm, send true to send PendingIntent.FLAG_UPDATE_CURRENT
     */
    fun startAlarm(notification: Notification, toUpdate: Boolean){
        if(!isAlarmInitialized()){
            return
        }
        val mainActivityIntent = Intent(context, MainActivity::class.java)
        mainActivityIntent.putExtra("assignment", notification.assignmentID)
        val statusCodeForLate = notification.assignmentID+1000
        val isThisLateNotification = notification.notifyType == NotificationService.LATE_CHANNEL_ID

        val maPendingIntent = PendingIntent.getActivity(context,
           notification.assignmentID,
            mainActivityIntent,
                PendingIntent.FLAG_IMMUTABLE
        )


        val assignment = assignmentsDB.read(notification.assignmentID)
        val intent = Intent(context,AlarmReceiver::class.java)

        intent.putExtra("CHANNEL_ID",notification.notifyType)
        intent.putExtra("assignment",assignment)

        val pendingStatusCode = if(isThisLateNotification)
            statusCodeForLate
        else
            notification.assignmentID

        val pendingIntent = PendingIntent.getBroadcast(context,pendingStatusCode,intent,
                PendingIntent.FLAG_IMMUTABLE
        )
        val clockInfo = AlarmManager.AlarmClockInfo(notification.notifyDate,maPendingIntent)
        alarmManager.setAlarmClock(clockInfo, pendingIntent)

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
            mainActivityIntent.putExtra("assignment",it.assignmentID)
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

                alarmManager.setAlarmClock(clockInfo, pendingIntent)

        }


    }
    fun cancelSpecificAlarm(assignment: Assignment){
        if(!isAlarmInitialized())
            return
        val intent = Intent(context,AlarmReceiver::class.java)
        val notificationService = NotificationService(context)
        if(!passInEdit){
            assignment.id?.let { notificationService.cancelNotification(it) }
        }
        val matchAssignNotifications = assignment.id?.let { notificationsDB.read(it) }
        matchAssignNotifications?.forEach {
            run {
                if (it.notifyType == NotificationService.DUE_CHANNEL_ID) {
                    val pendingIntent = PendingIntent.getBroadcast(context,it.assignmentID,intent,PendingIntent.FLAG_IMMUTABLE)
                    alarmManager.cancel(pendingIntent)
                    pendingIntent.cancel()

                }
                if(it.notifyType == NotificationService.LATE_CHANNEL_ID){
                    val pendingIntent = PendingIntent.getBroadcast(context,it.assignmentID+1000,intent,PendingIntent.FLAG_IMMUTABLE)
                    alarmManager.cancel(pendingIntent)
                    pendingIntent.cancel()
                }
                assignment.id?.let { notificationsDB.deleteOne(it) }
            }
        }
    }
    private fun cancelAllAlarm(alsoDeleteInDB: Boolean){
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