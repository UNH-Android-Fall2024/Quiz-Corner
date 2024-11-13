package com.unh.quizcorner

/**
 * This is a data class which is basically the blueprint of the firestore database .
 * QuizModel is the blueprint of the Quiz,
 * QuestionModel is the blueprint of the questions from the Quiz.
 */
data class QuizModel( // Data class representing the structure of a quiz

    val id : String, // Unique identifier for the quiz
    val title: String, // Title of the quiz
    val Subtitle : String, // Subtitle providing additional information about the quiz
    val time: String, // Time duration for the quiz
    val questionList : List<QuestionModel> // List of questions in the quiz, each represented by a QuestionModel
){
    // Secondary constructor to allow creation of an empty `QuizModel` instance
    constructor(): this("","","","", emptyList())
}


data class QuestionModel( // Data class representing the structure of a question
    val question :String, // Text of the question
    val options : List<String>, // List of answer options for the question
    val correct: String, // List of answer options for the question
){
    // Secondary constructor to allow creation of an empty `QuestionModel` instance
    constructor(): this("", emptyList(),"")
}