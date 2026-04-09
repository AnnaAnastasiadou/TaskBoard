package com.example.taskboard.presentation.todos.details

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.taskboard.R
import com.example.taskboard.databinding.TodoDetailsActivityBinding
import com.example.taskboard.presentation.common.showErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TodoDetailsActivity : AppCompatActivity(R.layout.todo_details_activity) {
    private val viewModel: TodoDetailsViewModel by viewModels()

    private lateinit var binding: TodoDetailsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TodoDetailsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMode()
        setupToolBar()
        setupListeners()
        observeUiEvents()
        observeUiState()
    }

    private fun setupMode() {
        val todoId = intent.getIntExtra("todo_id", -1)
        val isEditMode = todoId != -1

        binding.topBar.title = if (isEditMode) "Edit Todo #$todoId" else "New Todo"
        binding.btnSave.text = if (isEditMode) "Update" else "Add"
        binding.btnDelete.isVisible = isEditMode
        binding.spacer.isVisible = isEditMode
    }

    private fun setupToolBar() {
        binding.topBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            lifecycleScope.launch {
                viewModel.saveTodo()
            }
        }

        binding.btnDelete.setOnClickListener {
            lifecycleScope.launch {
                viewModel.deleteTodo()
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.etTitle.doAfterTextChanged { text ->
            viewModel.updateTitle(text.toString())
        }

        binding.stateLayout.btnRetry.setOnClickListener {
            viewModel.onRetry()
        }
    }

    private fun observeUiEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is TodoUiEvent.NavigateBack -> finish()
                    }
                }
            }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.todoDetails.isVisible = state.data != null
                    binding.stateLayout.root.isVisible =
                        state.status == ScreenStatus.LOADING_DATA || state.status == ScreenStatus.ERROR
                    binding.stateLayout.progressBar.isVisible = state.status == ScreenStatus.LOADING_DATA
                    val hasError = state.status == ScreenStatus.ERROR
                    binding.stateLayout.btnRetry.isVisible = hasError
                    binding.stateLayout.tvError.isVisible = hasError
                    if (hasError) {
                        binding.stateLayout.tvError.text = getString(R.string.unable_to_retrieve_data)
                    }
                    val isBusy = state.status == ScreenStatus.SAVING || state.status == ScreenStatus.DELETING

                    binding.btnDelete.isEnabled = !isBusy
                    binding.btnSave.isEnabled = !isBusy
                    binding.btnCancel.isEnabled = !isBusy

                    state.data?.let { todo ->
                        if (binding.etTitle.text.toString() != todo.title) {
                            binding.etTitle.setText(todo.title)
                        }

                        val errors = state.validationError
                        binding.tilTitle.error = errors.titleError
                    }
                    binding.tilTitle.error = state.validationError.titleError

                    state.snackBarMessage?.let {
                        showErrorMessage(
                            rootView = binding.root,
                            message = it,
                            isError = hasError
                        )
                        viewModel.clearSnackBarMessage()
                    }
                }
            }
        }
    }
}