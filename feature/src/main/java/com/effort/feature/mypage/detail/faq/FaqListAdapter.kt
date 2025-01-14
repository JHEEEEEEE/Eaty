package com.effort.feature.mypage.detail.faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.databinding.ItemFaqBinding
import com.effort.presentation.model.mypage.detail.faq.FaqModel

class FaqListAdapter : ListAdapter<FaqModel, FaqListAdapter.FaqViewHolder>(diffCallback) {

    private val expandedStates = mutableMapOf<Int, Boolean>()

    class FaqViewHolder(private val binding: ItemFaqBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FaqModel, isExpanded: Boolean, onItemClicked: () -> Unit) {
            with(binding) {
                faqCategory.text = item.category
                faqQuestion.text = item.question
                answerTextView.text = item.answer

                // 펼침 상태에 따라 답변 컨테이너 표시/숨김
                answerContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE
            }

            // 클릭 이벤트로 펼침 상태 토글
            itemView.setOnClickListener {
                onItemClicked()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val binding = ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FaqViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        val item = getItem(position)
        val isExpanded = expandedStates[position] ?: false

        holder.bind(item, isExpanded) {
            expandedStates[position] = !isExpanded
            notifyItemChanged(position)
        }
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<FaqModel>() {
            override fun areItemsTheSame(oldItem: FaqModel, newItem: FaqModel): Boolean {
                return oldItem.question == newItem.question
            }

            override fun areContentsTheSame(oldItem: FaqModel, newItem: FaqModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
