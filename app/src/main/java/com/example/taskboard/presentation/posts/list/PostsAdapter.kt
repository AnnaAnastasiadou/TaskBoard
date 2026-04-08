package com.example.taskboard.presentation.posts.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskboard.R
import com.example.taskboard.domain.model.Post
import com.example.taskboard.presentation.common.formatDateTime
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class PostsAdapter(
    private var items: List<Any>,
    private val onPostClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_POST = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is String) TYPE_HEADER else TYPE_POST
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is HeaderViewHolder -> holder.bind(item as String)
            is PostsViewHolder -> holder.bind(item as Post, onPostClick)
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.tv_header_title)
        fun bind(text: String) {title.text = text}
    }
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

            tagGroup.removeAllViews()

            post.tags.forEach { tag ->
                val chip = Chip(itemView.context).apply {
                    text = tag
                    isClickable = false
                    isCheckable = false
                    isFocusable = false
                }
                tagGroup.addView(chip)
            }

            if (post.updatedAt != null) {
                editedText.visibility = View.VISIBLE
                updatedAt.text = formatDateTime(post.updatedAt)
            } else {
                editedText.visibility = View.GONE
            }

            itemView.setOnClickListener { onPostClick(post.id) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> {
                val view = inflater.inflate(R.layout.item_header, parent, false)
                HeaderViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_post, parent, false)
                PostsViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(newList: List<Any>) {
        this.items = newList
        notifyDataSetChanged()
    }
}