package com.example.crashcourse

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val submitButton = findViewById<Button>(R.id.submitButton)
        val promptEditText = findViewById<EditText>(R.id.promptEditText)
        val spinner = findViewById<ProgressBar>(R.id.loadingSpinner)

        submitButton.setOnClickListener {
            val topic = promptEditText.text.toString().trim()

            if (topic.isEmpty()) {
                Toast.makeText(this, "Please enter a topic", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            spinner.visibility = View.VISIBLE

            val url = "https://38d4-165-255-242-36.ngrok-free.app/api/generate"
            val requestQueue = Volley.newRequestQueue(this)

            val requestBody = JSONObject().apply {
                put("topic", topic)
            }

            val jsonRequest = JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                { response ->
                    spinner.visibility = View.GONE
                    val success = response.optBoolean("success", false)
                    if (success) {
                        val generatedText = response.optString("generatedText", "")
                        val intent = Intent(this, ResultActivity::class.java).apply {
                            putExtra("topic", topic)
                            putExtra("generatedText", generatedText)
                        }
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Failed to generate content", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    spinner.visibility = View.GONE
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
            )

            requestQueue.add(jsonRequest)
        }
    }
}
