package com.example.fyp_skill_forge.auth.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.fyp_skill_forge.R
import com.example.fyp_skill_forge.auth.ForgetPassword
import com.example.fyp_skill_forge.auth.signup.SignUp
import com.example.fyp_skill_forge.databinding.ActivityLoginBinding
import com.example.fyp_skill_forge.student.Home
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        setContentView(binding.root)

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        // ✅ Initialize Google Sign-In Client (this was missing in your code)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Forget password
        binding.tvForgetPassword.setOnClickListener {
            startActivity(Intent(this, ForgetPassword::class.java))
        }

        // Sign up
        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }

        // Email + Password Login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (checkAllField()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null && user.isEmailVerified) {
                            Toast.makeText(this, "Successfully Signed In", Toast.LENGTH_SHORT).show()
                            saveLoginState()
                            startActivity(Intent(this, Home::class.java))
                            finish()
                            updatePasswordInFirestore(user.uid, password)
                        } else {
                            Toast.makeText(this, "Please verify your email first!", Toast.LENGTH_LONG).show()
                            user?.sendEmailVerification()
                        }
                    } else {
                        Toast.makeText(this, "Email or password incorrect", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Google Sign-In Button
        binding.googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun checkAllField(): Boolean {
        val email = binding.etEmail.text.toString()
        if (email.isEmpty()) {
            binding.etEmail.error = "This is a required field"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Invalid email format"
            return false
        }
        if (binding.etPassword.text.toString().isEmpty()) {
            binding.etPassword.error = "This is a required field"
            return false
        }
        if (binding.etPassword.length() < 8) {
            binding.etPassword.error = "Password should be at least 8 characters"
            return false
        }
        return true
    }

    private fun saveLoginState() {
        val pref: SharedPreferences = getSharedPreferences("userLoginInfo", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("flag", true)
        editor.apply()
    }

    private fun updatePasswordInFirestore(userId: String, password: String) {
        firestore.collection("users").document(userId)
            .update("password", password)
    }

    // ✅ Handle Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    val userId = user.uid
                    val userEmail = user.email ?: ""
                    val userName = user.displayName ?: ""

                    val userMap = hashMapOf(
                        "user_id" to userId,
                        "email" to userEmail,
                        "name" to userName,
                        "phone" to "",
                        "password" to "",
                        "date" to Calendar.getInstance().time.toString()
                    )

                    firestore.collection("users").document(userId).set(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Google Sign-In successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, Home::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Firebase Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
