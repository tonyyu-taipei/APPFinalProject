package com.example.appfinalproject_10130492.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.appfinalproject_10130492.data.Assignment

class AssignmentsDB(context: Context?) {
    private val dbHelper = SQLiteHelper(context);
    val db: SQLiteDatabase = dbHelper.readableDatabase

    fun readAllCursor(): Cursor {
        return db.query(false, dbHelper.assignTableName, null, null, null, null, null, null, null)
    }

    fun readAll(): ArrayList<Assignment>{
        val assList = ArrayList<Assignment>()
        try {
            val cursor =
                db.query(false, dbHelper.assignTableName, null, null, null, null, null, null, null)
            if (cursor!=null && cursor.moveToFirst())
                while(!cursor.isAfterLast){
                    // _id note title assignedDate dueDate courseName
                    Log.i("Menu",cursor.getString(2))
                    val _id = cursor.getInt(0)
                    val note = cursor.getString(5)
                    val title = cursor.getString(2)
                    val assignedDate = cursor.getLong(3)
                    val dueDate = cursor.getLong(4)
                    val courseName = cursor.getString(1)
                    val assignment = Assignment(_id,title,assignedDate,dueDate,note,courseName)
                    cursor.moveToNext()
                    assList.add(assignment)
                }
        }catch(e:Exception){

        }

        return assList
    }
    fun deleteOne(id: Int): Boolean{
        val res = db.delete(dbHelper.assignTableName,"_id = $id",null)
        return res > 0
    }
    fun insert(assignment: Assignment): Boolean{
        val toInsert = ContentValues()
        toInsert.put("note", assignment.note)
        toInsert.put("title",assignment.title)
        toInsert.put("assignedDate",assignment.assignedDate)
        toInsert.put("dueDate",assignment.dueDate)
        toInsert.put("courseName",assignment.courseName)
        val res = db.insert(dbHelper.assignTableName,null,toInsert)
        return res != (-1).toLong()
    }
    fun update(assignment: Assignment): Boolean{
        val update = ContentValues().apply{
            put("note", assignment.note)
            put("title",assignment.title)
            put("assignedDate",assignment.assignedDate)
            put("dueDate",assignment.dueDate)
            put("courseName",assignment.courseName)
        }

        val res= db.update(dbHelper.assignTableName,update,"id = ${assignment.id}",null)
        return res > 0
    }
}