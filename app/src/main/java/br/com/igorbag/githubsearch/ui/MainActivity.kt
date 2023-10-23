package br.com.igorbag.githubsearch.ui

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var userName: EditText
    lateinit var confirmButton: Button
    lateinit var repositoriesList: RecyclerView
    lateinit var githubApi: GitHubService
    lateinit var loading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setupView()
        showUserName()
        setupRetrofit()
        setupListeners()
    }

    private fun setupView() { // Method responsible for setting up the view and retrieving the layout IDs
        userName = findViewById(R.id.user_name)
        confirmButton = findViewById(R.id.search_btn)
        repositoriesList = findViewById(R.id.repositories_list)
        loading = findViewById(R.id.loading)
    }

    private fun setupRetrofit() { // Method responsible for configuring the base setup of Retrofit.

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        githubApi = retrofit.create(GitHubService::class.java)
    }

    private fun setupListeners() { // Method responsible for setting up click listeners for the screen.
        confirmButton.setOnClickListener {

            val nomePesquisar = userName.text.toString()
            getAllReposByUserName(nomePesquisar)
            saveUserLocal()
            repositoriesList.isVisible = false
        }
    }

    private fun getAllReposByUserName(userName: String) {
        // Method responsible for fetching all repositories of the provided user

        if (userName.isNotEmpty()) {

            loading.isVisible = true

            githubApi.getAllRepositoriesByUser(userName)
                .enqueue(object : Callback<List<Repository>> {

                    override fun onResponse(
                        call: Call<List<Repository>>,
                        response: Response<List<Repository>>,
                    ) {
                        if (response.isSuccessful) {

                            loading.isVisible = false
                            repositoriesList.isVisible = true

                            val repositories = response.body()

                            repositories?.let {
                                setupAdapter(repositories)
                            }

                        } else {

                            loading.isVisible = false

                            val context = applicationContext
                            Toast.makeText(context, R.string.response_error, Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<List<Repository>>, t: Throwable) {

                        loading.isVisible = false

                        val context = applicationContext
                        Toast.makeText(context, R.string.response_error, Toast.LENGTH_LONG).show()
                    }

                })
        }
    }

    fun setupAdapter(list: List<Repository>) { // Method responsible for configuring the adapter

        val adapter = RepositoryAdapter(
            this, list
        )

        repositoriesList.adapter = adapter
    }

    private fun saveUserLocal() {
        // Method responsible for saving the user entered in the EditText using SharedPreferences.

        val providedUser = userName.text.toString()

        val sharedPreference = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPreference.edit()) {
            putString("saved_username", providedUser)
            apply()
        }
    }

    private fun showUserName() { // Method that displays what was saved in SharedPreferences

        val sharedPreference = getPreferences(MODE_PRIVATE) ?: return
        val lastSearchedUser = sharedPreference.getString("saved_username", null)

        if (!lastSearchedUser.isNullOrEmpty()) {
            userName.setText(lastSearchedUser)
        }
    }
}
