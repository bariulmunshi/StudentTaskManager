package com.research.studenttaskmanager

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDao {

    @Insert
    fun insertTask(task: TaskEntity)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): List<TaskEntity>

    @Delete
    fun deleteTask(task: TaskEntity)
}