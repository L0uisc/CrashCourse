package com.example.crashcourse

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class ResultActivity : AppCompatActivity(), OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var backButton: Button
    private lateinit var readButton: Button
    private lateinit var subjectHeader: TextView
    private lateinit var factsList: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Initialize views
        backButton = findViewById(R.id.backButton)
        readButton = findViewById(R.id.readButton)
        subjectHeader = findViewById(R.id.subjectHeader)
        factsList = findViewById(R.id.factsList)

        // Get topic and generated text from intent
        val topic = intent.getStringExtra("topic") ?: "Unknown Topic"
        val generatedText = intent.getStringExtra("generatedText") ?: "No data available."

        // Set text to views
        subjectHeader.text = "Subject: $topic"
        factsList.text = generatedText

        // Initialize TextToSpeech
        tts = TextToSpeech(this, this)

        // Set up the read button to trigger text-to-speech
        readButton.setOnClickListener {
            val textToRead = factsList.text.toString()
            readTextAloud(textToRead)
        }

        // Set up back button to finish the activity
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun readTextAloud(text: String) {
        if (text.isNotEmpty()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val langResult = tts.setLanguage(Locale.US)
            if (langResult == TextToSpeech.LANG_MISSING_DATA ||
                langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Handle error if language is not supported
            }
        } else {
            // Handle initialization failure
        }
    }

    override fun onDestroy() {
        // Clean up the TextToSpeech object
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
