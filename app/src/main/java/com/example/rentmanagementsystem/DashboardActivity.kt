package com.example.rentmanagementsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var dbHelper: PropertyDatabaseHelper
    private lateinit var tvTotalProperties: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        dbHelper = PropertyDatabaseHelper(this)

        tvTotalProperties = findViewById(R.id.tvTotalProperties)

        val btnManageProperties = findViewById<Button>(R.id.btnManageProperties)
        val welcomeText = findViewById<TextView>(R.id.tvWelcome)

        btnManageProperties.setOnClickListener {
            val intent = Intent(this, ViewPropertiesActivity::class.java)
            startActivity(intent)
        }

        val email = intent.getStringExtra("email")
        welcomeText.text = if (email.isNullOrBlank()) {
            "Welcome back!"
        } else {
            "Welcome, $email"
        }

        updateDashboardStats()
    }

    override fun onResume() {
        super.onResume()
        updateDashboardStats()
    }

    private fun updateDashboardStats() {
        val propertyCount = dbHelper.getPropertiesCount()
        tvTotalProperties.text = propertyCount.toString()
    }
}
