package com.research.studenttaskmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

class ApiFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_api, container, false)

        val tvApiData = view.findViewById<TextView>(R.id.tvApiData)

        val url = "https://jsonplaceholder.typicode.com/todos"

        val requestQueue = Volley.newRequestQueue(requireContext())

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                val result = StringBuilder()

                for (i in 0 until 10) {
                    val task = response.getJSONObject(i)
                    result.append(task.getString("title")).append("\n\n")
                }

                tvApiData.text = result.toString()
            },
            {
                tvApiData.text = "Error fetching data"
            }
        )

        requestQueue.add(jsonArrayRequest)

        return view
    }
}