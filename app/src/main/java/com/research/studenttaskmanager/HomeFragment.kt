package com.research.studenttaskmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.firebase.database.*
import kotlin.concurrent.thread

class HomeFragment : Fragment() {

    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()
    private lateinit var database: AppDatabase

    private val firebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val etTask = view.findViewById<EditText>(R.id.etTask)
        val btnAddTask = view.findViewById<Button>(R.id.btnAddTask)
        val btnAll = view.findViewById<Button>(R.id.btnAll)
        val btnCompleted = view.findViewById<Button>(R.id.btnCompleted)
        val btnPending = view.findViewById<Button>(R.id.btnPending)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "task_database"
        ).build()

        taskAdapter = TaskAdapter(

            taskList,

            // Delete Task
            { deletedTask ->
                thread {
                    database.taskDao().deleteTask(
                        TaskEntity(title = deletedTask.title)
                    )

                    firebaseDatabase.reference
                        .child("tasks")
                        .child(deletedTask.id)
                        .removeValue()
                }
            },

            // Update Task
            { clickedTask ->

                val editText = EditText(requireContext())
                editText.setText(clickedTask.title)

                AlertDialog.Builder(requireContext())
                    .setTitle("Update Task")
                    .setView(editText)
                    .setPositiveButton("Update") { _, _ ->

                        val updatedTitle = editText.text.toString().trim()

                        if (updatedTitle.isNotEmpty()) {

                            firebaseDatabase.reference
                                .child("tasks")
                                .child(clickedTask.id)
                                .setValue(
                                    Task(
                                        clickedTask.id,
                                        updatedTitle,
                                        clickedTask.isCompleted
                                    )
                                )

                            thread {
                                database.taskDao().deleteTask(
                                    TaskEntity(title = clickedTask.title)
                                )

                                database.taskDao().insertTask(
                                    TaskEntity(title = updatedTitle)
                                )
                            }
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            },

            // Checkbox Toggle
            { checkedTask, isChecked ->

                firebaseDatabase.reference
                    .child("tasks")
                    .child(checkedTask.id)
                    .setValue(
                        Task(
                            checkedTask.id,
                            checkedTask.title,
                            isChecked
                        )
                    )
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = taskAdapter

        // Load all tasks initially
        loadFirebaseTasks()

        // Filter buttons
        btnAll.setOnClickListener {
            filterTasks("all")
        }

        btnCompleted.setOnClickListener {
            filterTasks("completed")
        }

        btnPending.setOnClickListener {
            filterTasks("pending")
        }

        // Add Task
        btnAddTask.setOnClickListener {

            val newTask = etTask.text.toString().trim()

            if (newTask.isNotEmpty()) {

                val task = TaskEntity(title = newTask)

                thread {
                    database.taskDao().insertTask(task)

                    val taskId = firebaseDatabase.reference
                        .child("tasks")
                        .push()
                        .key!!

                    val firebaseTask = Task(
                        taskId,
                        newTask,
                        false
                    )

                    firebaseDatabase.reference
                        .child("tasks")
                        .child(taskId)
                        .setValue(firebaseTask)

                    activity?.runOnUiThread {
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

    private fun loadFirebaseTasks() {

        firebaseDatabase.reference.child("tasks")
            .get()
            .addOnSuccessListener { snapshot ->

                taskList.clear()

                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java)

                    if (task != null) {
                        taskList.add(task)
                    }
                }

                taskAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Failed to load tasks",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
    private fun filterTasks(type: String) {

        firebaseDatabase.reference.child("tasks")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    taskList.clear()

                    for (taskSnapshot in snapshot.children) {
                        val task = taskSnapshot.getValue(Task::class.java)

                        if (task != null) {
                            when (type) {
                                "all" -> taskList.add(task)

                                "completed" -> {
                                    if (task.isCompleted) {
                                        taskList.add(task)
                                    }
                                }

                                "pending" -> {
                                    if (!task.isCompleted) {
                                        taskList.add(task)
                                    }
                                }
                            }
                        }
                    }

                    taskAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        "Filter failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}