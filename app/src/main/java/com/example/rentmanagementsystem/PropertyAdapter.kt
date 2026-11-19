package com.example.rentmanagementsystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class PropertyAdapter(
    private var propertyList: List<Property>,
    private val onEditClick: (Property) -> Unit,
    private val onDeleteClick: (Property) -> Unit
) : RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>() {

    class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPropertyName: TextView = itemView.findViewById(R.id.tvPropertyName)
        val tvPropertyAddress: TextView = itemView.findViewById(R.id.tvPropertyAddress)
        val tvNumberOfUnits: TextView = itemView.findViewById(R.id.tvNumberOfUnits)
        val tvMonthlyRent: TextView = itemView.findViewById(R.id.tvMonthlyRent)
        val btnEditProperty: Button = itemView.findViewById(R.id.btnEditProperty)
        val btnDeleteProperty: Button = itemView.findViewById(R.id.btnDeleteProperty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_property, parent, false)
        return PropertyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = propertyList[position]

        holder.tvPropertyName.text = property.name
        holder.tvPropertyAddress.text = property.address
        holder.tvNumberOfUnits.text = "Units: ${property.numberOfUnits}"

        val currencyFormat = NumberFormat.getNumberInstance(Locale.US)
        val formattedRent = currencyFormat.format(property.monthlyRent)
        holder.tvMonthlyRent.text = "KSh $formattedRent/mo"

        holder.btnEditProperty.setOnClickListener {
            onEditClick(property)
        }

        holder.btnDeleteProperty.setOnClickListener {
            onDeleteClick(property)
        }
    }

    override fun getItemCount(): Int = propertyList.size

    fun updateData(newPropertyList: List<Property>) {
        propertyList = newPropertyList
        notifyDataSetChanged()
    }
}
