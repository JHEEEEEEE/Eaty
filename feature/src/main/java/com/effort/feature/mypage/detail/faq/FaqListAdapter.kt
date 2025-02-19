package com.effort.feature.mypage.detail.faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.databinding.ItemFaqBinding
import com.effort.presentation.model.mypage.detail.faq.FaqModel

/**
 * FAQ 목록을 표시하는 RecyclerView Adapter
 * - 각 항목을 클릭하면 해당 질문의 답변을 펼치거나 숨긴다.
 * - 질문 상태는 `expandedStates` 맵을 사용하여 관리
 */
class FaqListAdapter : ListAdapter<FaqModel, FaqListAdapter.FaqViewHolder>(diffCallback) {

    private val expandedStates = mutableMapOf<Int, Boolean>() // 질문의 펼침 상태 관리

    inner class FaqViewHolder(private val binding: ItemFaqBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * FAQ 데이터를 UI에 바인딩하고 클릭 이벤트를 설정
         * - `isExpanded` 값에 따라 답변 컨테이너의 가시성을 조절
         * - 클릭 시 `onItemClicked` 콜백을 실행하여 펼침 상태를 토글
         */
        fun bind(item: FaqModel, isExpanded: Boolean, onItemClicked: () -> Unit) {
            with(binding) {
                faqCategory.text = item.category
                faqQuestion.text = item.question
                answerTextView.text = item.answer

                answerContainer.visibility =
                    if (isExpanded) View.VISIBLE else View.GONE // 답변 표시 여부 조절
            }

            itemView.setOnClickListener { onItemClicked() } // 클릭 시 펼침 상태 변경
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val binding = ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FaqViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        val item = getItem(position)
        val isExpanded = expandedStates[position] ?: false // 펼침 상태 가져오기

        holder.bind(item, isExpanded) {
            expandedStates[position] = !isExpanded // 상태 변경
            notifyItemChanged(position) // UI 업데이트
        }
    }

    companion object {
        /**
         * RecyclerView의 성능을 최적화하기 위한 DiffUtil 콜백
         * - 같은 `question` 값을 가진 항목은 동일한 아이템으로 간주
         */
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