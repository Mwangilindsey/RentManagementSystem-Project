package com.example.rentmanagementsystem

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Locale
import java.util.concurrent.TimeUnit

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

        val btnReports =
            findViewById<Button>(R.id.btnReports)

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

        // Opens the Reports screen
        btnReports.setOnClickListener {
            startActivity(
                Intent(this, ReportsActivity::class.java)
            )
        }

        val email = intent.getStringExtra("email")

        welcomeText.text = if (email.isNullOrBlank()) {
            "Welcome back!"
        } else {
            "Welcome, $email"
        }

        requestNotificationPermission()
        scheduleRentReminders()

        // TEMPORARY TEST: uncomment to fire the reminder check immediately,
        // then comment it out again when you are done testing.
        // WorkManager.getInstance(this).enqueue(
        //     androidx.work.OneTimeWorkRequestBuilder<RentReminderWorker>().build()
        // )

        updateDashboardStats()
    }

    override fun onResume() {
        super.onResume()

        if (::dbHelper.isInitialized) {
            updateDashboardStats()
        }
    }

    // Asks for notification permission on Android 13+
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }
    }

    // Schedules the daily check for due and overdue rent
    private fun scheduleRentReminders() {
        val request = PeriodicWorkRequestBuilder<RentReminderWorker>(
            1, TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "rent_reminders",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
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