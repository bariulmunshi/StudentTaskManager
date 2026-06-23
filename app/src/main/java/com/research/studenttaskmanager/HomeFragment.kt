package com.research.studenttaskmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlin.concurrent.thread

class HomeFragment : Fragment() {

    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val etTask = view.findViewById<EditText>(R.id.etTask)
        val btnAddTask = view.findViewById<Button>(R.id.btnAddTask)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "task_database"
        ).build()

        taskAdapter = TaskAdapter(taskList) { deletedTask ->

            thread {
                database.taskDao().deleteTask(
                    TaskEntity(title = deletedTask.title)
                )
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = taskAdapter

        loadTasks()

        btnAddTask.setOnClickListener {

            val newTask = etTask.text.toString().trim()

            if (newTask.isNotEmpty()) {

                val task = TaskEntity(title = newTask)

                thread {
                    database.taskDao().insertTask(task)

                    activity?.runOnUiThread {
                        taskList.add(Task(newTask))
                        taskAdapter.notifyDataSetChanged()
                        etTask.text.clear()
                    }
                }

            } else {
                Toast.makeText(
                    requireContext(),
                    "Please enter a task",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return view
    }

    private fun loadTasks() {
        thread {
            val savedTasks = database.taskDao().getAllTasks()

            activity?.runOnUiThread {
                taskList.clear()

                for (task in savedTasks) {
                    taskList.add(Task(task.title))
                }

                taskAdapter.notifyDataSetChanged()
            }
        }
    }
}