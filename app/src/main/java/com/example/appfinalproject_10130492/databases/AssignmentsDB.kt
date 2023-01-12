package com.example.appfinalproject_10130492.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.appfinalproject_10130492.data.Assignment
import com.example.appfinalproject_10130492.data.AssignmentsWithStatus
import com.example.appfinalproject_10130492.data.Course

class AssignmentsDB(context: Context?) {
    private val dbHelper = SQLiteHelper(context);
    val db: SQLiteDatabase = dbHelper.readableDatabase

    fun readAllCursor(): Cursor {
        return db.query(false, dbHelper.assignTableName, null, null, null, null, null, null, null)
    }


    fun read(id: Int): Assignment?{
        var assignment: Assignment? = null
        Log.i("database","database read-id: $id");
        try {
            var cursor = db.query(
                false,
                dbHelper.assignTableName,
                null,
                "_id = ?",
                Array(1) { id.toString() },
                null,
                null,
                null,
                null
            )
            Log.i("db",cursor.toString())
            assignment = cursorParser(cursor)[0]
        }catch(e: Exception){
            Log.i("db",e.toString())
            e.printStackTrace()
        }
        return assignment
    }
    fun readAll(courseName: String?): ArrayList<Assignment>{
        var assList = ArrayList<Assignment>()
        try {
            val whereClause = if(courseName == null) "" else "courseName = '$courseName'"
            val cursor =
                db.query(false, dbHelper.assignTableName, null, whereClause, null, null, null, "dueDate DESC", null)
            assList = cursorParser(cursor)
        }catch(e:Exception){

        }

        return assList
    }
    fun readAll(): ArrayList<Assignment>{
        return readAll(null)
    }

    fun readAllByCourse(course: Course): ArrayList<Assignment>{
        var assList = ArrayList<Assignment>()
        try {
            val cursor =
                db.query(false, dbHelper.assignTableName, null, "courseName = '${course.courseName}'", null, null, null, "dueDate DESC", null)
            assList = cursorParser(cursor)
        }catch(e:Exception){

        }

        return assList
    }
    fun readAllByStatus(): AssignmentsWithStatus{
        return readAllByStatus(null)
    }
    fun readAllByStatus(courseName: String?): AssignmentsWithStatus{
        var assListFinished = ArrayList<Assignment>()
        var assListQueued = ArrayList<Assignment>()
        var assListLate = ArrayList<Assignment>()
        val courseWhereClause = if(courseName == null) "" else "AND courseName = '$courseName'"
        try {
            //finished
            var cursor =
                db.rawQuery("SELECT _id,courseName,title,assignedDate,dueDate,note,finished " +
                        "FROM ${dbHelper.assignTableName} " +
                        "WHERE finished=1 $courseWhereClause " +
                        "ORDER BY dueDate DESC"
                    ,null)
            assListFinished = cursorParser(cursor)

            cursor =
                db.rawQuery("SELECT _id,courseName,title,assignedDate,dueDate,note,finished " +
                        "FROM ${dbHelper.assignTableName} " +
                        "WHERE finished=0 AND datetime(dueDate/1000,'unixepoch')<= datetime('now')  $courseWhereClause  " +
                        "ORDER BY dueDate DESC",null)
            assListLate = cursorParser(cursor)

            cursor =
                db.rawQuery("SELECT _id,courseName,title,assignedDate,dueDate,note,finished " +
                        "FROM ${dbHelper.assignTableName} " +
                        "WHERE finished=0 AND datetime(dueDate/1000,'unixepoch')> datetime('now')  $courseWhereClause  " +
                        "ORDER BY dueDate DESC",null)
            assListQueued = cursorParser(cursor)


        }catch(e:Exception){

        }

        return AssignmentsWithStatus(assListFinished,assListLate,assListQueued)
    }
    fun deleteOne(id: Int): Boolean{
        val res = db.delete(dbHelper.assignTableName,"_id = $id",null)
        return res > 0
    }
    fun deleteByCourse(courseName: String): Boolean{
        val res = db.delete(dbHelper.assignTableName,"courseName = '$courseName'",null)
        return res>0
    }
    fun insert(assignment: Assignment): Int{
        val toInsert = ContentValues()
        toInsert.put("note", assignment.note)
        toInsert.put("title",assignment.title)
        toInsert.put("assignedDate",assignment.assignedDate)
        toInsert.put("dueDate",assignment.dueDate)
        toInsert.put("courseName",if(assignment.courseName == null || assignment.courseName == "")null else assignment.courseName)
        toInsert.put("finished",assignment.finished)

        val res = db.insert(dbHelper.assignTableName,null,toInsert)
        return if(res != (-1).toLong()){
            val cursor = db.rawQuery("SELECT last_insert_rowid()",null);
            cursor.moveToFirst()
            cursor.getInt(0)
        }else {
            -1
        }
    }
    fun update(assignment: Assignment): Boolean{
        val update = ContentValues().apply{
            put("note", assignment.note)
            put("title",assignment.title)
            put("assignedDate",assignment.assignedDate)
            put("dueDate",assignment.dueDate)
            put("courseName",assignment.courseName)
            put("finished",assignment.finished)


        }

        val res= db.update(dbHelper.assignTableName,update,"_id = ${assignment.id}",null)
        return res > 0
    }

    fun cursorParser(cursor: Cursor): ArrayList<Assignment>{
        val assList = ArrayList<Assignment>()
        if (cursor!=null && cursor.moveToFirst())
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

}