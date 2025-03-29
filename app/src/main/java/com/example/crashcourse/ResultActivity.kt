package com.example.crashcourse

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val backButton = findViewById<Button>(R.id.backButton)
        val subjectHeader = findViewById<TextView>(R.id.subjectHeader)
        val factsList = findViewById<TextView>(R.id.factsList)

        val topic = intent.getStringExtra("topic") ?: "Unknown Topic"
        val generatedText = intent.getStringExtra("generatedText") ?: "No data available."

        subjectHeader.text = "Subject: $topic"
        factsList.text = generatedText

        backButton.setOnClickListener {
            finish()
        }
    }
}
