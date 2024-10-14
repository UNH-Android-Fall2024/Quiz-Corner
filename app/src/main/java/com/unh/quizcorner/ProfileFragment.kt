package com.unh.quizcorner

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class ProfileFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        firebaseAuth = FirebaseAuth.getInstance()

        val profileNameTextView: TextView = view.findViewById(R.id.profile_name)
        val currentUser: FirebaseUser? = firebaseAuth.currentUser

        // User name before @  is displaed.
        currentUser?.let {
            val userEmail = it.email
            val userName = ("Welcome," +(userEmail?.substringBefore("@") ?: "User"))
            profileNameTextView.text = userName
        }

        val fabAddQuiz: FloatingActionButton = view.findViewById(R.id.fab_add_quiz)
        fabAddQuiz.setOnClickListener {
            val intent = Intent(requireContext(), AddquizActivity::class.java)
            startActivity(intent)
        }

        // Find the sign-out button
        val signOutButton: Button = view.findViewById(R.id.signoutButton)
        signOutButton.setOnClickListener {
            // Sign out the user
            firebaseAuth.signOut()
            val intent = Intent(requireContext(), SignupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }

        return view
    }
}
