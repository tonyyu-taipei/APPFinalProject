package com.example.appfinalproject_10130492.data

data class AssignmentsWithStatus(
    val finished: ArrayList<Assignment>,
    val late: ArrayList<Assignment>,
    val queued: ArrayList<Assignment>
)
