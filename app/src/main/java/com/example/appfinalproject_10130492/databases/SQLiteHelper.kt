package com.example.appfinalproject_10130492.databases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class SQLiteHelper(context: Context?): SQLiteOpenHelper(context,DATABASE_NAME, null, DATABASE_VERSION ) {
    val courseTableName: String = "Course"
    val assignTableName = "Assignments"
    val settingTableName = "Settings"
    val notificationTableName = "Notifications"
    private val SQL_CREATE_ENTRIES_COURSES = "CREATE TABLE $courseTableName (courseName TEXT NOT NULL PRIMARY KEY, teacher TEXT)"
    private val SQL_CREATE_ENTRIES_ASSIGN = " CREATE TABLE $assignTableName ("+
            "_id INTEGER PRIMARY KEY,"+
            " note TEXT, title TEXT NOT NULL,"+
            " assignedDate DATETIME NOT NULL,"+
            " dueDate DATETIME NOT NULL,"+
            "courseName TEXT ,"+
            "finished INTEGER DEFAULT 0,"+
            "FOREIGN KEY(courseName) REFERENCES Course(courseName) )"
    private val SQL_CREATE_ENTRIES_SETTINGS = "CREATE TABLE $settingTableName " +
            "(toggleLate INTEGER DEFAULT 1," +
            " toggleDue INTEGER DEFAULT 1," +
            " duePercentage INTEGER DEFAULT 90 ) "
    private val SQL_CREATE_ENTRIES_NOTIFICATIONS = "CREATE TABLE $notificationTableName"+
            "(id INTEGER PRIMARY KEY, " +
            "assignId INTEGER NOT NULL," +
            " notifyDate DATETIME NOT NULL," +
            " notifyType TEXT NOT NULL)"

    private val SQL_INSERT_FIRST_SETTING = "INSERT INTO Settings(toggleDue) VALUES (1)"
    private val SQL_DELETE_ENTRIES_COURSES = "DROP TABLE IF EXISTS $courseTableName"
    private val SQL_DELETE_ENTRIES_SETTINGS = "DROP TABLE IF EXISTS $settingTableName"
    private val SQL_DELETE_ENTRIES_ASSIGN = "DROP TABLE IF EXISTS $assignTableName"
    private val SQL_DELETE_ENTRIES_NOTIFICATIONS = "DROP TABLE IF EXISTS $assignTableName"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES_COURSES)
        db.execSQL(SQL_CREATE_ENTRIES_ASSIGN)
        db.execSQL(SQL_CREATE_ENTRIES_SETTINGS)
        db.execSQL(SQL_INSERT_FIRST_SETTING)
        db.execSQL(SQL_CREATE_ENTRIES_NOTIFICATIONS)
        Log.d("DB","Creating Databases")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        if(oldVersion == 10 && (newVersion == 11 || newVersion == 12)){
            db.execSQL(SQL_CREATE_ENTRIES_NOTIFICATIONS)
            return
        }else if(oldVersion == 11 && newVersion == 12){
            db.execSQL(SQL_DELETE_ENTRIES_NOTIFICATIONS)
            return
        }
        db.execSQL(SQL_DELETE_ENTRIES_COURSES)
        db.execSQL(SQL_DELETE_ENTRIES_ASSIGN)
        db.execSQL(SQL_DELETE_ENTRIES_SETTINGS)
        db.execSQL(SQL_DELETE_ENTRIES_NOTIFICATIONS)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 11
        const val DATABASE_NAME = "School.db"

    }
}