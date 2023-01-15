package com.example.appfinalproject_10130492.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.example.appfinalproject_10130492.data.Notification
import com.example.appfinalproject_10130492.databases.exceptions.MultipleMatchesException

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

    fun read(assignId: Int):ArrayList<Notification>{
        Log.wtf("NotificationDB","NotificationDB reading:\r\n${assignId.toString()}")
        val cursor = db.query(false,dbHelper.notificationTableName,null, "assignId = ?",
            arrayOf(assignId.toString()),null,null,null,null)
        return cursorParser(cursor)
    }

    /**
     * @throws MultipleMatchesException
     * @param[id] The ID of the Notification, note that this is NOT Assignment ID.
     */
    fun readSpecId(id:Int): Notification{
        val cursor = db.query(false,dbHelper.notificationTableName,null, "id = ",
            arrayOf(id.toString()),null,null,null,null)
        val arrList = cursorParser(cursor)
        if(arrList.size > 1){
            val arrListAny: ArrayList<Any> = ArrayList();
            arrListAny.addAll(arrList)
            throw MultipleMatchesException(arrListAny)
        }
        return arrList[0]
    }
    fun deleteAll(){
        db.rawQuery("DELETE FROM ${dbHelper.notificationTableName}",null)
    }
    fun deleteOne(assignId: Int): Boolean{
        /*
        @param

        */
        val res = db.delete(dbHelper.notificationTableName,"assignId = '$assignId'",null)
        return res > 0
    }

    fun insert(notification: Notification): Int{
        val toInsert = ContentValues()
        toInsert.put("assignId", notification.assignmentID)
        toInsert.put("notifyDate",notification.notifyDate)
        toInsert.put("notifyType",notification.notifyType)
        val res = db.insert(dbHelper.notificationTableName,null,toInsert)
        return if(res != (-1).toLong()){
            val cursor = db.rawQuery("SELECT last_insert_rowid()",null);
            cursor.moveToFirst()
            cursor.getInt(0)
        }else {
            -1
        }
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
