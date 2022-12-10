package com.example.appfinalproject_10130492.databases

import android.content.Context

class AssignmentsSQLHelper(
   context: Context?
) : SQLiteHelper(context) {
    val tableName: String = "Assignments"
    override val SQL_CREATE_ENTRIES: String = "CREATE TABLE $tableName ("+
            "_id INTEGER PRIMARY KEY,"+
            " note TEXT, title TEXT NOT NULL,"+
            " assignedDate DATETIME NOT NULL,"+
            " dueDate DATETIME NOT NULL,"+
            "courseName TEXT ,"+"FOREIGN KEY(courseName) REFERENCES Course(courseName) )"
    override val SQL_DELETE_ENTRIES: String = "DROP TABLE IF EXSITS $tableName"
}