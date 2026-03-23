package com.example.taskboard.presentation.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskboard.R
import com.example.taskboard.domain.model.Post
import com.example.taskboard.presentation.formatDateTime
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class PostsAdapter(
    private var postsList: List<Post>,
    private val onPostClick: (Int) -> Unit
) : RecyclerView.Adapter<PostsAdapter.PostsViewHolder>() {
    class PostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.post_title)
        val bodyText: TextView = itemView.findViewById(R.id.post_body)
        val likesText: TextView = itemView.findViewById(R.id.tv_likes)
        val dislikesText: TextView = itemView.findViewById(R.id.tv_dislikes)
        val tagGroup: ChipGroup = itemView.findViewById(R.id.post_tags_group)
        val editedText: TextView = itemView.findViewById(R.id.tv_edited)
        val updatedAt: TextView = itemView.findViewById(R.id.updated_at)


        fun bind(post: Post, onPostClick: (Int) -> Unit) {
            titleText.text = post.title
            bodyText.text = post.body
            likesText.text = post.likes.toString()
            dislikesText.text = post.dislikes.toString()

            post.tags.forEach {tag ->
                val chip = Chip(itemView.context).apply {
                    text = tag
                    setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_LabelSmall)
                }
                tagGroup.addView(chip)
            }

            if(post.updatedAt != null) {
                editedText.visibility = View.VISIBLE
                updatedAt.text = formatDateTime(post.updatedAt)
            } else {
                editedText.visibility = View.GONE
            }

            itemView.setOnClickListener { onPostClick(post.id)}
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostsViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PostsViewHolder,
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