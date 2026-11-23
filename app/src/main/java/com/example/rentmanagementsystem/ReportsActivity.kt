package com.example.rentmanagementsystem

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout

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

        tenants = repo.getTenantNames()
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tenants.map { it.second })
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) = showHistory()
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }

        tabs.addTab(tabs.newTab().setText("Collected"))
        tabs.addTab(tabs.newTab().setText("Outstanding"))
        tabs.addTab(tabs.newTab().setText("History"))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) = show(tab.position)
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) = show(tab.position)
        })
        show(0)
    }

    private fun show(tab: Int) {
        spinner.visibility = if (tab == 2) View.VISIBLE else View.GONE
        when (tab) {
            0 -> {
                summary.text = "Total collected: %.2f".format(repo.getTotalCollected())
                setItems(repo.getCollectionByMonth(), "No payments recorded yet")
            }
            1 -> {
                val out = repo.getOutstanding()
                summary.text = "Total outstanding this month: %.2f".format(out.sumOf { it.balance })
                setItems(out.map { "${it.name}  —  owes %.2f (due day ${it.dueDay})".format(it.balance) }, "Nobody owes rent")
            }
            2 -> {
                summary.text = "Select a tenant"
                showHistory()
            }
        }
    }

    private fun showHistory() {
        if (tenants.isEmpty()) { setItems(emptyList(), "No tenants found"); return }
        val tenant = tenants[spinner.selectedItemPosition.coerceAtLeast(0)]
        summary.text = "Payments by ${tenant.second}"
        setItems(repo.getPaymentsForTenant(tenant.first), "No payments for this tenant")
    }

    private fun setItems(items: List<String>, emptyMsg: String) {
        val data = if (items.isEmpty()) listOf(emptyMsg) else items
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, data)
    }
}