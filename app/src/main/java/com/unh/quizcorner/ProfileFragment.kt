package com.unh.quizcorner

/**
 * This is ProfileFragment file which represents Basic profile of the user who logged in
 * User name will be displayed based on the gmail entered by the user.
 */
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class ProfileFragment : Fragment() {
    // Firebase Authentication instance for managing the current user session
    private lateinit var firebaseAuth: FirebaseAuth

    // this is a profile section comment
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        firebaseAuth = FirebaseAuth.getInstance()


        val profileNameTextView: TextView = view.findViewById(R.id.profile_name)
        val currentUser: FirebaseUser? = firebaseAuth.currentUser

        // User name before @ from gmail is displayed.
        currentUser?.let {
            val userEmail = it.email
            val userName = ("Welcome," +(userEmail?.substringBefore("@") ?: "User"))
            //val userName2 = currentUser.displayName
            profileNameTextView.text = (userName)
        }

        // this is a floating action button which takes user to Add Quiz Activity
        //where user can add a quiz if needed .
        val fabAddQuiz: FloatingActionButton = view.findViewById(R.id.fab_add_quiz)
        fabAddQuiz.setOnClickListener {
            val intent = Intent(requireContext(), AddquizActivity::class.java)
            startActivity(intent)
        }

        // Find the sign-out button
        val signOutButton: Button = view.findViewById(R.id.signoutButton)
        signOutButton.setOnClickListener {
            // Sign out the user and navigating to Signup page.
            firebaseAuth.signOut()
            val intent = Intent(requireContext(), SignupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }

        return view
    }
}


/**
 * REFERENCES :
 *  https://stackoverflow.com/questions/42571618/how-to-make-a-user-sign-out-in-firebase
 */

