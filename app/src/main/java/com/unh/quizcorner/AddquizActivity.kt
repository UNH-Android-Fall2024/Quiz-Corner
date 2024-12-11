package com.unh.quizcorner

/**
 * This Activity file as name indicates , demonstrates the Add Quiz Functinality.
 * User can add a quiz here .
 */
import android.R.id
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unh.quizcorner.databinding.ActivityAddquizBinding

class AddquizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddquizBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()
    private val questionsList = mutableListOf<QuestionModel>() // List to hold added questions
    private lateinit var questionsAdapter: UserQuestionsAdapter // Adapter for RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddquizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Setting up RecyclerView
        val recyclerView: RecyclerView = binding.questionsRecyclerView
        questionsAdapter = UserQuestionsAdapter(questionsList)
        recyclerView.adapter = questionsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Adding question button functionality
        binding.addQuestionBtn.setOnClickListener {
            addQuestion()
        }

        // Creating quiz button functionality
        binding.pushQuizBtn.setOnClickListener {
            createQuiz()
        }
    }

    private fun addQuestion() {
        val questionText = binding.questionText.text.toString()
        val optionA = binding.optionA.text.toString()
        val optionB = binding.optionB.text.toString()
        val optionC = binding.optionC.text.toString()
        val optionD = binding.optionD.text.toString()
        val correctAnswer = binding.correctAns.text.toString()

        if (questionText.isNotEmpty() && optionA.isNotEmpty() && optionB.isNotEmpty() &&
            optionC.isNotEmpty() && optionD.isNotEmpty() && correctAnswer.isNotEmpty()) {

            val newQuestion = QuestionModel(questionText, listOf(optionA, optionB, optionC, optionD), correctAnswer)
            questionsList.add(newQuestion)
            questionsAdapter.notifyItemInserted(questionsList.size - 1) // Notifying adapter
            // Clearing input fields after submission.
            binding.questionText.text.clear()
            binding.optionA.text.clear()
            binding.optionB.text.clear()
            binding.optionC.text.clear()
            binding.optionD.text.clear()
            binding.correctAns.text.clear()
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    // Below function relates to Creating the Quiz functionality
    private fun createQuiz() {
        val quizId = binding.quizId.text.toString()
        val quizTitle = binding.quizTitle.text.toString()
        val quizSubtitle = binding.quizSubtitle.text.toString()
        val quizTime = binding.quizTime.text.toString().toLongOrNull()
        val isPublic = binding.privacyToggle.isChecked // Checking the toggle state

        if (quizId.isNotEmpty() && quizTitle.isNotEmpty() && quizSubtitle.isNotEmpty() &&
            quizTime != null && questionsList.isNotEmpty()) {

            val newQuiz = mapOf(
                "id" to quizId,
                "title" to quizTitle,
                "subtitle" to quizSubtitle,
                "time" to quizTime.toString(),
                "creator" to firebaseAuth.currentUser?.email, // Adding the creator's email (to pullup private quizzes)
                "visibility" to if (isPublic) "public" else "private" // Set visibility based on toggle
            )

            val firestore = FirebaseFirestore.getInstance()
            val currentUser = firebaseAuth.currentUser
            val userEmail = currentUser?.email ?: return

            // Saving the quiz to the main "quizzes" collection
            val quizRef = firestore.collection("quizzes").document(quizId)

            // Adding quiz data to Firestore database.
            quizRef.set(newQuiz)
                .addOnSuccessListener {
                    firestore.collection("users")
                        .document(userEmail) // Document for the current user
                        .collection("createdQuizzes")
                        .document(quizId) // Using quizId as document ID
                        .set(newQuiz) // Saving the quiz to the user's createdQuizzes collection

                    // Adding questions to the sub-collection (this will be displayed at the bottom)
                    for ((index, question) in questionsList.withIndex()) {
                        val questionData = mapOf(
                            "question" to question.question,
                            "options" to question.options,
                            "correct" to question.correct
                        )
                        quizRef.collection("questions").document("question_$index").set(questionData)
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error adding question $index: ${e.message}")
                            }
                    }

                    Toast.makeText(this, "Quiz created successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error creating quiz: ${e.message}", Toast.LENGTH_SHORT).show()
                }

        } else {
            Toast.makeText(this, "Please fill all fields and add questions", Toast.LENGTH_SHORT).show()
        }
    }
}
