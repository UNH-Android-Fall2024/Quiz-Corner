package com.unh.quizcorner

/**
 * The QuizMainActivity file is the landing page for the user inn order to attempt a quiz.
 * The file displays all the quizzes that are in firestore database on to the screen.
 *
 * We're taking a list and adding the private quizzes first and public quizzes next to that list.
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
     * Retrieving data from firebase . Both public and private quizzes.
     */
    private fun getDataFromFirebase() {
        val db = FirebaseFirestore.getInstance()
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        quizModelList.clear()

        /**
         * Extracting the private quizzes with the help of Email from the user.
         */
        db.collection("users")
            .document(currentUserEmail)
            .collection("createdQuizzes")
            .get()
            .addOnSuccessListener { quizDocuments ->
                for (quizDocument in quizDocuments) {
                    val quizId = quizDocument.id
                    val title = quizDocument.getString("title") ?: ""
                    val subtitle = quizDocument.getString("subtitle") ?: ""
                    val time = quizDocument.getString("time") ?: ""
                    val visibility = quizDocument.getString("visibility") ?: ""
                    val questionList = mutableListOf<QuestionModel>()

                    db.collection("quizzes").document(quizId).collection("questions").get()
                        .addOnSuccessListener { questionDocuments ->
                            for (questionDocument in questionDocuments) {
                                val question = questionDocument.getString("question") ?: ""
                                val options = questionDocument.get("options") as? List<String> ?: listOf()
                                val correct = questionDocument.getString("correct") ?: ""
                                val questionModel = QuestionModel(question, options, correct)
                                questionList.add(questionModel)
                            }

                            quizModelList.add(QuizModel(quizId, title, subtitle, time, questionList, visibility))
                            setupRecyclerview()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error fetching questions for quiz: $quizId", e)
                        }
                }

                /**
                 * Adding public quizzes to the list which already have the private quizzes from above code:
                 */
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

                            db.collection("quizzes").document(quizId).collection("questions").get()
                                .addOnSuccessListener { questionDocuments ->
                                    for (questionDocument in questionDocuments) {
                                        val question = questionDocument.getString("question") ?: ""
                                        val options = questionDocument.get("options") as? List<String> ?: listOf()
                                        val correct = questionDocument.getString("correct") ?: ""
                                        val questionModel = QuestionModel(question, options, correct)
                                        questionList.add(questionModel)
                                    }
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
    /**
     * Recycler view regarding the question list
     */
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
 * https://stackoverflow.com/questions/46573014/firestore-query-subcollections
 */