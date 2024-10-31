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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using view binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         *   Setting up the button click listener to navigate to QuizMainActivity
          */
        binding.navigateQuizButton.setOnClickListener {
            val intent = Intent(requireActivity(), QuizMainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

