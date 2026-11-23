package com.example.rentmanagementsystem

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import java.util.Locale

class ReportsActivity : AppCompatActivity() {

    private lateinit var repo: ReportsRepository
    private lateinit var listView: ListView
    private lateinit var spinner: Spinner
    private lateinit var summary: TextView

    private var tenants: List<Pair<Long, String>> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        repo = ReportsRepository(this)

        listView = findViewById(R.id.reportList)
        spinner = findViewById(R.id.tenantSpinner)
        summary = findViewById(R.id.summaryText)

        val tabs = findViewById<TabLayout>(R.id.reportTabs)
        val btnBack = findViewById<Button>(R.id.btnBack)

        // Returns to the Dashboard
        btnBack.setOnClickListener {
            finish()
        }

        tenants = repo.getTenantNames()

        spinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            tenants.map { it.second }
        )

        spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position in tenants.indices) {
                        showHistory()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        tabs.addTab(tabs.newTab().setText("Collected"))
        tabs.addTab(tabs.newTab().setText("Outstanding"))
        tabs.addTab(tabs.newTab().setText("History"))

        tabs.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {

                override fun onTabSelected(tab: TabLayout.Tab) {
                    show(tab.position)
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabReselected(tab: TabLayout.Tab) {
                    show(tab.position)
                }
            }
        )

        show(0)
    }

    private fun show(tab: Int) {

        spinner.visibility =
            if (tab == 2) View.VISIBLE else View.GONE

        when (tab) {

            // Monthly rent collections
            0 -> {
                summary.text = "Total collected: ${
                    formatMoney(repo.getTotalCollected())
                }"

                setItems(
                    repo.getCollectionByMonth(),
                    "No payments recorded yet"
                )
            }

            // Outstanding rent
            1 -> {
                val outstanding = repo.getOutstanding()

                val totalOutstanding =
                    outstanding.sumOf { it.balance }

                summary.text =
                    "Total Outstanding This Month\n" +
                            formatMoney(totalOutstanding)

                val adapter =
                    object : ArrayAdapter<TenantBalance>(
                        this,
                        android.R.layout.simple_list_item_2,
                        android.R.id.text1,
                        outstanding
                    ) {

                        override fun getView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {

                            val row = super.getView(
                                position,
                                convertView,
                                parent
                            )

                            val tenant = getItem(position)!!

                            val name =
                                row.findViewById<TextView>(android.R.id.text1)

                            val details =
                                row.findViewById<TextView>(android.R.id.text2)

                            name.text = tenant.name
                            name.textSize = 16f
                            name.setTypeface(null, android.graphics.Typeface.BOLD)

                            details.text =
                                "Outstanding: ${formatMoney(tenant.balance)}\n" +
                                        "Rent due: ${tenant.dueDay}${ordinalSuffix(tenant.dueDay)} of every month"

                            details.textSize = 14f

                            return row
                        }
                    }

                if (outstanding.isEmpty()) {
                    setItems(emptyList(), "Nobody owes rent")
                } else {
                    listView.adapter = adapter
                }
            }

            // Individual tenant payment history
            2 -> {
                summary.text = "Select a tenant"
                showHistory()
            }
        }
    }

    private fun showHistory() {

        if (tenants.isEmpty()) {
            summary.text = "No tenants available"
            setItems(emptyList(), "No tenants found")
            return
        }

        val position = spinner.selectedItemPosition

        if (position !in tenants.indices) return

        val tenant = tenants[position]

        summary.text = "Payments by ${tenant.second}"

        setItems(
            repo.getPaymentsForTenant(tenant.first),
            "No payments for this tenant"
        )
    }

    private fun setItems(
        items: List<String>,
        emptyMsg: String
    ) {

        val data =
            if (items.isEmpty()) listOf(emptyMsg) else items

        listView.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            data
        )
    }

    private fun formatMoney(amount: Double): String {
        return String.format(
            Locale.getDefault(),
            "KSh %,.2f",
            amount
        )
    }

    private fun ordinalSuffix(day: Int): String {
        return when {
            day % 100 in 11..13 -> "th"
            day % 10 == 1 -> "st"
            day % 10 == 2 -> "nd"
            day % 10 == 3 -> "rd"
            else -> "th"
        }
    }
}
