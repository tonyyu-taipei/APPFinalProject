package com.example.appfinalproject_10130492.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.appfinalproject_10130492.data.Assignment

class AssignmentsDB(context: Context?) {
    val dbHelper = AssignmentsSQLHelper(context);
    val db = dbHelper.readableDatabase

    fun readAll(): Cursor{
        val cursor = db.query(false, dbHelper.tableName,null,null,null,null,null,null,null)
        return cursor
    }
    fun deleteOne(id: Int): Boolean{
        val res = db.delete(dbHelper.tableName,"_id = $id",null)
        return res > 0
    }
    fun insert(assignment: Assignment): Boolean{
        val toInsert = ContentValues()
        toInsert.put("note", assignment.note)
        toInsert.put("title",assignment.title)
        toInsert.put("assignedDate",assignment.assignedDate)
        toInsert.put("dueDate",assignment.dueDate)
        toInsert.put("courseName",assignment.courseName)
        val res = db.insert(dbHelper.tableName,null,toInsert)
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

        val res= db.update(dbHelper.tableName,update,"id = ${assignment.id}",null)
        return res > 0
    }
}