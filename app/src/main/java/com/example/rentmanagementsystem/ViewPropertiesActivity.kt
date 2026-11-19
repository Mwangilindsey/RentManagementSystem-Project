package com.example.rentmanagementsystem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ViewPropertiesActivity : AppCompatActivity() {

    private lateinit var dbHelper: PropertyDatabaseHelper
    private lateinit var rvProperties: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var tvSubtitle: TextView
    private lateinit var adapter: PropertyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_properties)

        dbHelper = PropertyDatabaseHelper(this)

        rvProperties = findViewById(R.id.rvProperties)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        tvSubtitle = findViewById(R.id.tvSubtitle)

        rvProperties.layoutManager = LinearLayoutManager(this)

        adapter = PropertyAdapter(
            propertyList = emptyList(),
            onEditClick = { property -> showEditDialog(property) },
            onDeleteClick = { property -> showDeleteConfirmationDialog(property) }
        )
        rvProperties.adapter = adapter

        loadProperties()
    }

    override fun onResume() {
        super.onResume()
        loadProperties()
    }

    // Retrieves properties from SQLite and updates UI list or empty state
    private fun loadProperties() {
        val properties = dbHelper.getAllProperties()

        if (properties.isEmpty()) {
            rvProperties.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
            tvSubtitle.text = "No properties registered yet."
        } else {
            rvProperties.visibility = View.VISIBLE
            layoutEmptyState.visibility = View.GONE
            val propertyCount = properties.size
            tvSubtitle.text = if (propertyCount == 1) {
                "Showing 1 registered property."
            } else {
                "Showing $propertyCount registered properties."
            }
            adapter.updateData(properties)
        }
    }

    // Displays a dialog to edit an existing property record (Update)
    private fun showEditDialog(property: Property) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_property, null)

        val etName = dialogView.findViewById<EditText>(R.id.etEditPropertyName)
        val etAddress = dialogView.findViewById<EditText>(R.id.etEditPropertyAddress)
        val etUnits = dialogView.findViewById<EditText>(R.id.etEditNumberOfUnits)
        val etRent = dialogView.findViewById<EditText>(R.id.etEditMonthlyRent)

        // Pre-fill inputs with current property values
        etName.setText(property.name)
        etAddress.setText(property.address)
        etUnits.setText(property.numberOfUnits.toString())
        etRent.setText(property.monthlyRent.toString())

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Update", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val updateBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            updateBtn.setOnClickListener {
                val name = etName.text.toString().trim()
                val address = etAddress.text.toString().trim()
                val unitsStr = etUnits.text.toString().trim()
                val rentStr = etRent.text.toString().trim()

                when {
                    name.isEmpty() -> {
                        etName.error = "Property name is required"
                        etName.requestFocus()
                    }

                    address.isEmpty() -> {
                        etAddress.error = "Property address is required"
                        etAddress.requestFocus()
                    }

                    unitsStr.isEmpty() || unitsStr.toIntOrNull() == null || unitsStr.toInt() <= 0 -> {
                        etUnits.error = "Enter a valid number of units"
                        etUnits.requestFocus()
                    }

                    rentStr.isEmpty() || rentStr.toDoubleOrNull() == null || rentStr.toDouble() <= 0 -> {
                        etRent.error = "Enter a valid monthly rent"
                        etRent.requestFocus()
                    }

                    else -> {
                        val updated = dbHelper.updateProperty(
                            id = property.id,
                            name = name,
                            address = address,
                            numberOfUnits = unitsStr.toInt(),
                            monthlyRent = rentStr.toDouble()
                        )

                        if (updated) {
                            Toast.makeText(this, "Property updated successfully", Toast.LENGTH_SHORT).show()
                            loadProperties()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(this, "Failed to update property", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        dialog.show()
    }

    // Displays a confirmation dialog before deleting a property record (Delete)
    private fun showDeleteConfirmationDialog(property: Property) {
        AlertDialog.Builder(this)
            .setTitle("Delete Property")
            .setMessage("Are you sure you want to delete '${property.name}'? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                val deleted = dbHelper.deleteProperty(property.id)
                if (deleted) {
                    Toast.makeText(this, "Property deleted successfully", Toast.LENGTH_SHORT).show()
                    loadProperties()
                } else {
                    Toast.makeText(this, "Failed to delete property", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
