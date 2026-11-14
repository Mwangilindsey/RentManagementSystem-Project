package com.example.rentmanagementsystem

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ManagePropertiesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_manage_properties)

        // This creates an instance of the database helper
        val dbHelper = PropertyDatabaseHelper(this)

        val etPropertyName =
            findViewById<EditText>(R.id.etPropertyName)

        val etPropertyAddress =
            findViewById<EditText>(R.id.etPropertyAddress)

        val etNumberOfUnits =
            findViewById<EditText>(R.id.etNumberOfUnits)

        val etMonthlyRent =
            findViewById<EditText>(R.id.etMonthlyRent)

        val btnSaveProperty =
            findViewById<Button>(R.id.btnSaveProperty)


        btnSaveProperty.setOnClickListener {

            // Gets the property details entered by the user
            val propertyName =
                etPropertyName.text.toString().trim()

            val propertyAddress =
                etPropertyAddress.text.toString().trim()

            val numberOfUnits =
                etNumberOfUnits.text.toString().trim()

            val monthlyRent =
                etMonthlyRent.text.toString().trim()

            // Validates the entered property details
            when {

                // Checks whether the property name is empty
                propertyName.isEmpty() -> {
                    etPropertyName.error =
                        "Property name is required"
                    etPropertyName.requestFocus()
                }

                // Checks whether the property address is empty
                propertyAddress.isEmpty() -> {
                    etPropertyAddress.error =
                        "Property address is required"
                    etPropertyAddress.requestFocus()
                }

                // Checks whether the number of units is a valid positive whole number
                numberOfUnits.isEmpty() ||
                        numberOfUnits.toIntOrNull() == null ||
                        numberOfUnits.toInt() <= 0 -> {
                    etNumberOfUnits.error =
                        "Enter a valid number of units"
                    etNumberOfUnits.requestFocus()
                }

                // This checks whether the monthly rent is a valid positive number
                monthlyRent.isEmpty() ||
                        monthlyRent.toDoubleOrNull() == null ||
                        monthlyRent.toDouble() <= 0 -> {
                    etMonthlyRent.error =
                        "Enter a valid monthly rent"
                    etMonthlyRent.requestFocus()
                }

                // Saves the property if all validations pass
                else -> {
                    val saved = dbHelper.insertProperty(
                        propertyName,
                        propertyAddress,
                        numberOfUnits.toInt(),
                        monthlyRent.toDouble()
                    )

                    // Displays a message depending on whether the property was saved
                    if (saved) {
                        Toast.makeText(
                            this,
                            "Property saved successfully",
                            Toast.LENGTH_LONG
                        ).show()

                        // This clears all input fields after successful registration
                        etPropertyName.text.clear()
                        etPropertyAddress.text.clear()
                        etNumberOfUnits.text.clear()
                        etMonthlyRent.text.clear()

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