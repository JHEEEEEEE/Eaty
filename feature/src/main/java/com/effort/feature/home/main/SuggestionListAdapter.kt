package com.effort.feature.home.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.databinding.ItemSuggestionBinding
import com.effort.presentation.model.home.KeywordModel

/**
 * 검색어 추천 목록을 표시하는 RecyclerView Adapter
 * 사용자가 추천 검색어를 선택하면 onClick 콜백을 통해 전달
 */
class SuggestionListAdapter(
    private val onClick: (String) -> Unit
) : ListAdapter<KeywordModel, SuggestionListAdapter.SuggestionViewHolder>(diffCallback) {

    inner class SuggestionViewHolder(private val binding: ItemSuggestionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * 검색어 데이터를 View에 바인딩하고 클릭 이벤트를 설정
         */
        fun bind(item: KeywordModel) {
            binding.suggestionKeyword.text = item.keyword
            binding.root.setOnClickListener { onClick(item.keyword) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val binding =
            ItemSuggestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SuggestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        /**
         * RecyclerView의 성능을 최적화하기 위한 DiffUtil 콜백
         * - 검색어(키워드)를 기준으로 같은 항목인지 판단
         */
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