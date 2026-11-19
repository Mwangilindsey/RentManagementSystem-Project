package com.example.rentmanagementsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var dbHelper: PropertyDatabaseHelper
    private lateinit var tvTotalProperties: TextView
    private lateinit var tvTotalTenants: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        dbHelper = PropertyDatabaseHelper(this)

        tvTotalProperties = findViewById(R.id.tvTotalProperties)
        tvTotalTenants = findViewById(R.id.tvTotalTenants)

        val btnManageProperties =
            findViewById<Button>(R.id.btnManageProperties)

        val btnManageTenants =
            findViewById<Button>(R.id.btnManageTenants)

        val btnViewTenants =
            findViewById<Button>(R.id.btnViewTenants)

        val welcomeText = findViewById<TextView>(R.id.tvWelcome)

        btnManageProperties.setOnClickListener {
            startActivity(
                Intent(this, ViewPropertiesActivity::class.java)
            )
        }

        btnManageTenants.setOnClickListener {
            startActivity(
                Intent(this, ManageTenantsActivity::class.java)
            )
        }

        btnViewTenants.setOnClickListener {
            startActivity(
                Intent(this, ViewTenantsActivity::class.java)
            )
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

        if (::dbHelper.isInitialized) {
            updateDashboardStats()
        }
    }

    private fun updateDashboardStats() {
        val propertyCount = dbHelper.getPropertiesCount()
        val tenantCount = dbHelper.getAllTenants().size

        tvTotalProperties.text = propertyCount.toString()
        tvTotalTenants.text = tenantCount.toString()
    }
}