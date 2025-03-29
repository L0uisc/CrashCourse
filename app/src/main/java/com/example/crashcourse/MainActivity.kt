package com.example.crashcourse

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val submitButton = findViewById<Button>(R.id.submitButton)
        submitButton.setOnClickListener {
            val intent = Intent(this, ResultActivity::class.java)
            // Optionally, pass data via intent:
            // intent.putExtra("prompt", "AI Crash Course")
            startActivity(intent)
        }
    }
}
