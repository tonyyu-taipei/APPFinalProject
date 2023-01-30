package com.example.appfinalproject_10130492

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.example.appfinalproject_10130492.data.Assignment
import com.example.appfinalproject_10130492.data.AssignmentsWithStatus
import com.example.appfinalproject_10130492.databases.AssignmentsDB
import org.joda.time.*

/**
 * Implementation of App Widget functionality.
 */
class AssignmentAppWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, showId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        val id = intent?.getIntExtra("showId",0)?:0
        val widgetId = intent?.getIntExtra("appWidgetId",0)?:0
        Log.d("Widget","onRecieve ID: $id")
        if(showId < 0 || showId > assignmentsStatusList.size-1){
            showId = 0;
            initAssignments(AssignmentsDB(context).readAllByStatus())
        }
        showId = id
        updateAppWidget(context,AppWidgetManager.getInstance(context),widgetId, showId)
    }
    companion object{
        private var showId = 0
        var pendingStatusCode = 0
        data class AssignmentWithStatusCode(val assignment: Assignment,var status: String)
        private val assignmentsStatusList: ArrayList<AssignmentWithStatusCode> = ArrayList()
        fun initAssignments(assignmentsWithStatus: AssignmentsWithStatus){
            assignmentsStatusList.clear()
            for(assignment in assignmentsWithStatus.late){
                assignmentsStatusList.add(AssignmentWithStatusCode(assignment,"late"))
            }
            for(assignment in assignmentsWithStatus.queued){
                assignmentsStatusList.add(AssignmentWithStatusCode(assignment,"queued"))
            }

        }
        fun isAssignmentListEmpty(): Boolean{
            return assignmentsStatusList.isEmpty()
        }
        fun assignmentWithStatusCodeSize(): Int{
            return assignmentsStatusList.size
        }
        fun readAssignmentWithStatusCode(index: Int):AssignmentWithStatusCode{
            Log.d("Widget","Read Status Code: $index \r\nReadList To String: \r\n${assignmentsStatusList.toString()}")
            if(index > assignmentsStatusList.size-1){
                return assignmentsStatusList.last()
            }else if(index < 0){
                return assignmentsStatusList.first()
            }
            return assignmentsStatusList[index]
        }

    }
}
internal fun dateToStringHelper(dueDate: Long, context: Context): String{
    val days: Int = Days.daysBetween(
        DateTime(),
        DateTime(dueDate)
    ).days
    val hours: Int = Hours.hoursBetween(
        DateTime(), DateTime(
            dueDate
        )
    ).hours - days * 24
    val mins: Int = Minutes.minutesBetween(
        DateTime(), DateTime(
        dueDate)
    ).minutes - days*24*60 -hours * 60
    val secs: Int = Seconds.secondsBetween(
        DateTime(), DateTime(
            dueDate
        )
    ).seconds - days * 24*60*60 - hours * 60*60 - mins * 60
    var output: String

    //Return Mins And Sec
    if(mins > 30 && hours == 0 && days == 0 ){
        output = "${mins}${context.resources.getString(R.string.min)}"
    }else if(mins < 0 || secs < 0 || hours < 0 || days < 0){
        output = context.getString(R.string.expired)
    }else{
        output = context.getString(R.string.upcoming)

    }
    //Returns Days And Hours
    if(days > 0) {
        output = "${days}${context.resources.getString(R.string.day)}${hours}${context.resources.getString(R.string.hour)}"
    }
    //Return Hours And Mins
    if(hours > 0 && days == 0) {
        output = "${hours}${context.resources.getString(R.string.hour)}${mins}${context.resources.getString(R.string.min)}"
    }

    Log.d("Widget","DateTime: $days, $hours, $mins, $secs")
    //Returns Sec
    return output
}
internal fun getSelfPendingIntent(context: Context, showId: Int, appWidgetId: Int): PendingIntent{
    val intent = Intent(context, AssignmentAppWidget::class.java)
    intent.putExtra("showId",showId)
    intent.putExtra("appWidgetId",appWidgetId)
    Log.d("Widget","PendingIntent ShowID: $showId")
    return PendingIntent.getBroadcast(context, AssignmentAppWidget.pendingStatusCode++,intent,PendingIntent.FLAG_IMMUTABLE)
}
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    showId: Int
) {

    var showIdWithCheck = showId
    // Get all of the assignments that's not finished
    val assignmentsDB = AssignmentsDB(context)
    val assignmentsWithStatus = assignmentsDB.readAllByStatus()
    AssignmentAppWidget.initAssignments(assignmentsWithStatus)


    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.assignment_app_widget)
    views.setViewVisibility(R.id.widget_assign_hint, View.GONE)
    //Make showId in AssignmentList circular
    if(showId >= AssignmentAppWidget.assignmentWithStatusCodeSize()){
        showIdWithCheck = 0;
    }else if(showId <0){
        showIdWithCheck = AssignmentAppWidget.assignmentWithStatusCodeSize()-1
    }
    val maIntent = Intent(context,MainActivity::class.java)
    if(AssignmentAppWidget.isAssignmentListEmpty()){
        views.setTextViewText(R.id.widget_assign_title,context.getString(R.string.empty_due_assign))
        views.setTextViewText(R.id.widget_due_clock_text," - ")
    }else {

        val dataNow = AssignmentAppWidget.readAssignmentWithStatusCode(showIdWithCheck)
        views.setTextViewText(R.id.widget_assign_title, dataNow.assignment.title)
        val dueText = dateToStringHelper(dataNow.assignment.dueDate,context)
        views.setTextViewText(
            R.id.widget_due_clock_text,
            dueText
        )
        views.setTextColor(R.id.widget_due_clock_text,Color.BLACK)
        maIntent.putExtra("assignment",dataNow.assignment.id)
        if(dueText == context.getString(R.string.expired)) {
            views.setImageViewResource(R.id.widget_due_icon, R.drawable.error_48px)
            views.setTextColor(R.id.widget_due_clock_text,Color.RED)
        }else{
            views.setImageViewResource(R.id.widget_due_icon, R.drawable.alarm_on_48px)

        }
        Log.d("Widget", dataNow.toString())
    }
    val pendingIntent = PendingIntent.getActivity(context,AssignmentAppWidget.pendingStatusCode++,maIntent,PendingIntent.FLAG_IMMUTABLE)
    views.setOnClickPendingIntent(android.R.id.background,pendingIntent)
    views.setOnClickPendingIntent(R.id.widget_button, getSelfPendingIntent(context, if(showId+1 > AssignmentAppWidget.assignmentWithStatusCodeSize()-1) 0 else showId+1,appWidgetId))
    views.setOnClickPendingIntent(R.id.widget_button_prev, getSelfPendingIntent(context, if(showId-1 < 0 ) AssignmentAppWidget.assignmentWithStatusCodeSize()-1 else showId-1,appWidgetId))
        // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.widget_assign_title)
}
