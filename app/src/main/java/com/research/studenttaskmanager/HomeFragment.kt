package com.research.studenttaskmanager

import android.content.Context
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

class HomeFragment : Fragment() {

    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val etTask = view.findViewById<EditText>(R.id.etTask)
        val btnAddTask = view.findViewById<Button>(R.id.btnAddTask)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        loadTasks()

        taskAdapter = TaskAdapter(taskList) {
            saveTasks()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = taskAdapter

        btnAddTask.setOnClickListener {

            val newTask = etTask.text.toString().trim()

            if (newTask.isNotEmpty()) {
                taskList.add(Task(newTask))
                taskAdapter.notifyDataSetChanged()
                saveTasks()
                etTask.text.clear()
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

    private fun saveTasks() {
        val sharedPreferences =
            requireActivity().getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        val taskTitles = taskList.joinToString(",") { it.title }

        editor.putString("TASK_LIST", taskTitles)
        editor.apply()
    }

    private fun loadTasks() {
        val sharedPreferences =
            requireActivity().getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)

        val savedTasks = sharedPreferences.getString("TASK_LIST", "")

        if (!savedTasks.isNullOrEmpty()) {
            val taskArray = savedTasks.split(",")

            for (task in taskArray) {
                taskList.add(Task(task))
            }
        }
    }
}