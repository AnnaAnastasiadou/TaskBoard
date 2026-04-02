package com.example.taskboard.presentation.posts.details

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.taskboard.R
import com.example.taskboard.databinding.ItemTagEditBinding
import com.example.taskboard.databinding.PostDetailsActivityBinding
import com.google.android.material.chip.Chip
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

        binding.topBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val postId = intent.getIntExtra("post_id", -1)

        if (postId == -1) {
            binding.topBar.title = "New Post"
            binding.btnSave.text = "Add"
            binding.btnDelete.isVisible = false
            binding.spacer.isVisible = false
        } else {
            binding.topBar.title = "Edit Post #$postId"
            binding.btnSave.text = "Update"
            binding.btnDelete.isVisible = true
            binding.spacer.isVisible = true
        }

        binding.btnAddTag.setOnClickListener {
            viewModel.addEmptyTag()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.data?.let { post ->
                        binding.etTitle.setText(post.title)
                        binding.etBody.setText(post.body)
                        if (binding.postTagsGroup.childCount != post.tags.size) {
                            displayTags(post.tags)
                        }
                    }
                }
            }
        }
    }

    private fun displayTags(tags: List<String>) {
        binding.postTagsGroup.removeAllViews()
        tags.forEachIndexed { tagIndex, tagText ->
            val tagBinding =
                ItemTagEditBinding.inflate(layoutInflater, binding.postTagsGroup, false)

            tagBinding.etTagName.setText(tagText)

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

