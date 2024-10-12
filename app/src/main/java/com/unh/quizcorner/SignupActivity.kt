package com.unh.quizcorner

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.unh.quizcorner.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

private lateinit var binding:ActivitySignupBinding
private lateinit var firebaseAuth: FirebaseAuth


@SuppressLint("MissingInflatedId")
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)


    binding = ActivitySignupBinding.inflate(layoutInflater)
    setContentView(binding.root)

    firebaseAuth = FirebaseAuth.getInstance()
    binding.signInText.setOnClickListener{
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    binding.signupButton.setOnClickListener{
        val email = binding.emailInput.text.toString()
        val pass = binding.passwordInput.text.toString()
        val confirmPass = binding.confirmPasswordInput.text.toString()

        if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()){
            if(pass == confirmPass){
                firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener{
                    if(it.isSuccessful){
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }else {
                        Toast.makeText(this, it.exception.toString(),Toast .LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Passwords does not match !", Toast.LENGTH_SHORT).show()
            }
        }else {
            Toast.makeText(this, "Empty fields are not allowed !", Toast.LENGTH_SHORT).show()
        }
    }

}
}