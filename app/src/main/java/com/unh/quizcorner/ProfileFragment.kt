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
import com.google.firebase.firestore.FirebaseFirestore

// Defines ProfileFragment class inheriting from Fragment
class ProfileFragment : Fragment() {
    // Firebase Authentication instance for managing the current user session
    private lateinit var firebaseAuth: FirebaseAuth
    // Firestore instance to access Firestore database
    private lateinit var firestore: FirebaseFirestore

    // this is a profile section comment
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment from fragment_profile XML
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        // Initialize FirebaseAuth instance to manage authentication
        firebaseAuth = FirebaseAuth.getInstance()
        // Initialize Firestore instance to manage Firestore operations
        firestore = FirebaseFirestore.getInstance()
        // TextView to display the user's nickname on the profile screen
        val profileNameTextView: TextView = view.findViewById(R.id.profile_name)
        // Gets the currently logged-in Firebase user
        val currentUser: FirebaseUser? = firebaseAuth.currentUser

        // User name before @ from gmail is displayed.
        currentUser?.let {
            val userId = it.uid // Get the user ID
            // Access the Firestore collection "users" and fetch the document by user ID
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->// Success listener for document fetch
                    if (document != null) {
                        // Retrieves the "nickname" field from the document
                        val nickname = document.getString("nickname")
                        // Sets the welcome message with the user's nickname in TextView
                        profileNameTextView.text = ("Welcome , " + nickname)
                    }
                }
                .addOnFailureListener { e ->// Failure listener if document fetch fails
                    // Sets an error message if nickname could not be fetched
                    profileNameTextView.text = "Error fetching nickname"
                }
        }

        // this is a floating action button which takes user to Add Quiz Activity
        //where user can add a quiz if needed .
        val fabAddQuiz: FloatingActionButton = view.findViewById(R.id.fab_add_quiz)
        fabAddQuiz.setOnClickListener {
            // Creates an intent to start AddquizActivity
            val intent = Intent(requireContext(), AddquizActivity::class.java)
            // Starts the AddquizActivity
            startActivity(intent)
        }

        // Find the sign-out button
        val signOutButton: Button = view.findViewById(R.id.signoutButton)
        signOutButton.setOnClickListener {
            // Sign out the user and navigating to Signup page.
            firebaseAuth.signOut()
            // Creates an intent to start SignupActivity for re-login
            val intent = Intent(requireContext(), SignupActivity::class.java)
            // Clears the activity stack to prevent navigating back after logout
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Starts the SignupActivity
            startActivity(intent)
            // Closes the current activity
            activity?.finish()
        }
        // Returns the root view to display the fragment's layout
        return view
    }
}

// comment
/**
 * REFERENCES :
 *  https://stackoverflow.com/questions/42571618/how-to-make-a-user-sign-out-in-firebase
 */

