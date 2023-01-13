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
        val mainActivityIntent = Intent(context,MainActivity::class.java)

        val maPendingIntent = PendingIntent.getActivity(context,id,mainActivityIntent,
            PendingIntent.FLAG_IMMUTABLE)





        if(setting.toggleDue == 1) {
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra("CHANNEL_ID",NotificationService.DUE_CHANNEL_ID)
            intent.putExtra("assignment",assignment)
            val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_IMMUTABLE)
            val dueTime = dueNotiCalc(assignment.assignedDate,assignment.dueDate,setting)
            val clockInfo = AlarmManager.AlarmClockInfo(dueTime,maPendingIntent)
            alarmManager.setAlarmClock(clockInfo,pendingIntent)
            val notification = Notification(-1, assignment.id!!, dueTime,NotificationService.DUE_CHANNEL_ID)
            notificationsDB.insert(notification)
        }
        if(setting.toggleLate == 1){
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra("CHANNEL_ID",NotificationService.LATE_CHANNEL_ID)
            Log.i("Notification","Alarm Set: $assignment")
            intent.putExtra("assignment",assignment)
            val pendingIntent = PendingIntent.getBroadcast(context, id+1000, intent, PendingIntent.FLAG_IMMUTABLE)

            val clockInfo = AlarmManager.AlarmClockInfo(assignment.dueDate,maPendingIntent)
            alarmManager.setAlarmClock(clockInfo,pendingIntent)
            val notification = Notification(-1, assignment.id!!, assignment.dueDate,NotificationService.LATE_CHANNEL_ID)
            notificationsDB.insert(notification)
        }



    }

    /**
     * RESTART ALARM will run after rebooting the device
     */
    fun restartAlarm(){
        if(!isAlarmInitialized())
            return
        val notificationsList = notificationsDB.readAll()
        val mainActivityIntent = Intent(context,MainActivity::class.java)

        notificationsList.forEach {
            run{
                val maPendingIntent = PendingIntent.getActivity(context,it.assignmentID,mainActivityIntent,
                    PendingIntent.FLAG_IMMUTABLE)
                val intent = Intent(context, AlarmReceiver::class.java)
                val assignment = assignmentsDB.read(it.assignmentID)
                intent.putExtra("CHANNEL_ID",it.notifyType)
                intent.putExtra("assignment",assignment)

                val isNotificationChannelLate = it.notifyType == NotificationService.LATE_CHANNEL_ID

                val pendingIntent = (if(isNotificationChannelLate)
                    assignment?.id?.plus(1000)
                else
                    assignment?.id)?.let { it1 ->
                    PendingIntent.getBroadcast(context,
                        it1,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                }
                val clockInfo = AlarmManager.AlarmClockInfo(
                    it.notifyDate,
                    maPendingIntent
                )
                alarmManager.setAlarmClock(clockInfo,pendingIntent)
            }
        }

    }
    fun cancelAlarm(assignment: Assignment){
        if(!isAlarmInitialized())
            return
        val intent = Intent(context,AlarmReceiver::class.java)
        val matchAssignNotifications = assignment.id?.let { notificationsDB.read(it) }
        matchAssignNotifications?.forEach {
            run {
                if (it.notifyType == NotificationService.DUE_CHANNEL_ID) {
                    val pendingIntent = PendingIntent.getBroadcast(context,it.assignmentID,intent,PendingIntent.FLAG_UPDATE_CURRENT)
                    alarmManager.cancel(pendingIntent)
                }
                if(it.notifyType == NotificationService.LATE_CHANNEL_ID){
                    val pendingIntent = PendingIntent.getBroadcast(context,it.assignmentID+1000,intent,PendingIntent.FLAG_UPDATE_CURRENT)
                    alarmManager.cancel(pendingIntent)
                }
            }
        }
        assignment.id?.let { notificationsDB.deleteOne(it) }
    }
    companion object{
        fun isAlarmInitialized(): Boolean{
            return this::alarmManager.isInitialized
        }
        lateinit var alarmManager: AlarmManager
    }
}