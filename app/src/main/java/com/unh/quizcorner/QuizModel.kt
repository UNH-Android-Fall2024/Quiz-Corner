package com.unh.quizcorner


data class QuizModel(

    val id : String,
    val title: String,
    val Subtitle : String,
    val time: String,
){
    constructor(): this("","","","")
}
