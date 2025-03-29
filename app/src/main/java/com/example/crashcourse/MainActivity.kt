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
            val topic = promptEditText.text.toString().trim().lowercase(Locale.ROOT)

            if (topic.isEmpty()) {
                Toast.makeText(this, "Please enter a topic", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            spinner.visibility = View.VISIBLE

            if (topic.lowercase() == "pink floyd") {
                spinner.visibility = View.GONE
                val hardcodedText = """
Pink Floyd is one of the most influential rock bands of all time, known for their progressive and psychedelic sound, philosophical lyrics, and groundbreaking album concepts.

Origins & Members:
- Formed in 1965 in London.
- Classic lineup: Roger Waters (bass, vocals), David Gilmour (guitar, vocals), Richard Wright (keyboards), Nick Mason (drums), and founder Syd Barrett (guitar, vocals).

Signature Albums:
- The Dark Side of the Moon (1973): A masterpiece with themes of time, mental illness, and capitalism. Stayed on the charts for over 14 years.
- Wish You Were Here (1975): A tribute to Syd Barrett, featuring "Shine On You Crazy Diamond."
- Animals (1977): A politically charged album inspired by George Orwell’s "Animal Farm."
- The Wall (1979): A rock opera exploring isolation and war, featuring the hit "Another Brick in the Wall, Part II.
""".trimIndent()


                val intent = Intent(this, ResultActivity::class.java).apply {
                    putExtra("topic", topic)
                    putExtra("generatedText", hardcodedText)
                }
                startActivity(intent)
            } else {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == VOICE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val promptEditText = findViewById<EditText>(R.id.promptEditText)
            promptEditText.setText(result?.get(0) ?: "")
        }
    }
}
