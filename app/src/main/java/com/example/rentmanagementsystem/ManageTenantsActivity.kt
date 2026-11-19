package com.example.rentmanagementsystem

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ManageTenantsActivity : AppCompatActivity() {

    private lateinit var dbHelper: PropertyDatabaseHelper
    private lateinit var properties: List<Property>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_tenants)

        dbHelper = PropertyDatabaseHelper(this)

        val etTenantName = findViewById<EditText>(R.id.etTenantName)
        val etTenantPhone = findViewById<EditText>(R.id.etTenantPhone)
        val etTenantEmail = findViewById<EditText>(R.id.etTenantEmail)
        val spTenantProperty = findViewById<Spinner>(R.id.spTenantProperty)
        val etTenantUnit = findViewById<EditText>(R.id.etTenantUnit)
        val etTenantRent = findViewById<EditText>(R.id.etTenantRent)
        val btnSaveTenant = findViewById<Button>(R.id.btnSaveTenant)

        // Load existing properties from SQLite
        properties = dbHelper.getAllProperties()

        val propertyNames = mutableListOf("Select a property")

        properties.forEach { property ->
            propertyNames.add("${property.name} - ${property.address}")
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            propertyNames
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spTenantProperty.adapter = adapter

        // Save Tenant button
        btnSaveTenant.setOnClickListener {

            val fullName = etTenantName.text.toString().trim()
            val phone = etTenantPhone.text.toString().trim()
            val email = etTenantEmail.text.toString().trim()
            val unitNumber = etTenantUnit.text.toString().trim()
            val rentText = etTenantRent.text.toString().trim()

            when {
                fullName.isEmpty() -> {
                    etTenantName.error = "Full name is required"
                    etTenantName.requestFocus()
                }

                phone.isEmpty() -> {
                    etTenantPhone.error = "Phone number is required"
                    etTenantPhone.requestFocus()
                }

                email.isEmpty() -> {
                    etTenantEmail.error = "Email is required"
                    etTenantEmail.requestFocus()
                }

                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    etTenantEmail.error = "Enter a valid email address"
                    etTenantEmail.requestFocus()
                }

                properties.isEmpty() -> {
                    Toast.makeText(
                        this,
                        "Please register a property first",
                        Toast.LENGTH_LONG
                    ).show()
                }

                spTenantProperty.selectedItemPosition == 0 -> {
                    Toast.makeText(
                        this,
                        "Please select a property",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                unitNumber.isEmpty() -> {
                    etTenantUnit.error = "Unit number is required"
                    etTenantUnit.requestFocus()
                }

                rentText.isEmpty() ||
                        rentText.toDoubleOrNull() == null ||
                        rentText.toDouble() <= 0 -> {
                    etTenantRent.error = "Enter a valid monthly rent"
                    etTenantRent.requestFocus()
                }

                else -> {
                    // Get the selected property object
                    val selectedProperty = properties[
                        spTenantProperty.selectedItemPosition - 1
                    ]

                    val tenant = Tenant(
                        fullName = fullName,
                        phone = phone,
                        email = email,
                        propertyId = selectedProperty.id,
                        unitNumber = unitNumber,
                        monthlyRent = rentText.toDouble()
                    )

                    val saved = dbHelper.insertTenant(tenant)

                    if (saved) {
                        Toast.makeText(
                            this,
                            "Tenant saved successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Clear the form
                        etTenantName.text.clear()
                        etTenantPhone.text.clear()
                        etTenantEmail.text.clear()
                        etTenantUnit.text.clear()
                        etTenantRent.text.clear()
                        spTenantProperty.setSelection(0)

                    } else {
                        Toast.makeText(
                            this,
                            "Failed to save tenant",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}