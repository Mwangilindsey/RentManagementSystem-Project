package com.example.rentmanagementsystem

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.util.Patterns
import androidx.appcompat.app.AlertDialog

class ViewTenantsActivity : AppCompatActivity() {

    private lateinit var dbHelper: PropertyDatabaseHelper
    private lateinit var rvTenants: RecyclerView
    private lateinit var tvTenantCount: TextView
    private lateinit var tvEmptyTenants: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_tenants)

        dbHelper = PropertyDatabaseHelper(this)

        val btnBack = findViewById<Button>(R.id.btnBack)
        rvTenants = findViewById(R.id.rvTenants)
        tvTenantCount = findViewById(R.id.tvTenantCount)
        tvEmptyTenants = findViewById(R.id.tvEmptyTenants)

        btnBack.setOnClickListener {
            finish()
        }

        rvTenants.layoutManager = LinearLayoutManager(this)

        loadTenants()
    }

    private fun loadTenants() {
        val tenants = dbHelper.getAllTenants()
        val properties = dbHelper.getAllProperties()

        tvTenantCount.text = "Tenants: ${tenants.size}"

        if (tenants.isEmpty()) {
            tvEmptyTenants.visibility = View.VISIBLE
            rvTenants.visibility = View.GONE
        } else {
            tvEmptyTenants.visibility = View.GONE
            rvTenants.visibility = View.VISIBLE
        }

        rvTenants.adapter = TenantAdapter(
            tenants = tenants,
            properties = properties,
            onEditClick = { tenant ->
                showEditTenantDialog(tenant)
            },
            onDeleteClick = { tenant ->

                AlertDialog.Builder(this)
                    .setTitle("Delete Tenant")
                    .setMessage("Are you sure you want to delete ${tenant.fullName}?")
                    .setPositiveButton("Delete") { _, _ ->

                        val deleted = dbHelper.deleteTenant(tenant.id)

                        if (deleted) {
                            Toast.makeText(
                                this,
                                "Tenant deleted successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            loadTenants()
                        } else {
                            Toast.makeText(
                                this,
                                "Failed to delete tenant",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
    }


    private fun showEditTenantDialog(tenant: Tenant) {

        val dialogView = layoutInflater.inflate(
            R.layout.dialog_edit_tenant,
            null
        )

        val etName = dialogView.findViewById<EditText>(
            R.id.etEditTenantName
        )
        val etPhone = dialogView.findViewById<EditText>(
            R.id.etEditTenantPhone
        )
        val etEmail = dialogView.findViewById<EditText>(
            R.id.etEditTenantEmail
        )
        val spProperty = dialogView.findViewById<Spinner>(
            R.id.spEditTenantProperty
        )
        val etUnit = dialogView.findViewById<EditText>(
            R.id.etEditTenantUnit
        )
        val etRent = dialogView.findViewById<EditText>(
            R.id.etEditTenantRent
        )

        val properties = dbHelper.getAllProperties()

        val propertyNames = properties.map {
            "${it.name} - ${it.address}"
        }

        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            propertyNames
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(Color.parseColor("#102B46"))
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(Color.parseColor("#102B46"))
                view.setBackgroundColor(Color.WHITE)
                return view
            }
        }

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spProperty.adapter = adapter

        etName.setText(tenant.fullName)
        etPhone.setText(tenant.phone)
        etEmail.setText(tenant.email)
        etUnit.setText(tenant.unitNumber)
        etRent.setText(tenant.monthlyRent.toString())

        val selectedIndex = properties.indexOfFirst {
            it.id == tenant.propertyId
        }

        if (selectedIndex >= 0) {
            spProperty.setSelection(selectedIndex)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Tenant")
            .setView(dialogView)
            .setPositiveButton("Update", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val name = etName.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val unit = etUnit.text.toString().trim()
                val rent = etRent.text.toString().trim().toDoubleOrNull()

                when {
                    name.isEmpty() -> etName.error = "Name is required"

                    phone.isEmpty() -> etPhone.error = "Phone is required"

                    email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                        etEmail.error = "Enter a valid email"

                    properties.isEmpty() ->
                        Toast.makeText(this, "Register a property first", Toast.LENGTH_SHORT).show()

                    unit.isEmpty() -> etUnit.error = "Unit number is required"

                    rent == null || rent <= 0 ->
                        etRent.error = "Enter a valid monthly rent"

                    else -> {
                        val updatedTenant = tenant.copy(
                            fullName = name,
                            phone = phone,
                            email = email,
                            propertyId = properties[spProperty.selectedItemPosition].id,
                            unitNumber = unit,
                            monthlyRent = rent
                        )

                        val updated = dbHelper.updateTenant(updatedTenant)

                        if (updated) {
                            Toast.makeText(
                                this,
                                "Tenant updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            dialog.dismiss()
                            loadTenants()
                        } else {
                            Toast.makeText(
                                this,
                                "Failed to update tenant",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        dialog.show()
    }


    override fun onResume() {
        super.onResume()

        if (::dbHelper.isInitialized && ::rvTenants.isInitialized) {
            loadTenants()
        }
    }
}
