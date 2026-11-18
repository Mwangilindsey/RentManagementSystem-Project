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
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

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

    companion object {
        private const val DATABASE_NAME = "rent_management.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_PROPERTIES = "properties"
    }
}
