package com.unh.quizcorner


/**
 * The QuizActivity file demonstrates Quizzes being displayed and results being processed ,
 * after user submits a quiz .
 */
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.unh.quizcorner.databinding.ActivityQuizBinding
import com.unh.quizcorner.databinding.ScoreDialogBinding

class QuizActivity : AppCompatActivity(),View.OnClickListener {

    companion object {
        var questionModelList : List<QuestionModel> = listOf()
        var time : String = ""
    }

    lateinit var binding:ActivityQuizBinding

    var currentQuestionIndex = 0;
    var selectedAnswer = ""
    var score = 0;

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
     `* Below function describes the timer that is being displayed when user starts a quiz.
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
        if (currentQuestionIndex >= questionModelList.size) {
            finishQuiz()
            return
        }

        selectedAnswer = "" // Reset the selected answer for the new question

        binding.apply {
            questionIndicator.text = "Question ${currentQuestionIndex+1}/ ${questionModelList.size}"

            questionProgressIndicator.progress =
                (currentQuestionIndex.toFloat()/ questionModelList.size.toFloat() * 100).toInt()

            // Question
            questionTextview.text = questionModelList[currentQuestionIndex].question

            // Options
            btn0.text = questionModelList[currentQuestionIndex].options[0]
            btn1.text = questionModelList[currentQuestionIndex].options[1]
            btn2.text = questionModelList[currentQuestionIndex].options[2]
            btn3.text = questionModelList[currentQuestionIndex].options[3]


        }
    }

    override fun onClick(view: View?) {

        // changing the color of buttons when clicked .
        binding.apply {
            btn0.setBackgroundColor(getColor(R.color.lightBlue))
            btn1.setBackgroundColor(getColor(R.color.lightBlue))
            btn2.setBackgroundColor(getColor(R.color.lightBlue))
            btn3.setBackgroundColor(getColor(R.color.lightBlue))
        }


        val clickedBtn = view as Button
        if(clickedBtn.id==R.id.next_btn){
            // Next button is clicked

            if(selectedAnswer.isEmpty()){
                Toast.makeText(applicationContext, "Please Select an answer to proceed !", Toast.LENGTH_SHORT).show()
                return;
            }

            if(selectedAnswer == questionModelList[currentQuestionIndex].correct){
                score++
            }

            currentQuestionIndex++
            loadQuestions()

        }else {
            // Options are clicked
            clickedBtn.setBackgroundColor(getColor(R.color.lightGreen))

            selectedAnswer = clickedBtn.text.toString()
        }
    }

    /**
     * The method finishQuiz() defines/includes the functionality to display Dialog section after user completes the quiz .
     *
     */

    private fun finishQuiz() {
        // Check if the activity is finishing or if the quiz has already been finished
        if (isFinishing) return

        val totalQuestions = questionModelList.size
        val percentage = ((score.toFloat() / totalQuestions.toFloat()) * 100).toInt()

        val emoji = when (percentage) {
            in 10..40 -> "\uD83D\uDE1E" // Sad emoji
            in 41..79 -> "\uD83D\uDE42" // Simple smile emoji
            in 80..100 -> "\uD83D\uDD25" // Star lightning emoji
            else -> "\uD83D\uDE10" // Neutral face for unexpected range
        }

        val dialogBinding = ScoreDialogBinding.inflate(layoutInflater)
        dialogBinding.apply {
            scoreProgressCircle.progress = percentage
            scoreProgressText.text = "$percentage %"
            if (percentage > 60) {
                scoreTitle.text = "Congrats! You have passed the exam!"
                scoreTitle.setTextColor(Color.GREEN)
            } else {
                scoreTitle.text = "Oops! You have failed the exam"
                scoreTitle.setTextColor(Color.RED)
            }

            scoreResult.text = "$score out of $totalQuestions are correct!"
            resultEmojiText.text = "Emoji based on your Score = $emoji"

            finishButton.setOnClickListener {
                finish()
            }
        }

        // Show the dialog only if the activity is not finishing
        if (!isFinishing) {
            AlertDialog.Builder(this)
                .setView(dialogBinding.root)
                .setCancelable(false) // Prevent the user from dismissing the dialog
                .show()
        }
    }

}

/**
 * REFERENCES ::
 *
 *  https://stackoverflow.com/questions/52076779/kotlin-custom-dialog-in-android
 * https://stackoverflow.com/questions/54095875/how-to-create-a-simple-countdown-timer-in-kotlin
 * https://www.geeksforgeeks.org/progressbar-in-kotlin/
 * https://stackoverflow.com/questions/10398114/how-to-get-value-of-a-pressed-button
 * https://www.geeksforgeeks.org/android-recyclerview/
 */