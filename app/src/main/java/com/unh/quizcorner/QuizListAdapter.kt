package com.unh.quizcorner

/**
 * The QuizListAdapter demonstrates the adapter setup for recycler view
 */
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.unh.quizcorner.databinding.QuizItemRecyclerViewBinding

class QuizListAdapter(private val quizModelList: List<QuizModel>):
    RecyclerView.Adapter<QuizListAdapter.MyViewHolder>() {

    class MyViewHolder(private val binding: QuizItemRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: QuizModel){
            binding.apply {
                quizTitleText.text= model.title
                quizSubtitleText.text = model.Subtitle
                quizTimerText.text = model.time + "min"
                root.setOnClickListener {
                    val intent  = Intent(root.context,QuizActivity::class.java)

                    intent.putExtra("quizId", model.id)
                    intent.putExtra("time", model.time)

                    QuizActivity.questionModelList = model.questionList
                    QuizActivity.time = model.time

                    root.context.startActivity(intent)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = QuizItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return quizModelList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(quizModelList[position])
    }

}

/**
 * REFERENCES ::
 *
 * https://www.geeksforgeeks.org/android-recyclerview-in-kotlin/
 * https://androidknowledge.com/recyclerview-in-android-studio-using-kotlin/
 * https://www.geeksforgeeks.org/android-recyclerview/
 */