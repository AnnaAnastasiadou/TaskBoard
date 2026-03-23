package com.example.taskboard.presentation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskboard.R
import com.example.taskboard.domain.model.Post

class RecentPostsAdapter(
    private var postsList: List<Post>,
    private val onPostClick: (Int) -> Unit
) : RecyclerView.Adapter<RecentPostsAdapter.RecentPostsViewHolder>() {
    class RecentPostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.post_title)
        val bodyText: TextView = itemView.findViewById(R.id.post_body)

        fun bind(post: Post, onPostClick: (Int) -> Unit) {
            titleText.text = post.title
            bodyText.text = post.body

            itemView.setOnClickListener { onPostClick(post.id)}
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentPostsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_post, parent, false)
        return RecentPostsViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecentPostsViewHolder,
        position: Int
    ) {
        holder.bind(postsList[position], onPostClick)
    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    fun updateData(newPostsList: List<Post>) {
        this.postsList = newPostsList
        notifyDataSetChanged()
    }
}