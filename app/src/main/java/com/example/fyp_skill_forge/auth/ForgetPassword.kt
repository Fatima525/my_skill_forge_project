package com.example.fyp_skill_forge.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.fyp_skill_forge.R
import com.example.fyp_skill_forge.auth.login.Login
import com.example.fyp_skill_forge.databinding.ActivityForgetPasswordBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class ForgetPassword : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityForgetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        setContentView(binding.root)

        binding.tvSignIn.setOnClickListener {
            val i = Intent(this, Login::class.java)
            startActivity(i)
            finish()
        }

        auth = Firebase.auth

        binding.btnResetPassword.setOnClickListener {
            val email = binding.etEmail.text.toString()
            if (checkAllField()) {
                auth.sendPasswordResetEmail(email).addOnCompleteListener {task ->
                    if (task.isSuccessful) {

                        Toast.makeText(this, "Email sent successfully",
                            Toast.LENGTH_SHORT).show()

                        // Clear filled data
                        binding.etEmail.text?.clear()
                        binding.etEmail.error = null

                        val i = Intent(this, Login::class.java)
                        startActivity(i)
                        finish()
                    }
                }
            }
        }

    }

    private fun checkAllField(): Boolean {
        val email = binding.etEmail.text.toString()
        if(binding.etEmail.text.toString() == ""){
            binding.etEmail.error = "This is required field"
            binding.etEmail.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.error = "Check email format"
            binding.etEmail.requestFocus()
            return false
        }
        return true
    }

}






