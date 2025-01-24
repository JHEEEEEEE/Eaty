package com.effort.feature.home.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.databinding.ItemSuggestionBinding
import com.effort.presentation.model.home.KeywordModel

class SuggestionListAdapter(
    private val onClick: (String) -> Unit
) : ListAdapter<KeywordModel, SuggestionListAdapter.SuggestionViewHolder>(diffCallback) {

    inner class SuggestionViewHolder(private val binding: ItemSuggestionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: KeywordModel) {
            with(binding) {
                suggestionKeyword.text = item.keyword

                root.setOnClickListener {
                    val keyword = item.keyword
                    onClick(keyword)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val binding =
            ItemSuggestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SuggestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("Adapter", "Binding item: $item at position: $position")
        holder.bind(item)
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<KeywordModel>() {
            override fun areItemsTheSame(oldItem: KeywordModel, newItem: KeywordModel): Boolean {
                return oldItem.keyword == newItem.keyword
            }

            override fun areContentsTheSame(oldItem: KeywordModel, newItem: KeywordModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}