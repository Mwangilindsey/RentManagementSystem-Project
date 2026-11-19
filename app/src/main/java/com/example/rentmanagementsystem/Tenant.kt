package com.example.rentmanagementsystem


data class Tenant(
    val id: Int = 0,
    val fullName: String,
    val phone: String,
    val email: String,
    val propertyId: Int,
    val unitNumber: String,
    val monthlyRent: Double
)