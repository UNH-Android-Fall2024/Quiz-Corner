package com.unh.quizcorner

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

        // Setup RecyclerView
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
            questionsList.add(newQuestion) // Add to questions list
            questionsAdapter.notifyItemInserted(questionsList.size - 1) // Notify adapter

            // Clear input fields
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

    private fun createQuiz() {
        val quizId = binding.quizId.text.toString()
        val quizTitle = binding.quizTitle.text.toString()
        val quizSubtitle = binding.quizSubtitle.text.toString()
        val quizTime = binding.quizTime.text.toString().toLongOrNull()

        if (quizId.isNotEmpty() && quizTitle.isNotEmpty() && quizSubtitle.isNotEmpty() &&
            quizTime != null && questionsList.isNotEmpty()) {

            val newQuiz = mapOf(
                "id" to quizId,
                "title" to quizTitle,
                "subtitle" to quizSubtitle,
                "time" to quizTime.toString()
            )

            // Referencing to the 'quizzes' collection
            val quizFirst = firestore.collection("quizzes").document(quizId)

            val db = FirebaseFirestore.getInstance()
            val userId = firebaseAuth.currentUser?.uid ?: return

            // Adding the quiz data
            quizFirst.set(newQuiz)
                .addOnSuccessListener {
                    // Add questions to the sub-collection
                    for ((index, question) in questionsList.withIndex()) {
                        val questionData = mapOf(
                            "question" to question.question,
                            "options" to question.options,
                            "correct" to question.correct
                        )
                        quizFirst.collection("questions").document("question_$index").set(questionData)
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

            val userQuizData = mapOf("title" to quizTitle, "id" to quizId)
            db.collection("users").document(userId)
                .collection("createdQuizzes")
                .document(quizId)
                .set(userQuizData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Quiz added to user's collection!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update user's collection: ${e.message}", Toast.LENGTH_SHORT).show()
                }


        } else {
            Toast.makeText(this, "Please fill all fields and add questions", Toast.LENGTH_SHORT).show()
        }
    }



}
