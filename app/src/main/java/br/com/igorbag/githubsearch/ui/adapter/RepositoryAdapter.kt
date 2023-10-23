package br.com.igorbag.githubsearch.ui.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(
    private val context: Context,
    private val repositories: List<Repository>,
) : RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder { // Creates a new view
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) { // Gets the content of the view and replaces it with the information from an item in a list

        val repository = repositories[position]

        holder.repositoryCard.setOnClickListener {
            openBrowser(context, repository.htmlUrl)
        }

        holder.repositoryName.text = repository.name

        holder.shareButton.setOnClickListener {
            shareRepositoryLink(context, repository.htmlUrl)
        }
    }

    override fun getItemCount(): Int =
        repositories.size // Gets the quantity of repositories from the list

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) { // Connects elements with the layout

        val repositoryCard: CardView
        val repositoryName: TextView
        val shareButton: ImageView

        init {
            view.apply {
                repositoryCard = findViewById(R.id.cv_repository)
                repositoryName = findViewById(R.id.tv_repository_name)
                shareButton = findViewById(R.id.iv_share)
            }
        }

    }

    private fun shareRepositoryLink(
        context: Context,
        urlRepository: String,
    ) { // Method responsible for sharing the link of the selected repository
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    private fun openBrowser(
        context: Context,
        urlRepository: String,
    ) { // Method responsible for opening the browser with the provided repository link
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )
    }
}
