package com.unh.quizcorner

/**
 * The QuizMainActivity file is the landing page for the user inn order to attempt a quiz.
 * The file displays all the quizzes that are in firestore database on to the screen.
 */

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

    private fun getDataFromFirebase() {
        val db = FirebaseFirestore.getInstance()
        quizModelList.clear()

        // Fetch user's quizzes
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Fetch quizzes created by the user
        db.collection("users").document(userId).collection("quizzes").get()
            .addOnSuccessListener { userQuizDocuments ->
                var totalQuizzes = userQuizDocuments.size()
                var quizCounter = 0

                // Loop through each user quiz document
                for (quizDocument in userQuizDocuments) {
                    val quizId = quizDocument.id
                    val title = quizDocument.getString("title") ?: ""
                    val subtitle = quizDocument.getString("subtitle") ?: ""
                    val time = quizDocument.getString("time") ?: ""
                    val questionList = mutableListOf<QuestionModel>()

                    // Fetch questions for the user's quiz
                    db.collection("users").document(userId).collection("quizzes").document(quizId).collection("questions").get()
                        .addOnSuccessListener { questionDocuments ->
                            for (questionDocument in questionDocuments) {
                                val question = questionDocument.getString("question") ?: ""
                                val options = questionDocument.get("options") as? List<String> ?: listOf()
                                val correct = questionDocument.getString("correct") ?: ""
                                val questionModel = QuestionModel(question, options, correct)
                                questionList.add(questionModel)
                            }

                            // Add quiz to the list after fetching questions
                            quizModelList.add(QuizModel(quizId, title, subtitle, time, questionList))
                            quizCounter++

                            // Check if all user's quizzes are fetched
                            if (quizCounter == totalQuizzes) {
                                // Now fetch public quizzes
                                fetchPublicQuizzes()
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error fetching questions for user's quiz: $quizId", e)
                        }
                }

                // If the user has no quizzes, directly fetch public quizzes
                if (totalQuizzes == 0) {
                    fetchPublicQuizzes()
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching user's quizzes", e)
            }
    }

    private fun fetchPublicQuizzes() {
        val db = FirebaseFirestore.getInstance()

        // Fetch public quizzes
        db.collection("quizzes").get()
            .addOnSuccessListener { publicQuizDocuments ->
                for (publicQuizDocument in publicQuizDocuments) {
                    val quizId = publicQuizDocument.id
                    val title = publicQuizDocument.getString("title") ?: ""
                    val subtitle = publicQuizDocument.getString("subtitle") ?: ""
                    val time = publicQuizDocument.getString("time") ?: ""
                    val questionList = mutableListOf<QuestionModel>()

                    // Fetch questions for the public quiz
                    db.collection("quizzes").document(quizId).collection("questions").get()
                        .addOnSuccessListener { questionDocuments ->
                            for (questionDocument in questionDocuments) {
                                val question = questionDocument.getString("question") ?: ""
                                val options = questionDocument.get("options") as? List<String> ?: listOf()
                                val correct = questionDocument.getString("correct") ?: ""
                                val questionModel = QuestionModel(question, options, correct)
                                questionList.add(questionModel)
                            }

                            // Add public quiz to the list after fetching questions
                            quizModelList.add(QuizModel(quizId, title, subtitle, time, questionList))
                            setupRecyclerview()  // Set up RecyclerView once public quizzes are added
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error fetching questions for public quiz: $quizId", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching public quizzes", e)
            }
    }

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