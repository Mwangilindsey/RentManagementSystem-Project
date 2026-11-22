package com.example.rentmanagementsystem

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ManageTenantsActivity : AppCompatActivity() {

    private lateinit var dbHelper: PropertyDatabaseHelper
    private lateinit var properties: List<Property>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_tenants)

        dbHelper = PropertyDatabaseHelper(this)

        val btnBack = findViewById<Button>(R.id.btnBack)
        val etTenantName = findViewById<EditText>(R.id.etTenantName)
        val etTenantPhone = findViewById<EditText>(R.id.etTenantPhone)
        val etTenantEmail = findViewById<EditText>(R.id.etTenantEmail)
        val spTenantProperty = findViewById<Spinner>(R.id.spTenantProperty)
        val etTenantUnit = findViewById<EditText>(R.id.etTenantUnit)
        val etTenantRent = findViewById<EditText>(R.id.etTenantRent)
        val btnSaveTenant = findViewById<Button>(R.id.btnSaveTenant)
        val btnViewTenants = findViewById<Button>(R.id.btnViewTenants)


        btnBack.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // View All Tenants button
        btnViewTenants.setOnClickListener {
            val intent = Intent(this, ViewTenantsActivity::class.java)
            startActivity(intent)
        }


        properties = dbHelper.getAllProperties()


        val propertyNames = properties.map { property ->
            "${property.name} - ${property.address}"
        }

        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            propertyNames
        ) {

            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getView(
                    position,
                    convertView,
                    parent
                ) as TextView

                view.setTextColor(Color.parseColor("#102B46"))
                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(
                    position,
                    convertView,
                    parent
                ) as TextView

                view.setTextColor(Color.parseColor("#102B46"))
                view.setBackgroundColor(Color.WHITE)
                return view
            }
        }

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spTenantProperty.adapter = adapter


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

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
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

                unitNumber.isEmpty() -> {
                    etTenantUnit.error = "Unit number is required"
                    etTenantUnit.requestFocus()
                }

                rentText.toDoubleOrNull() == null ||
                        rentText.toDouble() <= 0 -> {
                    etTenantRent.error = "Enter a valid monthly rent"
                    etTenantRent.requestFocus()
                }

                else -> {

                    // Get the selected property
                    val selectedPosition =
                        spTenantProperty.selectedItemPosition

                    if (selectedPosition !in properties.indices) {
                        Toast.makeText(
                            this,
                            "Please select a property",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val selectedProperty = properties[selectedPosition]

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
