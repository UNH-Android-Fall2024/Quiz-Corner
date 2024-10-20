package com.unh.quizcorner

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.unh.quizcorner.databinding.ActivityQuizBinding

class QuizActivity : AppCompatActivity(),View.OnClickListener {

    companion object {
        var questionModelList : List<QuestionModel> = listOf()
        var time : String = ""
    }

    lateinit var binding:ActivityQuizBinding

    var currentQuestionIndex = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btn0.setOnClickListener(this@QuizActivity)
            btn1.setOnClickListener(this@QuizActivity)
            btn2.setOnClickListener(this@QuizActivity)
            btn3.setOnClickListener(this@QuizActivity)
            nextBtn.setOnClickListener(this@QuizActivity)
        }

        loadQuestions()
        startTimer()


    }

    /**
     * Below function describes the timer that is being displayed when user starts a quiz.
     */
    private fun startTimer(){
        val totalTimeInMillis = time.toInt() * 60 *1000L

        object : CountDownTimer(totalTimeInMillis, 1000L){
            @SuppressLint("DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val minutes = seconds/ 60
                val remainingSeconds = seconds % 60
                binding.timerIndicator.text = String.format("%02d:%02d", minutes,remainingSeconds)
            }

            override fun onFinish() {
                TODO("Not yet implemented")
            }

        }.start()
    }

    /**
     * In this method, Question count at the top is being displayed.
     */
    @SuppressLint("SetTextI18n")
    private fun loadQuestions(){
        binding.apply {
            questionIndicator.text = "Question ${currentQuestionIndex+1}/ ${questionModelList.size}"
            questionProgressIndicator.progress =
                (currentQuestionIndex.toFloat()/ questionModelList.size.toFloat() * 100).toInt()
            questionTextview.text = questionModelList[currentQuestionIndex].question

            btn0.text = questionModelList[currentQuestionIndex].options[0]
            btn1.text = questionModelList[currentQuestionIndex].options[1]
            btn2.text = questionModelList[currentQuestionIndex].options[2]
            btn3.text = questionModelList[currentQuestionIndex].options[3]


        }
    }

    override fun onClick(view: View?) {

        val clickedBtn = view as Button
        if(clickedBtn.id==R.id.next_btn){
            // NExt button is clicked
            currentQuestionIndex++
            loadQuestions()

        }else {
            // Options are clicked

            clickedBtn.setBackgroundColor(getColor(R.color.lightGreen))

        }
    }
}