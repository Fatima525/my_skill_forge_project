package com.example.fyp_skill_forge.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fyp_skill_forge.auth.login.Login
import com.example.fyp_skill_forge.databinding.ActivityPasswordChangedBinding

class PasswordChanged : AppCompatActivity() {
    private lateinit var binding: ActivityPasswordChangedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordChangedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackToLogin.setOnClickListener {
            val i = Intent(this, Login::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(i)
            finish()
        }
    }
}