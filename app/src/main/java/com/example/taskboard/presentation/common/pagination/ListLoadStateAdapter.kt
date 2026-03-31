package com.example.taskboard.presentation.common.pagination

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskboard.R

class ListLoadStateAdapter(
    private val onRetry: () -> Unit
) : RecyclerView.Adapter<ListLoadStateAdapter.LoadStateViewHolder>() {
    private var state: ListLoadState = ListLoadState.Hidden

    fun setState(newState: ListLoadState) {
        val hadItem = state != ListLoadState.Hidden
        val hasItem = newState != ListLoadState.Hidden

        state = newState

        when {
            !hadItem && hasItem -> notifyItemInserted(0)
            hadItem && !hasItem -> notifyItemRemoved(0)
            hadItem && hasItem -> notifyItemChanged(0)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LoadStateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.load_state, parent,false)
        return LoadStateViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: LoadStateViewHolder,
        position: Int
    ) {
        holder.bind(state, onRetry)
    }

    override fun getItemCount(): Int {
        return if(state is ListLoadState.Hidden) 0 else 1
    }

    class LoadStateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val progressBar = itemView.findViewById<ProgressBar>(R.id.progress_bar)
        private val errorText = itemView.findViewById<TextView>(R.id.tvError)
        private val retryButton = itemView.findViewById<Button>(R.id.btn_retry)

        fun bind(state: ListLoadState, onRetry: () -> Unit) {
            when(state) {
                is ListLoadState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    errorText.visibility = View.GONE
                    retryButton.visibility = View.GONE
                }
                is ListLoadState.Error -> {
                    progressBar.visibility = View.GONE
                    errorText.visibility= View.VISIBLE
                    errorText.text = state.message
                    retryButton.visibility = View.VISIBLE

                    retryButton.setOnClickListener { onRetry() }
                }
                is ListLoadState.NetworkError -> {
                    progressBar.visibility = View.GONE
                    errorText.visibility= View.VISIBLE
                    errorText.text = state.message
                    retryButton.visibility = View.GONE
                }

                is ListLoadState.Hidden -> {
                    progressBar.visibility = View.GONE
                    errorText.visibility = View.GONE
                    retryButton.visibility = View.GONE
                }

            }
        }
    }
}