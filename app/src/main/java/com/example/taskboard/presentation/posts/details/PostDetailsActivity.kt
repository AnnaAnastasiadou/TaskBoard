package com.example.taskboard.presentation.posts.details

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.taskboard.R
import com.example.taskboard.databinding.ItemTagEditBinding
import com.example.taskboard.databinding.PostDetailsActivityBinding
import com.example.taskboard.presentation.common.showErrorMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetailsActivity : AppCompatActivity(R.layout.post_details_activity) {
    private val viewModel: PostDetailsViewModel by viewModels()
    private lateinit var binding: PostDetailsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PostDetailsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupMode()
        setupListeners()
        observeUiState()
        observeUiEvents()
    }

    private fun observeUiEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is PostUiEvent.NavigateBack -> finish()
                    }
                }
            }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.postDetails.isVisible = state.data != null
                    binding.stateLayout.root.isVisible =
                        state.status == ScreenStatus.LOADING_DATA || state.status == ScreenStatus.ERROR
                    binding.stateLayout.progressBar.isVisible =
                        state.status == ScreenStatus.LOADING_DATA
                    val hasError = state.status == ScreenStatus.ERROR
                    binding.stateLayout.btnRetry.isVisible = hasError
                    binding.stateLayout.tvError.isVisible = hasError
                    if (hasError) {
                        binding.stateLayout.tvError.text = "Unable to retrieve data"
                    }

                    val isBusy = state.status == ScreenStatus.SAVING || state.status == ScreenStatus.DELETING

                    binding.btnDelete.isEnabled = !isBusy
                    binding.btnSave.isEnabled = !isBusy
                    binding.btnCancel.isEnabled = !isBusy
                    binding.btnAddTag.isEnabled = !isBusy

                    state.data?.let { post ->
                        if (binding.etTitle.text.toString() != post.title) {
                            binding.etTitle.setText(post.title)
                        }

                        if (binding.etBody.text.toString() != post.body) {
                            binding.etBody.setText(post.body)
                        }

                        val errors = state.validationError
                        binding.tilTitle.error = errors.titleError
                        binding.tilBody.error = errors.bodyError

                        if (errors.tagsError != null && errors.errorTagIndices.isEmpty()) {
                            binding.tvTagsError.text = errors.tagsError
                            binding.tvTagsError.visibility = View.VISIBLE
                        } else {
                            binding.tvTagsError.visibility = View.GONE
                        }

                        if (binding.postTagsGroup.childCount != post.tags.size) {
                            displayTags(post.tags, errors.errorTagIndices, errors.tagsError ?: "")
                        } else {
                            updateTagErrors(errors.errorTagIndices, errors.tagsError ?: "")
                        }
                    }

                    state.snackbarMessage?.let {
                        showErrorMessage(
                            rootView = binding.root,
                            message = it,
                            isError = hasError
                        )
                        viewModel.clearSnackbarMessage()
                    }
                }
            }
        }
    }
    private fun setupMode() {
        val postId = intent.getIntExtra("post_id", -1)
        val isEditMode = postId != -1

        binding.topBar.title = if (isEditMode) "Edit Post #$postId" else "New Post"
        binding.btnSave.text = if (isEditMode) "Update" else "Add"
        binding.btnDelete.isVisible = isEditMode
        binding.spacer.isVisible = isEditMode
    }

    private fun setupToolbar() {
        binding.topBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            lifecycleScope.launch {
                viewModel.savePost()
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnDelete.setOnClickListener {
            lifecycleScope.launch {
                viewModel.deletePost()
            }
        }

        binding.btnAddTag.setOnClickListener {
            viewModel.addEmptyTag()
        }

        binding.stateLayout.btnRetry.setOnClickListener {
            viewModel.onRetry()
        }

        binding.etTitle.doAfterTextChanged { text ->
            viewModel.updateTitle(text.toString())
        }

        binding.etBody.doAfterTextChanged { text ->
            viewModel.updateBody(text.toString())
        }
    }

    private fun updateTagErrors(invalidIndices: Set<Int>, errorMessage: String) {
        for (i in 0 until binding.postTagsGroup.childCount) {
            val tagView = binding.postTagsGroup.getChildAt(i)
            val tagBinding = ItemTagEditBinding.bind(tagView)

            tagBinding.etTagName.error =
                if (invalidIndices.contains(i)) errorMessage else null
        }
    }

    private fun displayTags(tags: List<String>, invalidIndices: Set<Int>, errorMessage: String) {
        binding.postTagsGroup.removeAllViews()
        tags.forEachIndexed { tagIndex, tagText ->
            val tagBinding =
                ItemTagEditBinding.inflate(layoutInflater, binding.postTagsGroup, false)

            tagBinding.etTagName.setText(tagText)

            if (invalidIndices.contains(tagIndex)) {
                tagBinding.etTagName.error = errorMessage
            }

            tagBinding.etTagName.doAfterTextChanged { text ->
                viewModel.updateTagAt(tagIndex, text.toString())
            }

            tagBinding.btnRemoveTag.setOnClickListener {
                viewModel.removeTagAt(tagIndex)
            }

            binding.postTagsGroup.addView(tagBinding.root)
        }
    }
}

