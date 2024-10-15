package com.unh.quizcorner

// blueprint of recycler view.
data class QuizModel(

    val id : String,
    val title: String,
    val Subtitle : String,
    val time: String,
){
    constructor(): this("","","","")
}
