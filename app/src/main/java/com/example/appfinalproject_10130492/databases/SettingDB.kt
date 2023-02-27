package com.example.appfinalproject_10130492.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.appfinalproject_10130492.data.Course
import com.example.appfinalproject_10130492.data.Setting

class SettingDB(context: Context?) {
    private val dbHelper = SQLiteHelper(context);
    private val db: SQLiteDatabase = dbHelper.readableDatabase

    fun read(): Setting{
        val cursor = db.query(false,dbHelper.settingTableName,null,null,null,null,null,null,null)
        return cursorParser(cursor)
    }
    fun deleteAll(){
        db.execSQL("DELETE FROM ${dbHelper.settingTableName}")
    }
    fun insert(setting: Setting): Boolean{
        val toInsert = ContentValues()
        toInsert.put("toggleLate", setting.toggleLate)
        toInsert.put("toggleDue", setting.toggleDue)
        toInsert.put("duePercentage", setting.duePercentage)

        val res = db.insert(dbHelper.settingTableName,null,toInsert)
        return res != (-1).toLong()
    }
    private fun cursorParser(cursor: Cursor): Setting{
        var setting = Setting(1,1,90)
        if (cursor.moveToFirst()) {
            val toggleLate = cursor.getInt(0)
            val toggleDue = cursor.getInt(1);
            val duePercentage = cursor.getInt(2)
            setting = Setting(toggleLate, toggleDue, duePercentage)
            Log.i("Courses", "settingsDB CursorParser ")
        }
        cursor.close()
        return setting
    }
}