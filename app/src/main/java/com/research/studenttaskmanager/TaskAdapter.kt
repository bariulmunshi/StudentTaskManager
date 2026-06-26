package com.research.studenttaskmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val taskList: MutableList<Task>,
    private val onTaskDeleted: (Task) -> Unit,
    private val onTaskClicked: (Task) -> Unit,
    private val onTaskChecked: (Task, Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTask: TextView = itemView.findViewById(R.id.tvTask)
        val checkTask: CheckBox = itemView.findViewById(R.id.checkTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)

        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        val currentTask = taskList[position]

        // Task title
        holder.tvTask.text = currentTask.title

        // Remove old listener first (important)
        holder.checkTask.setOnCheckedChangeListener(null)

        // Set current checkbox state
        holder.checkTask.isChecked = currentTask.isCompleted

        // New checkbox listener
        holder.checkTask.setOnCheckedChangeListener { _, isChecked ->
            if (currentTask.isCompleted != isChecked) {

                // Update local state immediately
                currentTask.isCompleted = isChecked

                // Send update to HomeFragment
                onTaskChecked(currentTask, isChecked)
            }
        }

        // Single click = Update task
        holder.itemView.setOnClickListener {
            onTaskClicked(currentTask)
        }

        // Long click = Delete task
        holder.itemView.setOnLongClickListener {
            onTaskDeleted(currentTask)
            true
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }
}