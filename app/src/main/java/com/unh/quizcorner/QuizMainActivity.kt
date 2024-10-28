package com.unh.quizcorner

/**
 * The QuizMainActivity file is the landing page for the user inn order to attempt a quiz.
 * The file displays all the quizzes that are in firestore database on to the screen.
 */

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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

//    private fun setupRecyclerview(){
//        adapter = QuizListAdapter(quizModelList)
//        binding.recyclerView.layoutManager = LinearLayoutManager(this)
//        binding.recyclerView.adapter = adapter
//    }

//    private fun getDataFromFirebase(){
//        // dummy data
//
//        val listQuestionModel = mutableListOf<QuestionModel>()
//        listQuestionModel.add(QuestionModel("What is Android ? ", mutableListOf("language","OS","Product","None"), correct = "OS"))
//        listQuestionModel.add(QuestionModel("Who owns  Android ? ", mutableListOf("Apple","MS","Kotlin","Google"), correct = "Google"))
//        listQuestionModel.add(QuestionModel("Who owns  Android ? ", mutableListOf("Apple","MS","Kotlin","Google"), correct = "Google"))
//
//
//        quizModelList.add(QuizModel("1","Programming","Basic programming","10",listQuestionModel))
//        setupRecyclerview()
//    }

    private fun getDataFromFirebase() {
        val db = FirebaseFirestore.getInstance()
        quizModelList.clear()

        db.collection("quizzes").get()
            .addOnSuccessListener { quizDocuments ->
                val totalQuizzes = quizDocuments.size()
                var quizCounter = 0

                // Loop through each quiz document
                for (quizDocument in quizDocuments) {
                    val quizId = quizDocument.id
                    val title = quizDocument.getString("title") ?: ""
                    val subtitle = quizDocument.getString("subtitle") ?: ""
                    val time = quizDocument.getString("time") ?: ""
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

                            // Add quiz to the list after fetching questions
                            quizModelList.add(QuizModel(quizId, title, subtitle, time, questionList))
                            quizCounter++

                            // Check if all quizzes are fetched
                            if (quizCounter == totalQuizzes) {
                                setupRecyclerview()
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error fetching questions for quiz: $quizId", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching quizzes", e)
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