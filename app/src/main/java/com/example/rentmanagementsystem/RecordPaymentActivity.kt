package com.example.rentmanagementsystem

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.util.Locale

class RecordPaymentActivity : AppCompatActivity() {

    private lateinit var dbHelper: PropertyDatabaseHelper
    private lateinit var spinnerTenant: Spinner
    private lateinit var tvSelectedProperty: TextView
    private lateinit var tvMonthlyRent: TextView
    private lateinit var tvTotalPaid: TextView
    private lateinit var tvOutstandingBalance: TextView
    private lateinit var tvPaymentStatus: TextView
    private lateinit var etPaymentAmount: EditText
    private lateinit var etPaymentDate: EditText

    private var tenants = listOf<Tenant>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_payment)

        dbHelper = PropertyDatabaseHelper(this)

        spinnerTenant = findViewById(R.id.spinnerTenant)
        tvSelectedProperty = findViewById(R.id.tvSelectedProperty)
        tvMonthlyRent = findViewById(R.id.tvMonthlyRent)
        tvTotalPaid = findViewById(R.id.tvTotalPaid)
        tvOutstandingBalance = findViewById(R.id.tvOutstandingBalance)
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus)
        etPaymentAmount = findViewById(R.id.etPaymentAmount)
        etPaymentDate = findViewById(R.id.etPaymentDate)

        val btnBack = findViewById<Button>(R.id.btnBack)
        val btnSavePayment = findViewById<Button>(R.id.btnSavePayment)
        val btnPaymentHistory = findViewById<Button>(R.id.btnPaymentHistory)

        btnBack.setOnClickListener {
            finish()
        }

        loadTenants()

        spinnerTenant.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (tenants.isNotEmpty() && position < tenants.size) {
                        val selectedTenant = tenants[position]
                        displayPropertyDetails(selectedTenant)
                        updateRentBalance(selectedTenant)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    tvSelectedProperty.text = "No tenant selected"
                }
            }

        etPaymentDate.setOnClickListener {
            showDatePicker()
        }

        btnSavePayment.setOnClickListener {
            savePayment()
        }

        btnPaymentHistory.setOnClickListener {
            startActivity(Intent(this, PaymentHistoryActivity::class.java))
        }
    }

    private fun loadTenants() {
        tenants = dbHelper.getAllTenants()

        if (tenants.isEmpty()) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                listOf("No tenants available")
            )

            adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )

            spinnerTenant.adapter = adapter
            tvSelectedProperty.text =
                "Add a tenant before recording a payment."

            showEmptyBalance()
            return
        }

        val tenantNames = tenants.map { it.fullName }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            tenantNames
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerTenant.adapter = adapter
    }

    private fun displayPropertyDetails(tenant: Tenant) {
        val property = dbHelper.getAllProperties()
            .find { it.id == tenant.propertyId }

        if (property != null) {
            tvSelectedProperty.text =
                "Property: ${property.name}\n" +
                        "Address: ${property.address}\n" +
                        "Unit: ${tenant.unitNumber}\n" +
                        "Monthly Rent: ${formatMoney(tenant.monthlyRent)}"
        } else {
            tvSelectedProperty.text = "Property details unavailable."
        }
    }

    private fun updateRentBalance(tenant: Tenant) {
        val calendar = Calendar.getInstance()

        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)

        val monthlyRent = tenant.monthlyRent

        val totalPaid = dbHelper.getPaymentsForTenantInMonth(
            tenant.id,
            currentMonth,
            currentYear
        )

        val outstanding = (monthlyRent - totalPaid).coerceAtLeast(0.0)

        tvMonthlyRent.text =
            "Monthly Rent: ${formatMoney(monthlyRent)}"

        tvTotalPaid.text =
            "Total Paid This Month: ${formatMoney(totalPaid)}"

        tvOutstandingBalance.text =
            "Outstanding Balance: ${formatMoney(outstanding)}"

        tvPaymentStatus.text = when {
            totalPaid >= monthlyRent ->
                "Payment Status: Paid"

            totalPaid > 0 ->
                "Payment Status: Partially Paid"

            else ->
                "Payment Status: Unpaid"
        }
    }

    private fun showEmptyBalance() {
        tvMonthlyRent.text = "Monthly Rent: KSh 0.00"
        tvTotalPaid.text = "Total Paid This Month: KSh 0.00"
        tvOutstandingBalance.text = "Outstanding Balance: KSh 0.00"
        tvPaymentStatus.text = "Payment Status: No tenant selected"
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format(
                    Locale.getDefault(),
                    "%02d/%02d/%04d",
                    dayOfMonth,
                    month + 1,
                    year
                )

                etPaymentDate.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    private fun savePayment() {
        if (tenants.isEmpty()) {
            Toast.makeText(
                this,
                "Please add a tenant first.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val amountText = etPaymentAmount.text.toString().trim()
        val paymentDate = etPaymentDate.text.toString().trim()

        if (amountText.isEmpty()) {
            etPaymentAmount.error = "Enter payment amount"
            etPaymentAmount.requestFocus()
            return
        }

        val amount = amountText.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            etPaymentAmount.error = "Enter a valid amount"
            etPaymentAmount.requestFocus()
            return
        }

        if (paymentDate.isEmpty()) {
            etPaymentDate.error = "Select payment date"
            etPaymentDate.requestFocus()
            return
        }

        val selectedPosition = spinnerTenant.selectedItemPosition

        if (selectedPosition !in tenants.indices) {
            Toast.makeText(
                this,
                "Please select a valid tenant.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val selectedTenant = tenants[selectedPosition]

        val saved = dbHelper.insertPayment(
            selectedTenant.id,
            amount,
            paymentDate
        )

        if (saved) {
            Toast.makeText(
                this,
                "Payment recorded successfully!",
                Toast.LENGTH_SHORT
            ).show()

            etPaymentAmount.text.clear()
            etPaymentDate.text.clear()

            updateRentBalance(selectedTenant)
        } else {
            Toast.makeText(
                this,
                "Failed to record payment.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun formatMoney(amount: Double): String {
        return String.format(
            Locale.getDefault(),
            "KSh %,.2f",
            amount
        )
    }
}
