package com.example.rentmanagementsystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TenantAdapter(
    private val tenants: List<Tenant>,
    private val properties: List<Property>,
    private val onEditClick: (Tenant) -> Unit,
    private val onDeleteClick: (Tenant) -> Unit
) : RecyclerView.Adapter<TenantAdapter.TenantViewHolder>() {

    class TenantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvItemTenantName)
        val tvPhone: TextView = itemView.findViewById(R.id.tvItemTenantPhone)
        val tvEmail: TextView = itemView.findViewById(R.id.tvItemTenantEmail)
        val tvProperty: TextView = itemView.findViewById(R.id.tvItemTenantProperty)
        val tvUnitRent: TextView = itemView.findViewById(R.id.tvItemTenantUnitRent)
        val btnEdit: Button = itemView.findViewById(R.id.btnEditTenant)
        val btnDelete: Button = itemView.findViewById(R.id.btnDeleteTenant)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TenantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tenant, parent, false)

        return TenantViewHolder(view)
    }

    override fun onBindViewHolder(holder: TenantViewHolder, position: Int) {
        val tenant = tenants[position]

        holder.tvName.text = tenant.fullName
        holder.tvPhone.text = "Phone: ${tenant.phone}"
        holder.tvEmail.text = "Email: ${tenant.email}"

        val property = properties.find { it.id == tenant.propertyId }

        holder.tvProperty.text = if (property != null) {
            "Property: ${property.name}"
        } else {
            "Property: Unknown"
        }

        holder.tvUnitRent.text =
            "Unit: ${tenant.unitNumber} | Rent: ${tenant.monthlyRent}"

        holder.btnEdit.setOnClickListener {
            onEditClick(tenant)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(tenant)
        }
    }

    override fun getItemCount(): Int = tenants.size
}