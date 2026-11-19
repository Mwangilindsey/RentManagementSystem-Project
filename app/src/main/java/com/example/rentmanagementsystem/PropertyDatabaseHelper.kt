package com.example.rentmanagementsystem

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PropertyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE properties (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                address TEXT NOT NULL,
                number_of_units INTEGER NOT NULL,
                monthly_rent REAL NOT NULL
            )
        """.trimIndent()

        db.execSQL(createTable)

        createTenantsTable(db)
    }


    private fun createTenantsTable(db: SQLiteDatabase) {
        db.execSQL("""
        CREATE TABLE IF NOT EXISTS tenants (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            full_name TEXT NOT NULL,
            phone TEXT NOT NULL,
            email TEXT NOT NULL,
            property_id INTEGER NOT NULL,
            unit_number TEXT NOT NULL,
            monthly_rent REAL NOT NULL,
            FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
        )
    """.trimIndent())
    }


    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        if (oldVersion < 2) {
            createTenantsTable(db)
        }

    }

    // Inserts a new property record into SQLite (Create)
    fun insertProperty(
        name: String,
        address: String,
        numberOfUnits: Int,
        monthlyRent: Double
    ): Boolean {
        val values = ContentValues().apply {
            put("name", name)
            put("address", address)
            put("number_of_units", numberOfUnits)
            put("monthly_rent", monthlyRent)
        }

        val result = writableDatabase.insert(
            TABLE_PROPERTIES,
            null,
            values
        )
        return result != -1L
    }

    // Retrieves all saved property records from SQLite (Read)
    fun getAllProperties(): List<Property> {
        val propertyList = mutableListOf<Property>()
        val selectQuery = "SELECT * FROM $TABLE_PROPERTIES ORDER BY id DESC"
        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndexOrThrow("id")
            val nameIndex = cursor.getColumnIndexOrThrow("name")
            val addressIndex = cursor.getColumnIndexOrThrow("address")
            val unitsIndex = cursor.getColumnIndexOrThrow("number_of_units")
            val rentIndex = cursor.getColumnIndexOrThrow("monthly_rent")

            do {
                val property = Property(
                    id = cursor.getInt(idIndex),
                    name = cursor.getString(nameIndex),
                    address = cursor.getString(addressIndex),
                    numberOfUnits = cursor.getInt(unitsIndex),
                    monthlyRent = cursor.getDouble(rentIndex)
                )
                propertyList.add(property)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return propertyList
    }

    // Updates an existing property record in SQLite (Update)
    fun updateProperty(
        id: Int,
        name: String,
        address: String,
        numberOfUnits: Int,
        monthlyRent: Double
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("address", address)
            put("number_of_units", numberOfUnits)
            put("monthly_rent", monthlyRent)
        }

        val result = db.update(
            TABLE_PROPERTIES,
            values,
            "id = ?",
            arrayOf(id.toString())
        )
        return result > 0
    }

    // Deletes a property record from SQLite by ID (Delete)
    fun deleteProperty(id: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(
            TABLE_PROPERTIES,
            "id = ?",
            arrayOf(id.toString())
        )
        return result > 0
    }

    // Inserts a new tenant record into SQLite
    fun insertTenant(tenant: Tenant): Boolean {
        val values = ContentValues().apply {
            put("full_name", tenant.fullName)
            put("phone", tenant.phone)
            put("email", tenant.email)
            put("property_id", tenant.propertyId)
            put("unit_number", tenant.unitNumber)
            put("monthly_rent", tenant.monthlyRent)
        }

        val result = writableDatabase.insert(
            TABLE_TENANTS,
            null,
            values
        )

        return result != -1L
    }


    // Retrieves all tenants from the database
    fun getAllTenants(): List<Tenant> {
        val tenantList = mutableListOf<Tenant>()

        val cursor = readableDatabase.query(
            TABLE_TENANTS,
            null,
            null,
            null,
            null,
            null,
            "id DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                val tenant = Tenant(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    fullName = it.getString(it.getColumnIndexOrThrow("full_name")),
                    phone = it.getString(it.getColumnIndexOrThrow("phone")),
                    email = it.getString(it.getColumnIndexOrThrow("email")),
                    propertyId = it.getInt(it.getColumnIndexOrThrow("property_id")),
                    unitNumber = it.getString(it.getColumnIndexOrThrow("unit_number")),
                    monthlyRent = it.getDouble(it.getColumnIndexOrThrow("monthly_rent"))
                )

                tenantList.add(tenant)
            }
        }

        return tenantList
    }


    // Retrieves one tenant using their ID
    fun getTenantById(tenantId: Int): Tenant? {
        val cursor = readableDatabase.query(
            TABLE_TENANTS,
            null,
            "id = ?",
            arrayOf(tenantId.toString()),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                return Tenant(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    fullName = it.getString(it.getColumnIndexOrThrow("full_name")),
                    phone = it.getString(it.getColumnIndexOrThrow("phone")),
                    email = it.getString(it.getColumnIndexOrThrow("email")),
                    propertyId = it.getInt(it.getColumnIndexOrThrow("property_id")),
                    unitNumber = it.getString(it.getColumnIndexOrThrow("unit_number")),
                    monthlyRent = it.getDouble(it.getColumnIndexOrThrow("monthly_rent"))
                )
            }
        }

        return null
    }


    // Updates an existing tenant record
    fun updateTenant(tenant: Tenant): Boolean {
        val values = ContentValues().apply {
            put("full_name", tenant.fullName)
            put("phone", tenant.phone)
            put("email", tenant.email)
            put("property_id", tenant.propertyId)
            put("unit_number", tenant.unitNumber)
            put("monthly_rent", tenant.monthlyRent)
        }

        val result = writableDatabase.update(
            TABLE_TENANTS,
            values,
            "id = ?",
            arrayOf(tenant.id.toString())
        )

        return result > 0
    }


    // Deletes a tenant using their ID
    fun deleteTenant(tenantId: Int): Boolean {
        val result = writableDatabase.delete(
            TABLE_TENANTS,
            "id = ?",
            arrayOf(tenantId.toString())
        )

        return result > 0
    }







    // Returns the total count of registered properties
    fun getPropertiesCount(): Int {
        val countQuery = "SELECT COUNT(*) FROM $TABLE_PROPERTIES"
        val db = readableDatabase
        val cursor = db.rawQuery(countQuery, null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }

    companion object {
        private const val DATABASE_NAME = "rent_management.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_PROPERTIES = "properties"

        private const val TABLE_TENANTS = "tenants"
    }
}
