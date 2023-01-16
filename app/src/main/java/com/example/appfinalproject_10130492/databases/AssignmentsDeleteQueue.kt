package com.example.appfinalproject_10130492.databases

import android.content.Context
import com.example.appfinalproject_10130492.AlarmService
import com.example.appfinalproject_10130492.data.Assignment

/**
 * A class to queue the Assignments item that's going to get deleted.
 * @param context Fragment's context. For AssignentsDB
 */
class AssignmentsDeleteQueue(val context: Context) {
    private var count = 0
    private var toBeDeleted:ArrayList<Assignment> = ArrayList()

    /**
     * Queue an item inside the queue inorder to implement soft delete in Assignments recyclerAdapter.
     * @param id The Assignment ID.
     */
    fun enqueue(id: Assignment){
        count++
        toBeDeleted.add(id)
    }
    fun dequeue(): Assignment{
        count--
        return toBeDeleted.removeFirst()
    }

    /**
     * Fully delete the assignment from the database
     */
    fun hardDelete(){
        val tmpCount = count
        val assignmentsDB = AssignmentsDB(context)

        for(i in 0 until tmpCount){
            val assignment = dequeue()
            var id = assignment.id
            if(id != null) {
                id = assignment.id
                val alarmService = AlarmService(context)
                alarmService.cancelSpecificAlarm(assignment)
                id?.let { assignmentsDB.deleteOne(it) }
            }
        }
    }
    fun getCount():Int{
        return count
    }
/*
    /**
     * Undo all of the Assignments inside the queue.
     */

    fun undoAll(): ArrayList<Assignment>{
        val toReturn = toBeDeleted.clone() as ArrayList<Assignment>
        toBeDeleted.clear()
        count = 0
        return toReturn
    }*/
}