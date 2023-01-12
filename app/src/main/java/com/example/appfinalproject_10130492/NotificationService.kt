package com.example.appfinalproject_10130492

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.Calendar

class NotificationService(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    var assignmentName = ""
    var notificationId: Int = 0
    var dueDateString = ""
    fun showNotification(channelID: String){

        val activityIntent = Intent(context, MainActivity::class.java)
        activityIntent.putExtra("ID",notificationId)
        val receiverIntent = Intent(context, NotificationReceiver::class.java)
        receiverIntent.putExtra("NOTIFICATION_ID", notificationId)
        Log.i("Notification","Notification Service Received: $notificationId")
        val pendingIntent = PendingIntent.getActivity(
            context, notificationId,activityIntent,PendingIntent.FLAG_IMMUTABLE
        )
        val finishedIntent = PendingIntent.getBroadcast(
            context,
            notificationId+1000,
            receiverIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context,channelID)
            .setContentTitle(
                if(channelID == LATE_CHANNEL_ID)
                    context.resources.getString(R.string.late_notification)
                else
                    context.resources.getString(R.string.due_notification)
            )
            .setContentText(
                if(channelID == DUE_CHANNEL_ID)
                    context.resources.getString(R.string.due_notification_description,
                        assignmentName, dueDateString)
                else
                    context.resources.getString(R.string.late_notification_description,
                        assignmentName)
            )
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.close,context.resources.getString(R.string.finished),finishedIntent)
            .setSmallIcon(R.drawable.book_48px)

        notificationManager.notify(notificationId, notification.build())
    }
    fun cancelNotification(id: Int){
        notificationManager.cancel(id)
    }
    companion object{
        const val LATE_CHANNEL_ID = "late_channel"
        const val DUE_CHANNEL_ID = "due_channel"

    }
}