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
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unh.quizcorner.databinding.ActivityQuizBinding
import com.unh.quizcorner.databinding.ScoreDialogBinding
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class QuizActivity : AppCompatActivity(),View.OnClickListener {

    companion object {
        var questionModelList : List<QuestionModel> = listOf()
        var time : String = ""
    }

    lateinit var binding:ActivityQuizBinding

    var currentQuestionIndex = 0;
    var selectedAnswer = ""
    var score = 0;
    var quizId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the quizId passed from the adapter
        quizId = intent.getStringExtra("quizId") ?: ""

        // Check if the quizId is null or empty
        if (quizId.isEmpty()) {
            Toast.makeText(this, "Quiz ID not found. Cannot submit rating.", Toast.LENGTH_SHORT).show()
            return
        }

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
        if (isFinishing) return

        val totalQuestions = questionModelList.size
        val percentage = ((score.toFloat() / totalQuestions.toFloat()) * 100).toInt()

        sendQuizCompletionNotification(score, totalQuestions)

        val emoji = when (percentage) {
            in 10..40 -> "\uD83D\uDE1E"
            in 41..79 -> "\uD83D\uDE42"
            in 80..100 -> "\uD83D\uDD25"
            else -> "\uD83D\uDE10"
        }

        val dialogBinding = ScoreDialogBinding.inflate(layoutInflater)
        dialogBinding.apply {
            scoreProgressCircle.progress = percentage
            scoreProgressText.text = "$percentage %"
            scoreTitle.text = if (percentage > 60) "Congrats! You passed!" else "Oops! You failed."
            scoreResult.text = "$score out of $totalQuestions are correct!"
            resultEmojiText.text = "Emoji based on your Score = $emoji"

            finishButton.setOnClickListener {
                finish()
            }

            // When the user changes the rating, submit it to Firebase
            ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
                submitRatingToFirebase(rating)
            }
        }

        if (!isFinishing) {
            AlertDialog.Builder(this)
                .setView(dialogBinding.root)
                .setCancelable(false)
                .show()
        }
    }

    private fun sendQuizCompletionNotification(score: Int, totalQuestions: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
                return
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "quiz_completion_channel"
            val channelName = "Quiz Completion Notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Notifications for quiz completion"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, "quiz_completion_channel")
            .setSmallIcon(R.drawable.logo_quiz) // replace with your icon
            .setContentTitle("Quiz Completed!")
            .setContentText("You have successfully completed your quiz with a score of $score/$totalQuestions.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(1, notification.build())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                sendQuizCompletionNotification(score, questionModelList.size)
            }
        }
    }

    private fun submitRatingToFirebase(userRating: Float) {
        val firestore = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not logged in. Cannot submit rating.", Toast.LENGTH_SHORT).show()
            return
        }

        val quizId = intent.getStringExtra("quizId")
        if (quizId.isNullOrEmpty()) {
            Toast.makeText(this, "Quiz ID not found. Cannot submit rating.", Toast.LENGTH_SHORT).show()
            return
        }

        // Create the rating data
        val ratingData = hashMapOf(
            "userId" to userId,
            "rating" to userRating
        )

        // Add the rating to the ratings subcollection
        firestore.collection("quizzes")
            .document(quizId)
            .collection("ratings")
            .add(ratingData)
            .addOnSuccessListener {
                Toast.makeText(this, "Rating submitted successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to submit rating: ${e.message}", Toast.LENGTH_SHORT).show()
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