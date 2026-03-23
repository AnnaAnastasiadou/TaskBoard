package com.example.taskboard.presentation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskboard.R
import com.example.taskboard.domain.model.Todo
import com.google.android.material.checkbox.MaterialCheckBox

class PendingTodosAdapter(
    private var todosList: List<Todo>,
    private val onCheckBoxClicked: (Int) -> Unit
) : RecyclerView.Adapter<PendingTodosAdapter.PendingTodosViewHolder>() {
    class PendingTodosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.tvTodoTitle)
        private val checkBox: MaterialCheckBox = itemView.findViewById(R.id.cbTodoStatus)

        fun bind(todo: Todo, onCheckBoxClicked: (Int) -> Unit) {
            titleText.text = todo.todo
            // When the card is reused by a different to-do it might appear checked/unchecked when it shouldn't
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = todo.completed
            checkBox.setOnClickListener {
                onCheckBoxClicked(todo.id)
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PendingTodosViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pending_todos, parent, false)
        return PendingTodosViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PendingTodosViewHolder,
        position: Int
    ) {
        holder.bind(todosList[position], onCheckBoxClicked)
    }

    override fun getItemCount(): Int = todosList.size

    fun updateData(newTodos: List<Todo>) {
        this.todosList = newTodos
        notifyDataSetChanged()
    }
}