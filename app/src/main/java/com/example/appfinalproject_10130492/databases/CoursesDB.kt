package com.example.appfinalproject_10130492.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.appfinalproject_10130492.data.Course

class CoursesDB(context: Context?) {
    private val dbHelper = SQLiteHelper(context);
    val db: SQLiteDatabase = dbHelper.readableDatabase

    fun readAllCursor(): Cursor {
        return db.query(false, dbHelper.courseTableName, null, null, null, null, null, null, null)
    }
    fun readAll(): ArrayList<Course>{

        var assList = ArrayList<Course>()
        try {
            val cursor =
                readAllCursor()
            assList = cursorParser(cursor)
        }catch(e:Exception){

        }

        return assList
    }
    fun deleteOne(courseName: String): Boolean{
        val res = db.delete(dbHelper.courseTableName,"courseName = '$courseName'",null)
        return res > 0
    }
    fun insert(course: Course): Boolean{
        val toInsert = ContentValues()
        if(course.courseName.isEmpty()){
            return false
        }
        toInsert.put("courseName", course.courseName)
        toInsert.put("teacher",course.teacher)
        val res = db.insert(dbHelper.courseTableName,null,toInsert)
        return res != (-1).toLong()
    }

    /**
     * Update the existing course in the database
     * @param course The updated Course
     * @param courseName The course name that you want to edit
     */
    fun update(course: Course, courseName: String): Boolean{
        val update = ContentValues().apply{
            put("courseName", course.courseName)
            put("teacher",course.teacher)
        }

        val res= db.update(dbHelper.courseTableName,update,"courseName = ?",arrayOf(courseName))
        return res > 0
    }
    private fun cursorParser(cursor: Cursor): ArrayList<Course>{
        val assList = ArrayList<Course>()
        if (cursor!=null && cursor.moveToFirst())
            while(!cursor.isAfterLast){
                val courseName = cursor.getString(0)
                val teacherName:String? = cursor.getString(1);
                val course = Course(courseName,teacherName)
                cursor.moveToNext()
                assList.add(course)
                Log.i("Courses","CoursesDB CursorParser $courseName")
            }
        return assList
    }
}