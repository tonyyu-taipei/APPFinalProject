package com.example.appfinalproject_10130492.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.appfinalproject_10130492.data.Assignment
import com.example.appfinalproject_10130492.data.AssignmentsWithStatus
import com.example.appfinalproject_10130492.databases.exceptions.MultipleMatchesException
import com.example.appfinalproject_10130492.databases.exceptions.TooManyAssignmentsException

class AssignmentsDB(context: Context?) {
    private val dbHelper = SQLiteHelper(context)
    val db: SQLiteDatabase = dbHelper.readableDatabase


    /**
     * To read Specific ID in Assignment DB.
     * If multiple value matches your arg, it throws MultipleMatchesException.
     * @throws MultipleMatchesException
     * @param[id] ID of the Assignment that you want to read
     */
    fun close(){
        db.close()
    }
    fun read(id: Int): Assignment?{
        var assignment: Assignment? = null
        Log.i("database","database read-id: $id")
        try {
            val cursor = db.query(
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
            val assList = cursorParser(cursor)
            if(assList.size > 1){
                val assListAny = ArrayList<Any>()
                assListAny.addAll(assList)
                throw MultipleMatchesException(assListAny)
            }
            assignment = assList[0]
            cursor.close()
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
            cursor.close()
        }catch(e:Exception){
            e.printStackTrace()
        }

        return assList
    }
    fun readAll(): ArrayList<Assignment>{
        return readAll(null)
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
            cursor.close()

        }catch(e:Exception){
            e.printStackTrace()
        }

        return AssignmentsWithStatus(assListFinished,assListLate,assListQueued)
    }
    fun deleteOne(id: Int): Boolean{
        Log.i("DeleteQueue","Deleting $id")
        val res = db.delete(dbHelper.assignTableName,"_id = $id",null)
        return res > 0
    }
    fun deleteByCourse(courseName: String): Boolean{
        val res = db.delete(dbHelper.assignTableName,"courseName = '$courseName'",null)
        return res>0
    }

    /**
     * Insert Assignment Into Database
     * @exception TooManyAssignmentsException() The limit size of assignmentDB is 1000, due to notification status code.
     *
     */
    fun insert(assignment: Assignment): Int{
        val cur = db.rawQuery("SELECT COUNT(*) FROM ${dbHelper.assignTableName}", null)
        cur.moveToFirst()
        val count = cur.getInt(0)
        if(count > 1000){
            throw  TooManyAssignmentsException()
        }
        cur.close()
        val toInsert = ContentValues()
        toInsert.put("note", assignment.note)
        toInsert.put("title",assignment.title)
        toInsert.put("assignedDate",assignment.assignedDate)
        toInsert.put("dueDate",assignment.dueDate)
        toInsert.put("courseName",if(assignment.courseName == null || assignment.courseName == "")null else assignment.courseName)
        toInsert.put("finished",assignment.finished)

        val res = db.insert(dbHelper.assignTableName,null,toInsert)
        return if(res != (-1).toLong()){
            val cursor = db.rawQuery("SELECT last_insert_rowid()",null)
            cursor.moveToFirst()
            val innerRes = cursor.getInt(0)
            cursor.close()
            innerRes
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
            put("courseName",if(assignment.courseName == null || assignment.courseName.isEmpty()){
                null}
                else
                assignment.courseName
            )
            put("finished",assignment.finished)


        }

        val res= db.update(dbHelper.assignTableName,update,"_id = ${assignment.id}",null)
        return res > 0
    }

    private fun cursorParser(cursor: Cursor): ArrayList<Assignment>{
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
        cursor.close()
        return assList
    }

}