package com.example.appfinalproject_10130492.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.appfinalproject_10130492.data.Course

class CoursesDB(context: Context?) {
    private val dbHelper = SQLiteHelper(context);
    val db: SQLiteDatabase = dbHelper.readableDatabase

    fun readAllCursor(): Cursor {
        return db.query(false, dbHelper.courseTableName, null, null, null, null, null, null, null)
    }
    fun deleteOne(courseName: String): Boolean{
        val res = db.delete(dbHelper.courseTableName,"courseName = $courseName",null)
        return res > 0
    }
    fun insert(course: Course): Boolean{
        val toInsert = ContentValues()
        toInsert.put("courseName", course.couseName)
        toInsert.put("teacher",course.teacher)
        val res = db.insert(dbHelper.courseTableName,null,toInsert)
        return res != (-1).toLong()
    }
    fun update(course: Course): Boolean{
        val update = ContentValues().apply{
            put("courseName", course.couseName)
            put("teacher",course.teacher)
        }

        val res= db.update(dbHelper.courseTableName,update,"id = ${course.couseName}",null)
        return res > 0
    }
}