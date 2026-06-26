package com.research.studenttaskmanager

data class Task(
    val id: String = "",
    val title: String = "",
    var isCompleted: Boolean = false
)