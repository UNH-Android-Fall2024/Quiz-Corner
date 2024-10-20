package com.unh.quizcorner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
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

    private fun setupRecyclerview(){
        adapter = QuizListAdapter(quizModelList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun getDataFromFirebase(){
        // dummy data

        val listQuestionModel = mutableListOf<QuestionModel>()
        listQuestionModel.add(QuestionModel("What is Android ? ", mutableListOf("language","OS","Product","None"), correct = "OS"))
        listQuestionModel.add(QuestionModel("Who owns  Android ? ", mutableListOf("Apple","MS","Kotlin","Google"), correct = "Google"))

        listQuestionModel.add(QuestionModel("Who owns  Android ? ", mutableListOf("Apple","MS","Kotlin","Google"), correct = "Google"))


        quizModelList.add(QuizModel("1","Programming","Basic programming","10",listQuestionModel))
//        quizModelList.add(QuizModel("2","Science","Science topics","10"))
//        quizModelList.add(QuizModel("3","History","Topics of History","10"))
        setupRecyclerview()
    }

}