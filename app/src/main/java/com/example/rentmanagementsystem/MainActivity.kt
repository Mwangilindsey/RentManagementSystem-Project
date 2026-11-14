package com.example.rentmanagementsystem

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnSignIn = findViewById<Button>(R.id.btnSignIn)

        btnSignIn.setOnClickListener {

            // Gets the email and password entered by the user
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            // Validates the email and password before proceeding
            when {

                // Checks whether the email field is empty
                email.isEmpty() -> {
                    etEmail.error = "Email is required"
                    etEmail.requestFocus()
                }

                // Checks whether the email has a valid format
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    etEmail.error = "Enter a valid email address"
                    etEmail.requestFocus()
                }

                // Checks whether the password field is empty
                password.isBlank() -> {
                    etPassword.error = "Password is required"
                    etPassword.requestFocus()
                }

                // Proceeds to the Dashboard if all validations pass
                else -> {
                    val intent = Intent(this, DashboardActivity::class.java)

                    // Passes the email to the Dashboard screen
                    intent.putExtra("email", email)

                    startActivity(intent)
                }
            }
        }
    }
}