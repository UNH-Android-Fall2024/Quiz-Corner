package com.unh.quizcorner

/**
 * This Fragment as name indicates , is the Home fragment.
 * User will be navigated to this page after being Authenticated .
 * this fragment contains a Button which takes user to the QuizMainActivity page.
 */
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.unh.quizcorner.databinding.FragmentHomeBinding
// Defines HomeFragment class inheriting from Fragment
class HomeFragment : Fragment() {
    // View binding property, initialized as null and assigned in onCreateView
    private var _binding: FragmentHomeBinding? = null
    // Property to access the binding object safely, ensuring it's not null
    private val binding get() = _binding!!
    // Override onCreateView to inflate the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using the binding object and return the root view
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    // Override onViewCreated to set up view-related logic after the view has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         *   Setting up the button click listener to navigate to QuizMainActivity
          */
        binding.navigateQuizButton.setOnClickListener {
            // Create an Intent to start QuizMainActivity
            val intent = Intent(requireActivity(), QuizMainActivity::class.java)
            // Start the QuizMainActivity
            startActivity(intent)
        }
    }
    // Override onDestroyView to clean up the binding when the view is destroyed
    override fun onDestroyView() {
        // Clean up the binding object when the view is destroyed
        super.onDestroyView()
        _binding = null
    }
}

