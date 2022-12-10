package com.example.appfinalproject_10130492.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.appfinalproject_10130492.data.Course

class CoursesDB(context: Context?) {
    val dbHelper = AssignmentsSQLHelper(context);
    val db = dbHelper.readableDatabase

    fun readAll(): Cursor{
        val cursor = db.query(false, dbHelper.tableName,null,null,null,null,null,null,null)
        return cursor
    }
    fun deleteOne(courseName: String): Boolean{
        val res = db.delete(dbHelper.tableName,"courseName = $courseName",null)
        return res > 0
    }
    fun insert(course: Course): Boolean{
        val toInsert = ContentValues()
        toInsert.put("courseName", course.couseName)
        toInsert.put("teacher",course.teacher)
        val res = db.insert(dbHelper.tableName,null,toInsert)
        return res != (-1).toLong()
    }
    fun update(course: Course): Boolean{
        val update = ContentValues().apply{
            put("courseName", course.couseName)
            put("teacher",course.teacher)
        }

        val res= db.update(dbHelper.tableName,update,"id = ${course.couseName}",null)
        return res > 0
    }
}