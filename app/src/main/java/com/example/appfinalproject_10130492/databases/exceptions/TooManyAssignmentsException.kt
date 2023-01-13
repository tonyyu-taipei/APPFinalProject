package com.example.appfinalproject_10130492.databases.exceptions

import android.database.sqlite.SQLiteException

class TooManyAssignmentsException: SQLiteException() {
    override fun toString(): String {
       return "Too many assignments are in the database.\r\nDue to notification status code, you can't have more than 1000 assignments in the database."
    }
}