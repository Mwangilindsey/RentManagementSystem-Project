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

    fun insertProperty(
        name: String,
        address: String,
        numberOfUnits: Int,
        monthlyRent: Double
    ): Boolean {

        // This stores the property details as database values
        val values = ContentValues().apply {
            put("name", name)
            put("address", address)
            put("number_of_units", numberOfUnits)
            put("monthly_rent", monthlyRent)
        }

        // Inserts the property details into the properties table
        val result = writableDatabase.insert(
            "properties",
            null,
            values
        )

        // Returns true if the property is saved successfully
        return result != -1L
    }

    companion object {
        private const val DATABASE_NAME = "rent_management.db"
        private const val DATABASE_VERSION = 1
    }
}