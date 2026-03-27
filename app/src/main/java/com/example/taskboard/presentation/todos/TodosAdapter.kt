package com.example.taskboard.presentation.todos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskboard.R
import com.example.taskboard.domain.model.Todo

class TodosAdapter(
    private var todosList: List<Todo>,
    private var onTodoClick: (Int) -> Unit
) : RecyclerView.Adapter<TodosAdapter.TodosViewHolder>() {
    class TodosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val todoTitle : TextView =  itemView.findViewById(R.id.tvTodoTitle)
        val checkbox : CheckBox = itemView.findViewById(R.id.cbTodoStatus)
        fun bind(todo: Todo, onTodoClick: (Int) -> Unit){
            todoTitle.text = todo.todo
            checkbox.isChecked = todo.completed

            itemView.setOnClickListener {
                onTodoClick(todo.id)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodosAdapter.TodosViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return TodosAdapter.TodosViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodosAdapter.TodosViewHolder, position: Int) {
        holder.bind(todosList[position], onTodoClick)
    }

    override fun getItemCount(): Int {
        return todosList.size
    }

    fun updateData(newTodoList: List<Todo>) {
        this.todosList = newTodoList
        notifyDataSetChanged()
    }
}