package com.example.appfinalproject_10130492.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.appfinalproject_10130492.data.Course
import com.example.appfinalproject_10130492.data.Notification

class NotificationsDB(context: Context){
    /*  id INTEGER PRIMARY KEY
        assignId INTEGER NOT NULL
        notifyDate DATETIME NOT NULL
        notifyType TEXT NOT NULL)*/
    val dbHelper = SQLiteHelper(context)
    val db = dbHelper.readableDatabase

    private fun readAllCursor(): Cursor{
        return db.query(false, dbHelper.notificationTableName, null, null, null, null, null, null, null)
    }
    fun readAll(): ArrayList<Notification>{
        return cursorParser(readAllCursor())
    }
    fun deleteOne(id: Int): Boolean{
        val res = db.delete(dbHelper.notificationTableName,"id = '$id'",null)
        return res > 0
    }

    fun insert(notification: Notification): Boolean{
        val toInsert = ContentValues()
        toInsert.put("assignId", notification.assignmentID)
        toInsert.put("notifyDate",notification.notifyDate)
        toInsert.put("notifyType",notification.notifyType)
        val res = db.insert(dbHelper.notificationTableName,null,toInsert)
        return res != (-1).toLong()
    }

    private fun cursorParser(cursor: Cursor): ArrayList<Notification> {
        val notificationList = ArrayList<Notification>()
        if (cursor!=null && cursor.moveToFirst())
            while(!cursor.isAfterLast){
                val id = cursor.getInt(0)
                val assignID = cursor.getInt(1);
                val notifyDate = cursor.getLong(2);
                val notifyType = cursor.getString(3)
                cursor.moveToNext()

                val notification = Notification(id,assignID,notifyDate,notifyType)
                notificationList.add(notification)
            }
        return notificationList
    }
}