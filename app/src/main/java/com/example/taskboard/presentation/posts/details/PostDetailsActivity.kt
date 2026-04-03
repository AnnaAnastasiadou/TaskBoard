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

    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
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
            viewModel.savePost()
        }

        binding.btnAddTag.setOnClickListener {
            viewModel.addEmptyTag()
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

