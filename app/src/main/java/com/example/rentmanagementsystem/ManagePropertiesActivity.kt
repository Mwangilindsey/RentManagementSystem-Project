package com.example.rentmanagementsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ManagePropertiesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_properties)

        val dbHelper = PropertyDatabaseHelper(this)

        val etPropertyName = findViewById<EditText>(R.id.etPropertyName)
        val etPropertyAddress = findViewById<EditText>(R.id.etPropertyAddress)
        val etNumberOfUnits = findViewById<EditText>(R.id.etNumberOfUnits)
        val etMonthlyRent = findViewById<EditText>(R.id.etMonthlyRent)
        val btnSaveProperty = findViewById<Button>(R.id.btnSaveProperty)
        val btnViewProperties = findViewById<Button>(R.id.btnViewProperties)

        btnViewProperties.setOnClickListener {
            val intent = Intent(this, ViewPropertiesActivity::class.java)
            startActivity(intent)
        }

        btnSaveProperty.setOnClickListener {
            val propertyName = etPropertyName.text.toString().trim()
            val propertyAddress = etPropertyAddress.text.toString().trim()
            val numberOfUnits = etNumberOfUnits.text.toString().trim()
            val monthlyRent = etMonthlyRent.text.toString().trim()

            when {
                propertyName.isEmpty() -> {
                    etPropertyName.error = "Property name is required"
                    etPropertyName.requestFocus()
                }

                propertyAddress.isEmpty() -> {
                    etPropertyAddress.error = "Property address is required"
                    etPropertyAddress.requestFocus()
                }

                numberOfUnits.isEmpty() ||
                        numberOfUnits.toIntOrNull() == null ||
                        numberOfUnits.toInt() <= 0 -> {
                    etNumberOfUnits.error = "Enter a valid number of units"
                    etNumberOfUnits.requestFocus()
                }

                monthlyRent.isEmpty() ||
                        monthlyRent.toDoubleOrNull() == null ||
                        monthlyRent.toDouble() <= 0 -> {
                    etMonthlyRent.error = "Enter a valid monthly rent"
                    etMonthlyRent.requestFocus()
                }

                else -> {
                    val saved = dbHelper.insertProperty(
                        propertyName,
                        propertyAddress,
                        numberOfUnits.toInt(),
                        monthlyRent.toDouble()
                    )

                    if (saved) {
                        Toast.makeText(
                            this,
                            "Property saved successfully",
                            Toast.LENGTH_LONG
                        ).show()

                        etPropertyName.text.clear()
                        etPropertyAddress.text.clear()
                        etNumberOfUnits.text.clear()
                        etMonthlyRent.text.clear()

                        // Navigate to view properties list
                        val intent = Intent(this, ViewPropertiesActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this,
                            "Failed to save property",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}
