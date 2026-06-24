package com.research.studenttaskmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class PostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post, container, false)

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val btnSend = view.findViewById<Button>(R.id.btnSend)

        btnSend.setOnClickListener {
            val title = etTitle.text.toString().trim()

            if (title.isNotEmpty()) {
                sendPostRequest(title)
            }
        }

        return view
    }

    private fun sendPostRequest(title: String) {
        val url = "https://jsonplaceholder.typicode.com/posts"

        val jsonBody = JSONObject()
        jsonBody.put("title", title)

        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonBody,
            {
                Toast.makeText(
                    requireContext(),
                    "Task sent successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            },
            {
                Toast.makeText(
                    requireContext(),
                    "Failed to send task",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        Volley.newRequestQueue(requireContext()).add(request)
    }
}