package com.example.rentmanagementsystem

data class Property(
    val id: Int,
    val name: String,
    val address: String,
    val numberOfUnits: Int,
    val monthlyRent: Double
)
