package com.research.studenttaskmanager

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val userName = intent.getStringExtra("USER_NAME")

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)

        tvWelcome.text = "Welcome $userName"
    }
}