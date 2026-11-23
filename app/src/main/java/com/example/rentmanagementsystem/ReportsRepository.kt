package com.example.rentmanagementsystem

import android.content.Context
import java.util.Calendar

const val RENT_DUE_DAY = 5

data class TenantBalance(
    val id: Long,
    val name: String,
    val rent: Double,
    val dueDay: Int,
    val paid: Double
) {
    val balance: Double get() = rent - paid
}

class ReportsRepository(context: Context) {

    private val helper = PropertyDatabaseHelper(context)

    /** Total rent collected across all months (existing helper method). */
    fun getTotalCollected(): Double = helper.getTotalPayments()

    /** Report 1: rent collected per month. payment_date is dd/MM/yyyy. */
    fun getCollectionByMonth(): List<String> {
        val result = mutableListOf<String>()
        val c = helper.readableDatabase.rawQuery(
            """SELECT substr(payment_date, 7, 4) || '-' || substr(payment_date, 4, 2) AS m,
                      SUM(amount)
               FROM payments GROUP BY m ORDER BY m DESC""", null
        )
        c.use { while (it.moveToNext()) result.add("${it.getString(0)}  —  %.2f".format(it.getDouble(1))) }
        return result
    }

    /** Every tenant with what they have paid THIS month. */
    fun getBalancesThisMonth(): List<TenantBalance> {
        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH) + 1
        val year = cal.get(Calendar.YEAR)
        return helper.getAllTenants().map { t ->
            TenantBalance(
                id = t.id.toLong(),
                name = t.fullName,
                rent = t.monthlyRent,
                dueDay = RENT_DUE_DAY,
                paid = helper.getPaymentsForTenantInMonth(t.id, month, year)
            )
        }
    }

    /** Report 2: tenants who still owe rent this month. */
    fun getOutstanding(): List<TenantBalance> = getBalancesThisMonth().filter { it.balance > 0 }

    /** For the tenant picker. */
    fun getTenantNames(): List<Pair<Long, String>> =
        helper.getAllTenants().sortedBy { it.fullName }.map { it.id.toLong() to it.fullName }

    /** Report 3: one tenant's payment history, newest first. */
    fun getPaymentsForTenant(tenantId: Long): List<String> {
        val list = mutableListOf<String>()
        val c = helper.readableDatabase.rawQuery(
            "SELECT payment_date, amount FROM payments WHERE tenant_id = ? ORDER BY id DESC",
            arrayOf(tenantId.toString())
        )
        c.use { while (it.moveToNext()) list.add("${it.getString(0)}  —  %.2f".format(it.getDouble(1))) }
        return list
    }
}