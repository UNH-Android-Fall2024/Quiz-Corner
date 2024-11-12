package com.unh.quizcorner

/**
 * This is the LoginActivity file which is responsible for the User Login Authentication.
 * The class LoginActivity checks if the user is already signed up or not, If yes, User will be navigated to the Home fragment under MainActivity..
 * Otherwise, User will be navigated to the SignupActivity file.
 */
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.unh.quizcorner.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * Instantiating firebase  and navigation being defined to SignUpActivity .
         */
        firebaseAuth = FirebaseAuth.getInstance()
        binding.haventregisteredText.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.signinButton.setOnClickListener {
            /**
             * Saving the data (email,password) from the user.
             */
            val email = binding.emailInput.text.toString()
            val pass = binding.passwordInput.text.toString()

            /**
             * Checking if the user entered email and password or not .
             */
            if (email.isNotEmpty() && pass.isNotEmpty()){

                    firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        /**
         * The below code is to make the development easy, this code checks if the user is signed in or not,
         * if yes it will take the user directly to the main page, user dont have to login again.
         */
        if(firebaseAuth.currentUser!= null ){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}

/**
 * REFERENCES ::
 * https://firebase.google.com/docs/auth/android/start
 * https://www.geeksforgeeks.org/login-and-registration-in-android-using-firebase-in-kotlin/
 */