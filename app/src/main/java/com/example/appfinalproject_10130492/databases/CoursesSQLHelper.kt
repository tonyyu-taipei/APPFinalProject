package com.example.appfinalproject_10130492.databases

import android.content.Context

class CoursesSQLHelper(
   context: Context?
) : SQLiteHelper(context) {
    val tableName: String = "Course"
    override val SQL_CREATE_ENTRIES: String = "CREATE TABLE $tableName (courseName TEXT NOT NULL PRIMARY KEY, teacher TEXT NOT NULL) "
    override val SQL_DELETE_ENTRIES: String = "DROP TABLE IF EXSITS $tableName"
}