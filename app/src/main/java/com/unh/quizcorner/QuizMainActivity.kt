package com.unh.quizcorner

/**
 * This is the Main page of the Quizzes . All the quizzes will be dooisplayed here .
 * User can search a quiz and attempt a quiz here .
 * This kt file has the function to pull the quizzes from firebase ( both public and private)
 */
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unh.quizcorner.databinding.ActivityQuizMainBinding

class QuizMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizMainBinding
    private lateinit var quizModelList: MutableList<QuizModel>
    private lateinit var filteredQuizList: MutableList<QuizModel> // For search functionality
    private lateinit var adapter: QuizListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizModelList = mutableListOf()
        filteredQuizList = mutableListOf()

        setupRecyclerView()
        setupSearchView()
        getDataFromFirebase()
    }

    /**
     * Sets up the RecyclerView with the filtered quiz list.
     */
    private fun setupRecyclerView() {
        adapter = QuizListAdapter(filteredQuizList) // Use the filtered list in the adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    /**
     * Configures the SearchView to filter quizzes based on user input.
     */
    private fun setupSearchView() {
        binding.searchBar.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterQuizzes(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterQuizzes(newText)
                return true
            }
        })
    }

    /**
     * Filters the quiz list based on the search query.
     */
    private fun filterQuizzes(query: String?) {
        if (query.isNullOrEmpty()) {
            // If query is empty, show all quizzes
            filteredQuizList.clear()
            filteredQuizList.addAll(quizModelList)
        } else {
            val lowerCaseQuery = query.lowercase()
            filteredQuizList.clear()
            filteredQuizList.addAll(quizModelList.filter { quiz ->
                quiz.title.lowercase().contains(lowerCaseQuery) ||
                        quiz.subtitle.lowercase().contains(lowerCaseQuery)
            })
        }
        adapter.notifyDataSetChanged() // Notify the adapter to update the UI
    }

    /**
     * Fetches quiz data from Firebase Firestore.
     */
    private fun getDataFromFirebase() {
        val db = FirebaseFirestore.getInstance()
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        quizModelList.clear()

        // Fetch quizzes created by the current user
        db.collection("users")
            .document(currentUserEmail)
            .collection("createdQuizzes")
            .get()
            .addOnSuccessListener { quizDocuments ->
                for (quizDocument in quizDocuments) {
                    val quizId = quizDocument.id
                    val title = quizDocument.getString("title") ?: ""
                    val subtitle = quizDocument.getString("subtitle") ?: ""
                    val time = quizDocument.getString("time") ?: ""
                    val visibility = quizDocument.getString("visibility") ?: ""
                    val questionList = mutableListOf<QuestionModel>()

                    // Fetch questions for each quiz
                    db.collection("quizzes").document(quizId).collection("questions").get()
                        .addOnSuccessListener { questionDocuments ->
                            for (questionDocument in questionDocuments) {
                                val question = questionDocument.getString("question") ?: ""
                                val options = questionDocument.get("options") as? List<String> ?: listOf()
                                val correct = questionDocument.getString("correct") ?: ""
                                val questionModel = QuestionModel(question, options, correct)
                                questionList.add(questionModel)
                            }

                            // Add quiz to the list after fetching questions
                            quizModelList.add(QuizModel(quizId, title, subtitle, time, questionList, visibility))
                            updateFilteredList() // Update filtered list to display all quizzes initially
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error fetching questions for quiz: $quizId", e)
                        }
                }

                // Fetch public quizzes from the "quizzes" collection
                db.collection("quizzes")
                    .whereEqualTo("visibility", "public")
                    .get()
                    .addOnSuccessListener { quizDocuments ->
                        for (quizDocument in quizDocuments) {
                            val quizId = quizDocument.id
                            val title = quizDocument.getString("title") ?: ""
                            val subtitle = quizDocument.getString("subtitle") ?: ""
                            val time = quizDocument.getString("time") ?: ""
                            val visibility = quizDocument.getString("visibility") ?: ""
                            val questionList = mutableListOf<QuestionModel>()

                            // Fetch questions for each quiz
                            db.collection("quizzes").document(quizId).collection("questions").get()
                                .addOnSuccessListener { questionDocuments ->
                                    for (questionDocument in questionDocuments) {
                                        val question = questionDocument.getString("question") ?: ""
                                        val options = questionDocument.get("options") as? List<String> ?: listOf()
                                        val correct = questionDocument.getString("correct") ?: ""
                                        val questionModel = QuestionModel(question, options, correct)
                                        questionList.add(questionModel)
                                    }

                                    // Add quiz to the list after fetching questions
                                    quizModelList.add(QuizModel(quizId, title, subtitle, time, questionList, visibility))
                                    updateFilteredList()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Error fetching questions for quiz: $quizId", e)
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error fetching public quizzes", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching created quizzes", e)
            }
    }



    /**
     * Updates the filtered list to show all quizzes initially.
     */
    private fun updateFilteredList() {
        filteredQuizList.clear()
        filteredQuizList.addAll(quizModelList)
        adapter.notifyDataSetChanged()
    }
}
