package com.example.appfinalproject_10130492.databases.exceptions

import android.database.sqlite.SQLiteException

/**
 * *MultipleMatchesException*
 * @param objList The ArrayList<Any> to log in the exception. This can accept any Object as long as there's toString() method available.
 */
class MultipleMatchesException(private val objList: ArrayList<Any>): SQLiteException() {
    override fun toString(): String{
        return "Multiple value matches your where clause in the query.\r\nDatabase returned: \r\n${objList.toString()}"
    }
}