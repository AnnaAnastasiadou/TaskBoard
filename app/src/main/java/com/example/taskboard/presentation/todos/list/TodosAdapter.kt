package com.example.taskboard.presentation.todos.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskboard.R
import com.example.taskboard.domain.model.Post
import com.example.taskboard.domain.model.Todo
import com.example.taskboard.presentation.posts.list.PostsAdapter
import com.example.taskboard.presentation.posts.list.PostsAdapter.HeaderViewHolder
import com.example.taskboard.presentation.posts.list.PostsAdapter.PostsViewHolder

class TodosAdapter(
    private var items: List<Any>,
    private var toggleTodoStatus: (Int) -> Unit,
    private var onTodoClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_TODO = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is String) TYPE_HEADER else TYPE_TODO
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is HeaderViewHolder -> holder.bind(item as String)
            is TodosViewHolder -> holder.bind(
                item as Todo,
                onTodoClick,
                toggleTodoStatus
            )
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.tv_header_title)
        fun bind(text: String) {title.text = text}
    }

    class TodosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val todoTitle: TextView = itemView.findViewById(R.id.tvTodoTitle)
        val checkbox: CheckBox = itemView.findViewById(R.id.cbTodoStatus)
        fun bind(todo: Todo, onTodoClick: (Int) -> Unit, toggleTodoStatus: (Int) -> Unit) {
            todoTitle.text = todo.title
            checkbox.isChecked = todo.completed

            todoTitle.setOnClickListener {
                onTodoClick(todo.id)
            }

            checkbox.setOnClickListener {
                toggleTodoStatus(todo.id)
                checkbox.isChecked = todo.completed
            }

            itemView.setOnClickListener { onTodoClick(todo.id) }
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
                val view = inflater.inflate(R.layout.item_todo, parent, false)
                TodosViewHolder(view)
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