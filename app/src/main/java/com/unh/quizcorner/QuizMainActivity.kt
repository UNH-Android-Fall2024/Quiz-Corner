package com.unh.quizcorner

/**
 * The QuizMainActivity file is the landing page for the user inn order to attempt a quiz.
 * The file displays all the quizzes that are in firestore database on to the screen.
 */

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unh.quizcorner.databinding.ActivityQuizMainBinding
class QuizMainActivity : AppCompatActivity() {

    lateinit var binding: ActivityQuizMainBinding
    lateinit var quizModelList: MutableList<QuizModel>
    lateinit var adapter: QuizListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizModelList = mutableListOf()
        getDataFromFirebase()
    }

    /**
     * serach bar functionality
     */

    private fun getDataFromFirebase() {
        val db = FirebaseFirestore.getInstance()
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        quizModelList.clear()

        // Fetch quizzes created by the current user (both public and private)
        db.collection("users")
            .document(currentUserEmail)
            .collection("createdQuizzes") // Fetch from createdQuizzes sub-collection
            .get()
            .addOnSuccessListener { quizDocuments ->
                // Add user's created quizzes to the list
                for (quizDocument in quizDocuments) {
                    val quizId = quizDocument.id
                    val title = quizDocument.getString("title") ?: ""
                    val subtitle = quizDocument.getString("subtitle") ?: ""
                    val time = quizDocument.getString("time") ?: ""
                    val visibility = quizDocument.getString("visibility") ?: ""
                    val questionList = mutableListOf<QuestionModel>()

                    // Fetch questions for each quiz
                    db.collection("quizzes").document(quizId).collection("questions").get()
                        .addOnSuccessListener { questionDocuments ->
                            for (questionDocument in questionDocuments) {
                                val question = questionDocument.getString("question") ?: ""
                                val options = questionDocument.get("options") as? List<String> ?: listOf()
                                val correct = questionDocument.getString("correct") ?: ""
                                val questionModel = QuestionModel(question, options, correct)
                                questionList.add(questionModel)
                            }

                            // Add quiz to the list after fetching questions
                            quizModelList.add(QuizModel(quizId, title, subtitle, time, questionList, visibility))
                            setupRecyclerview()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error fetching questions for quiz: $quizId", e)
                        }
                }

                // Also fetch public quizzes from the "quizzes" collection
                db.collection("quizzes")
                    .whereEqualTo("visibility", "public")
                    .get()
                    .addOnSuccessListener { quizDocuments ->
                        for (quizDocument in quizDocuments) {
                            val quizId = quizDocument.id
                            val title = quizDocument.getString("title") ?: ""
                            val subtitle = quizDocument.getString("subtitle") ?: ""
                            val time = quizDocument.getString("time") ?: ""
                            val visibility = quizDocument.getString("visibility") ?: ""
                            val questionList = mutableListOf<QuestionModel>()

                            // Fetch questions for each quiz
                            db.collection("quizzes").document(quizId).collection("questions").get()
                                .addOnSuccessListener { questionDocuments ->
                                    for (questionDocument in questionDocuments) {
                                        val question = questionDocument.getString("question") ?: ""
                                        val options = questionDocument.get("options") as? List<String> ?: listOf()
                                        val correct = questionDocument.getString("correct") ?: ""
                                        val questionModel = QuestionModel(question, options, correct)
                                        questionList.add(questionModel)
                                    }

                                    // Add quiz to the list after fetching questions
                                    quizModelList.add(QuizModel(quizId, title, subtitle, time, questionList, visibility))
                                    setupRecyclerview()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Error fetching questions for quiz: $quizId", e)
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error fetching public quizzes", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching created quizzes", e)
            }
    }






    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerview() {
        if (!::adapter.isInitialized) {
            adapter = QuizListAdapter(quizModelList)
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter
        } else {
            adapter.notifyDataSetChanged()
        }
    }
}




/**
 * REFERENCES ::
 *
 * https://firebase.google.com/docs/database/android/read-and-write
 * https://www.youtube.com/watch?v=EMM_3Wld2jU
 * https://www.geeksforgeeks.org/how-to-retrieve-data-from-the-firebase-realtime-database-in-android/
 */