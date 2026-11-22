package com.example.rentmanagementsystem

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PropertyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createPropertiesTable = """
            CREATE TABLE properties (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                address TEXT NOT NULL,
                number_of_units INTEGER NOT NULL,
                monthly_rent REAL NOT NULL
            )
        """.trimIndent()

        db.execSQL(createPropertiesTable)

        createTenantsTable(db)
        createPaymentsTable(db)
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
                FOREIGN KEY (property_id) REFERENCES properties(id)
                ON DELETE CASCADE
            )
        """.trimIndent())
    }

    private fun createPaymentsTable(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS payments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tenant_id INTEGER NOT NULL,
                amount REAL NOT NULL,
                payment_date TEXT NOT NULL,
                FOREIGN KEY (tenant_id) REFERENCES tenants(id)
                ON DELETE CASCADE
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

        if (oldVersion < 3) {
            createPaymentsTable(db)
        }
    }

    // Inserts a new property record
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

    // Retrieves all saved properties
    fun getAllProperties(): List<Property> {
        val propertyList = mutableListOf<Property>()

        val selectQuery =
            "SELECT * FROM $TABLE_PROPERTIES ORDER BY id DESC"

        val cursor = readableDatabase.rawQuery(selectQuery, null)

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val property = Property(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        name = it.getString(it.getColumnIndexOrThrow("name")),
                        address = it.getString(it.getColumnIndexOrThrow("address")),
                        numberOfUnits = it.getInt(
                            it.getColumnIndexOrThrow("number_of_units")
                        ),
                        monthlyRent = it.getDouble(
                            it.getColumnIndexOrThrow("monthly_rent")
                        )
                    )

                    propertyList.add(property)

                } while (it.moveToNext())
            }
        }

        return propertyList
    }

    // Updates an existing property
    fun updateProperty(
        id: Int,
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

        val result = writableDatabase.update(
            TABLE_PROPERTIES,
            values,
            "id = ?",
            arrayOf(id.toString())
        )

        return result > 0
    }

    // Deletes a property
    fun deleteProperty(id: Int): Boolean {
        val result = writableDatabase.delete(
            TABLE_PROPERTIES,
            "id = ?",
            arrayOf(id.toString())
        )

        return result > 0
    }

    // Inserts a new tenant
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

    // Retrieves all tenants
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
                    fullName = it.getString(
                        it.getColumnIndexOrThrow("full_name")
                    ),
                    phone = it.getString(
                        it.getColumnIndexOrThrow("phone")
                    ),
                    email = it.getString(
                        it.getColumnIndexOrThrow("email")
                    ),
                    propertyId = it.getInt(
                        it.getColumnIndexOrThrow("property_id")
                    ),
                    unitNumber = it.getString(
                        it.getColumnIndexOrThrow("unit_number")
                    ),
                    monthlyRent = it.getDouble(
                        it.getColumnIndexOrThrow("monthly_rent")
                    )
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
                    fullName = it.getString(
                        it.getColumnIndexOrThrow("full_name")
                    ),
                    phone = it.getString(
                        it.getColumnIndexOrThrow("phone")
                    ),
                    email = it.getString(
                        it.getColumnIndexOrThrow("email")
                    ),
                    propertyId = it.getInt(
                        it.getColumnIndexOrThrow("property_id")
                    ),
                    unitNumber = it.getString(
                        it.getColumnIndexOrThrow("unit_number")
                    ),
                    monthlyRent = it.getDouble(
                        it.getColumnIndexOrThrow("monthly_rent")
                    )
                )
            }
        }

        return null
    }

    // Updates an existing tenant
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

    // Deletes a tenant
    fun deleteTenant(tenantId: Int): Boolean {
        val result = writableDatabase.delete(
            TABLE_TENANTS,
            "id = ?",
            arrayOf(tenantId.toString())
        )

        return result > 0
    }

    // Saves a rent payment
    fun insertPayment(
        tenantId: Int,
        amount: Double,
        paymentDate: String
    ): Boolean {
        val values = ContentValues().apply {
            put("tenant_id", tenantId)
            put("amount", amount)
            put("payment_date", paymentDate)
        }

        val result = writableDatabase.insert(
            TABLE_PAYMENTS,
            null,
            values
        )

        return result != -1L
    }

    // Retrieves payment history with tenant and property details
    fun getAllPaymentRecords(): List<PaymentRecord> {
        val paymentList = mutableListOf<PaymentRecord>()

        val query = """
            SELECT
                payments.id,
                tenants.full_name,
                properties.name,
                tenants.unit_number,
                payments.amount,
                payments.payment_date
            FROM payments
            INNER JOIN tenants
                ON payments.tenant_id = tenants.id
            INNER JOIN properties
                ON tenants.property_id = properties.id
            ORDER BY payments.id DESC
        """.trimIndent()

        val cursor = readableDatabase.rawQuery(query, null)

        cursor.use {
            while (it.moveToNext()) {
                paymentList.add(
                    PaymentRecord(
                        id = it.getInt(0),
                        tenantName = it.getString(1),
                        propertyName = it.getString(2),
                        unitNumber = it.getString(3),
                        amount = it.getDouble(4),
                        paymentDate = it.getString(5)
                    )
                )
            }
        }

        return paymentList
    }

    // Calculates payments made by a tenant during a specific month
    fun getPaymentsForTenantInMonth(
        tenantId: Int,
        month: Int,
        year: Int
    ): Double {

        val formattedMonth = month.toString().padStart(2, '0')

        val query = """
            SELECT SUM(amount)
            FROM payments
            WHERE tenant_id = ?
            AND substr(payment_date, 4, 2) = ?
            AND substr(payment_date, 7, 4) = ?
        """.trimIndent()

        val cursor = readableDatabase.rawQuery(
            query,
            arrayOf(
                tenantId.toString(),
                formattedMonth,
                year.toString()
            )
        )

        cursor.use {
            if (it.moveToFirst()) {
                return if (it.isNull(0)) 0.0 else it.getDouble(0)
            }
        }

        return 0.0
    }

    // Calculates the total rent collected
    fun getTotalPayments(): Double {
        val query = "SELECT SUM(amount) FROM $TABLE_PAYMENTS"
        val cursor = readableDatabase.rawQuery(query, null)

        cursor.use {
            if (it.moveToFirst()) {
                return if (it.isNull(0)) 0.0 else it.getDouble(0)
            }
        }

        return 0.0
    }

    // Returns the total number of registered properties
    fun getPropertiesCount(): Int {
        val countQuery = "SELECT COUNT(*) FROM $TABLE_PROPERTIES"
        val cursor = readableDatabase.rawQuery(countQuery, null)

        cursor.use {
            if (it.moveToFirst()) {
                return it.getInt(0)
            }
        }

        return 0
    }

    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }

    companion object {
        private const val DATABASE_NAME = "rent_management.db"
        private const val DATABASE_VERSION = 3

        private const val TABLE_PROPERTIES = "properties"
        private const val TABLE_TENANTS = "tenants"
        private const val TABLE_PAYMENTS = "payments"
    }
}
