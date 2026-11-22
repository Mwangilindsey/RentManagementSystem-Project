package com.example.rentmanagementsystem

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class PaymentHistoryActivity : AppCompatActivity() {

    private lateinit var dbHelper: PropertyDatabaseHelper
    private lateinit var layoutPaymentRecords: LinearLayout
    private lateinit var tvTotalCollected: TextView
    private lateinit var tvNoPayments: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_history)

        dbHelper = PropertyDatabaseHelper(this)

        layoutPaymentRecords = findViewById(R.id.layoutPaymentRecords)
        tvTotalCollected = findViewById(R.id.tvTotalCollected)
        tvNoPayments = findViewById(R.id.tvNoPayments)

        val btnBack = findViewById<Button>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        loadPaymentHistory()
    }

    override fun onResume() {
        super.onResume()

        if (::dbHelper.isInitialized) {
            loadPaymentHistory()
        }
    }

    private fun loadPaymentHistory() {
        layoutPaymentRecords.removeAllViews()

        val totalCollected = dbHelper.getTotalPayments()

        tvTotalCollected.text = String.format(
            Locale.getDefault(),
            "Total Rent Collected: KSh %,.2f",
            totalCollected
        )

        val payments = dbHelper.getAllPaymentRecords()

        if (payments.isEmpty()) {
            tvNoPayments.visibility = android.view.View.VISIBLE
            return
        }

        tvNoPayments.visibility = android.view.View.GONE

        for (payment in payments) {
            addPaymentCard(payment)
        }
    }

    private fun addPaymentCard(payment: PaymentRecord) {

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))

            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = dp(12).toFloat()
            }

            elevation = dp(3).toFloat()
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.bottomMargin = dp(16)
        card.layoutParams = params

        val tenantName = createTextView(
            "Tenant: ${payment.tenantName}",
            17f,
            "#102B46",
            true
        )

        val propertyName = createTextView(
            "Property: ${payment.propertyName}",
            14f,
            "#60758A"
        )

        val unitNumber = createTextView(
            "Unit: ${payment.unitNumber}",
            14f,
            "#60758A"
        )

        val amount = createTextView(
            String.format(
                Locale.getDefault(),
                "Amount Paid: KSh %,.2f",
                payment.amount
            ),
            16f,
            "#087F8C",
            true
        )

        val paymentDate = createTextView(
            "Payment Date: ${payment.paymentDate}",
            14f,
            "#60758A"
        )

        card.addView(tenantName)
        card.addView(propertyName)
        card.addView(unitNumber)
        card.addView(amount)
        card.addView(paymentDate)

        layoutPaymentRecords.addView(card)
    }

    private fun createTextView(
        text: String,
        textSize: Float,
        textColor: String,
        bold: Boolean = false
    ): TextView {

        return TextView(this).apply {
            this.text = text
            this.textSize = textSize
            setTextColor(Color.parseColor(textColor))
            gravity = Gravity.START
            setPadding(0, dp(5), 0, dp(5))

            if (bold) {
                setTypeface(null, Typeface.BOLD)
            }
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
