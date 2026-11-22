package com.example.rentmanagementsystem

data class PaymentRecord(
    val id: Int,
    val tenantName: String,
    val propertyName: String,
    val unitNumber: String,
    val amount: Double,
    val paymentDate: String
)
