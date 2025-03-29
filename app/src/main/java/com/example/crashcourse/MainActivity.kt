package com.example.crashcourse

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {

    private val VOICE_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val submitButton = findViewById<Button>(R.id.submitButton)
        val promptEditText = findViewById<EditText>(R.id.promptEditText)
        val micButton = findViewById<ImageButton>(R.id.micButton)
        val spinner = findViewById<ProgressBar>(R.id.loadingSpinner)

        micButton.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your topic")
            }

            try {
                startActivityForResult(intent, VOICE_REQUEST_CODE)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Your device doesn't support voice input", Toast.LENGTH_SHORT).show()
            }
        }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == VOICE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val promptEditText = findViewById<EditText>(R.id.promptEditText)
            promptEditText.setText(result?.get(0) ?: "")
        }
    }
}
