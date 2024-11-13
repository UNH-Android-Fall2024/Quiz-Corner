package com.unh.quizcorner

/**
 * This is the Main Activity file which has two fragments , Homefragment , ProfileFragment.
 * Basically, Bottom Navigation is defined in this Activity .
 */
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.unh.quizcorner.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // Declares a variable for binding the activity layout to use View Binding
    private  lateinit var binding: ActivityMainBinding
    // onCreate method, called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Calls the superclass's onCreate method
        binding = ActivityMainBinding.inflate(layoutInflater) // Inflates the layout using View Binding
        setContentView(binding.root) // Sets the root view of the binding as the content view
        replaceFragment(HomeFragment()) // Sets the initial fragment to HomeFragment
        // Sets an ItemSelectedListener on the bottom navigation to handle fragment switching
        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.home -> replaceFragment(HomeFragment()) // Switches to HomeFragment
                R.id.profile -> replaceFragment(ProfileFragment()) // Switches to ProfileFragment

                else -> {
                } // No action for other cases
            }
            true // Returns true to indicate successful handling of the selection
        }

    }
    // Helper function to replace the current fragment with the specified fragment
    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager // Gets the FragmentManager for handling fragments
        val fragmentTransaction = fragmentManager.beginTransaction() // Starts a new FragmentTransaction
        fragmentTransaction.replace(R.id.frame_layout,fragment) // Replaces the frame layout with the specified fragment
        fragmentTransaction.commit()  // Commits the transaction to apply the change
    }
}


/**
 * REFERENCES :
 *
 * Bottom Navigation : https://www.geeksforgeeks.org/bottom-navigation-bar-in-android-using-kotlin/
 */