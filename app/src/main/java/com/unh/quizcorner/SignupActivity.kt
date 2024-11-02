package com.unh.quizcorner

/**
 * This is the SignupActivity file which is responsible for the User Signup Authentication.
 * This class creates an account for the user, and saves it in the database.
 */
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unh.quizcorner.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

private lateinit var binding:ActivitySignupBinding
private lateinit var firebaseAuth: FirebaseAuth
private lateinit var firestore: FirebaseFirestore

@SuppressLint("MissingInflatedId")
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)


    binding = ActivitySignupBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // instantiate firebase and firestore
    firebaseAuth = FirebaseAuth.getInstance()
    firestore = FirebaseFirestore.getInstance()

    binding.signInText.setOnClickListener{
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    /**
     * The below code checks whether the passwords entered are correct or not. If all the data ( email,password,confirmpassword)
     * are correct, User account will be created and will be navigated to LoginActivity .
     */

    binding.signupButton.setOnClickListener {
        val nickname = binding.nicknameInput.text.toString()
        val email = binding.emailInput.text.toString()
        val pass = binding.passwordInput.text.toString()
        val confirmPass = binding.confirmPasswordInput.text.toString()

        if (nickname.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
            if (pass == confirmPass) {
                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Get the user ID and nickname
                        val userId = firebaseAuth.currentUser?.uid
                        val nickname = binding.nicknameInput.text.toString()
                        // Create a user object
                        val user = hashMapOf(
                            "nickname" to nickname,
                            "email" to email
                        )
                        // Add user data to Firestore
                        userId?.let {
                            firestore.collection("users").document(it)
                                .set(user)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish() // Close SignupActivity
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

}
}

/**
 * REFERENCES ::
 * https://www.geeksforgeeks.org/login-and-registration-in-android-using-firebase-in-kotlin/
 *
 */