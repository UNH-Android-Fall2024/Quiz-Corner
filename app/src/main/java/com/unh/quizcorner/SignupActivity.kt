package com.unh.quizcorner

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.unh.quizcorner.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    private val RC_SIGN_IN = 1001 // Request code for Google Sign-In

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Instantiate Firebase Auth and Firestore
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        // Handle email-password signup
        binding.signupButton.setOnClickListener {
            val nickname = binding.nicknameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val pass = binding.passwordInput.text.toString()
            val confirmPass = binding.confirmPasswordInput.text.toString()

            if (nickname.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = firebaseAuth.currentUser?.uid
                            val user = hashMapOf(
                                "nickname" to nickname,
                                "email" to email
                            )
                            userId?.let {
                                firestore.collection("users").document(it)
                                    .set(user)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error adding user: $e", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty fields are not allowed!", Toast.LENGTH_SHORT).show()
            }
        }



        // Navigate to LoginActivity
        binding.signInText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }




    private fun saveUserToFirestore(user: FirebaseUser?) {
        user?.let {
            val userData = hashMapOf(
                "nickname" to it.displayName,
                "email" to it.email
            )
            firestore.collection("users").document(it.uid)
                .set(userData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Signed in with Google!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error adding user: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }
}

/**
 * REFERENCES ::
 *
 *https://www.geeksforgeeks.org/login-and-registration-in-android-using-firebase-in-kotlin/
 * https://www.geeksforgeeks.org/google-signing-using-firebase-authentication-in-kotlin/
 */
