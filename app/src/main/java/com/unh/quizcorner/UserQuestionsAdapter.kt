package com.unh.quizcorner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserQuestionsAdapter(private val questions: List<QuestionModel>) : RecyclerView.Adapter<UserQuestionsAdapter.QuestionViewHolder>() {

    class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionTextView: TextView = itemView.findViewById(R.id.question_text_view)
        val optionsTextView: TextView = itemView.findViewById(R.id.options_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questions[position]
        holder.questionTextView.text = question.question
        holder.optionsTextView.text = question.options.joinToString(", ") // Join options into a single string
    }

    override fun getItemCount(): Int {
        return questions.size
    }
}

