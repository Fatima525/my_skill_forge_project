package com.example.fyp_skill_forge.auth.signup

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.fyp_skill_forge.R
import com.example.fyp_skill_forge.auth.login.Login
import com.example.fyp_skill_forge.databinding.ActivitySignupBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class SignUp : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignupBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        setContentView(binding.root)
        binding.tvSignIn.setOnClickListener{
            val i = Intent(this, Login::class.java)
            startActivity(i)
        }

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (checkAllField()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                                if (verificationTask.isSuccessful) {
                                    saveUserData(user.uid, email, password)
                                } else {
                                    Toast.makeText(this, "Failed to send verification email: ${verificationTask.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            Toast.makeText(
                                baseContext, "Authentication failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }

    // Function to save user data in Firestore
    private fun saveUserData(userId: String, email: String, password: String) {
        val userData = hashMapOf(
            "name" to binding.etUsername.text.toString(),
            "email" to email,
            "phone" to binding.etPhone.text.toString(),
            "password" to password,
            "user_id" to userId,
            "date" to Calendar.getInstance().time.toString(),
            "isVerified" to false
        )

        firestore.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Verification email sent. Please verify before logging in.",
                    Toast.LENGTH_LONG
                ).show()

                // 👉 Don't clear fields here, just navigate
                val intent = Intent(this, Login::class.java)
                startActivity(intent)

                // Finish so user cannot return back to signup with back button
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: $e", Toast.LENGTH_SHORT).show()
            }
    }


    private fun checkAllField(): Boolean {
        val email = binding.etEmail.text.toString()
        if (binding.etUsername.text.toString().isEmpty()) {
            binding.etUsername.error = "This is a required field"
            binding.etUsername.requestFocus()
            return false
        }
        if (binding.etEmail.text.toString().isEmpty()) {
            binding.etEmail.error = "This is a required field"
            binding.etEmail.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Check email format"
            binding.etEmail.requestFocus()
            return false
        }
        if (binding.etPhone.text.toString().isEmpty()) {
            binding.etPhone.error = "This is a required field"
            binding.etPhone.requestFocus()
            return false
        }
        if (binding.etPhone.length() != 11) {
            binding.etPhone.error = "Enter a valid 11-digit number"
            binding.etPhone.requestFocus()
            return false
        }
        if (binding.etPassword.text.toString().isEmpty()) {
            binding.etPassword.error = "This is a required field"
            binding.etPassword.requestFocus()
            return false
        }
        if (binding.etPassword.length() < 8) {
            binding.etPassword.error = "Password should be at least 8 characters"
            binding.etPassword.requestFocus()
            return false
        }
        if (binding.etConfirmPassword.text.toString().isEmpty()) {
            binding.etConfirmPassword.error = "This is a required field"
            binding.etConfirmPassword.requestFocus()
            return false
        }
        if (binding.etPassword.text.toString() != binding.etConfirmPassword.text.toString()) {
            binding.etConfirmPassword.error = "Passwords do not match"
            binding.etConfirmPassword.requestFocus()
            return false
        }

        return true
    }

    private fun clearFields() {
        binding.etUsername.text?.clear()
        binding.etEmail.text?.clear()
        binding.etPhone.text?.clear()
        binding.etPassword.text?.clear()
        binding.etConfirmPassword.text?.clear()

        binding.etUsername.error = null
        binding.etEmail.error = null
        binding.etPhone.error = null
        binding.etPassword.error = null
        binding.etConfirmPassword.error = null
    }

}