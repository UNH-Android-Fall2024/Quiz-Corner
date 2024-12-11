package com.unh.quizcorner

/**
 * This is a data class which is basically the blueprint of the firestore database .
 * QuizModel is the blueprint of the Quiz,
 * QuestionModel is the blueprint of the questions from the Quiz.
 */
data class QuizModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val time: String,
    val questionList: List<QuestionModel>,
    val visibility: String = "public",  // "public" or "private"
    val creator: String = ""  ,     // Email of the quiz creator
){
    constructor(): this("","","","", emptyList())
}


data class QuestionModel(
    val question :String,
    val options : List<String>,
    val correct: String,
){
    constructor(): this("", emptyList(),"")
}