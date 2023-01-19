package com.example.appfinalproject_10130492.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.appfinalproject_10130492.data.Assignment

/**
 * The SQLite Database Helper
 */
class SQLiteHelper(val context: Context?): SQLiteOpenHelper(context,DATABASE_NAME, null, DATABASE_VERSION ) {
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
            "FOREIGN KEY(courseName) REFERENCES Course(courseName) " +
            "ON DELETE CASCADE " +
            "ON UPDATE CASCADE" +
            " )"
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
    private val SQL_DELETE_ENTRIES_NOTIFICATIONS = "DROP TABLE IF EXISTS $notificationTableName"
    override fun onConfigure(db: SQLiteDatabase?) {
        db?.setForeignKeyConstraintsEnabled(true)
        super.onConfigure(db)
    }
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
        if(oldVersion == 15 && newVersion == 17){
            rebuildAssignments(db)
            return
        }
        if(oldVersion == 16 && newVersion == 17){
        }
        if(oldVersion == 15 && newVersion == 16){
            rebuildAssignments(db)
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
    fun rebuildAssignments(db: SQLiteDatabase){
        var assList: ArrayList<Assignment>
        fun cursorParser(cursor: Cursor): ArrayList<Assignment>{
            val assList = ArrayList<Assignment>()
            if (cursor.moveToFirst())
                while(!cursor.isAfterLast){
                    // _id note title assignedDate dueDate courseName
                    Log.i("Menu",cursor.getString(2))
                    val _id = cursor.getInt(0)
                    val note = cursor.getString(5)
                    val finished = cursor.getInt(6)
                    val title = cursor.getString(2)
                    val assignedDate = cursor.getLong(3)
                    val dueDate = cursor.getLong(4)
                    val courseName = cursor.getString(1)
                    val assignment = Assignment(_id,title,assignedDate,dueDate,note,courseName,finished)
                    cursor.moveToNext()
                    assList.add(assignment)
                }
            return assList
        }
        val cursor =
            db.query(false,assignTableName, null,null, null, null, null, "dueDate DESC", null)
        assList = cursorParser(cursor)
        cursor.close()

        db.execSQL(SQL_DELETE_ENTRIES_ASSIGN)
        db.execSQL(SQL_CREATE_ENTRIES_ASSIGN)

        for(ass in assList){
            val toInsert = ContentValues()
            toInsert.put("note", ass.note)
            toInsert.put("title",ass.title)
            toInsert.put("assignedDate",ass.assignedDate)
            toInsert.put("dueDate",ass.dueDate)
            toInsert.put("courseName",ass.courseName)
            toInsert.put("finished",ass.finished)

            val res = db.insert(assignTableName,null,toInsert)

        }
        return
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 17
        const val DATABASE_NAME = "School.db"

    }
}