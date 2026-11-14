package com.example.rentmanagementsystem

import android.content.Intent
import android.widget.Button
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        val btnManageProperties =
            findViewById<Button>(R.id.btnManageProperties)

        // Opens the manage properties screen when the button is clicked
        btnManageProperties.setOnClickListener {
            val intent = Intent(this, ManagePropertiesActivity::class.java)
            startActivity(intent)
        }

        val email = intent.getStringExtra("email")

        val welcomeText = findViewById<android.widget.TextView>(
            R.id.tvWelcome
        )

        // This displays a welcome message using the user's email
        welcomeText.text = if (email.isNullOrBlank()) {
            "Welcome back!"
        } else {
            "Welcome, $email"
        }
    }
}