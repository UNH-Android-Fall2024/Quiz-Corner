package com.unh.quizcorner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.unh.quizcorner.databinding.ActivityMainBinding
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
        val totalQuizzes = db.collection("quizzes").get().addOnSuccessListener { quizDocuments ->
            var quizCounter = 0
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

                        val quizModel = QuizModel(quizId, title, subtitle, time, questionList)
                        quizModelList.add(quizModel)
                        quizCounter++

                        // When all quizzes are fetched, update the adapter
                        if (quizCounter == quizDocuments.size()) {
                            if (!::adapter.isInitialized) {
                                setupRecyclerview()
                            } else {
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error fetching questions: ", e)
                    }
            }
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Error fetching quizzes: ", e)
        }
    }


    private fun setupRecyclerview(){
        adapter = QuizListAdapter(quizModelList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

}