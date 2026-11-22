package com.example.rentmanagementsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private lateinit var dbHelper: PropertyDatabaseHelper
    private lateinit var tvTotalProperties: TextView
    private lateinit var tvTotalTenants: TextView
    private lateinit var tvRentCollected: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        dbHelper = PropertyDatabaseHelper(this)

        tvTotalProperties = findViewById(R.id.tvTotalProperties)
        tvTotalTenants = findViewById(R.id.tvTotalTenants)
        tvRentCollected = findViewById(R.id.tvRentCollected)

        val btnManageProperties =
            findViewById<Button>(R.id.btnManageProperties)

        val btnViewProperties =
            findViewById<Button>(R.id.btnViewProperties)

        val btnManageTenants =
            findViewById<Button>(R.id.btnManageTenants)

        val btnRecordPayment =
            findViewById<Button>(R.id.btnRecordPayment)

        val welcomeText = findViewById<TextView>(R.id.tvWelcome)

        // Opens the Manage Properties screen
        btnManageProperties.setOnClickListener {
            startActivity(
                Intent(this, ManagePropertiesActivity::class.java)
            )
        }

        // Opens the View Properties screen
        btnViewProperties.setOnClickListener {
            startActivity(
                Intent(this, ViewPropertiesActivity::class.java)
            )
        }

        // Opens the Manage Tenants screen
        btnManageTenants.setOnClickListener {
            startActivity(
                Intent(this, ManageTenantsActivity::class.java)
            )
        }

        // Opens the Record Rent Payment screen
        btnRecordPayment.setOnClickListener {
            startActivity(
                Intent(this, RecordPaymentActivity::class.java)
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
        val totalRentCollected = dbHelper.getTotalPayments()

        tvTotalProperties.text = propertyCount.toString()
        tvTotalTenants.text = tenantCount.toString()

        tvRentCollected.text = String.format(
            Locale.getDefault(),
            "KSh %,.2f",
            totalRentCollected
        )
    }
}
