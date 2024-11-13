package com.unh.quizcorner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
// Adapter class for displaying a list of user questions in a RecyclerView
class UserQuestionsAdapter(private val questions: List<QuestionModel>) : RecyclerView.Adapter<UserQuestionsAdapter.QuestionViewHolder>() {
    // ViewHolder class for caching views associated with each item in the list
    class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TextView for displaying the question text
        val questionTextView: TextView = itemView.findViewById(R.id.question_text_view)
        // TextView for displaying the options associated with the question
        val optionsTextView: TextView = itemView.findViewById(R.id.options_text_view)
    }
    // Creates and inflates a new ViewHolder when there are no existing ones to reuse
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        // Inflates the item_question layout for each item in the list
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
        // Returns a new ViewHolder instance with the inflated view
        return QuestionViewHolder(view)
    }
    // Binds data to the views in the ViewHolder for the item at the given position
    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        // Retrieves the question data for the current position
        val question = questions[position]
        // Sets the question text in the questionTextView
        holder.questionTextView.text = question.question
        // Joins options into a single string and sets it in optionsTextView
        holder.optionsTextView.text = question.options.joinToString(", ")
    }
    // Returns the total number of items in the list
    override fun getItemCount(): Int {
        return questions.size // Returns the size of the questions list
    }
}

