package com.effort.feature.mypage.detail.notice

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.effort.feature.R
import com.effort.feature.databinding.ItemNoticeBinding
import com.effort.presentation.model.mypage.detail.notice.NoticeModel

class NoticeListAdapter :
    ListAdapter<NoticeModel, NoticeListAdapter.NoticeViewHolder>(diffCallback) {

    private val expandedStates = mutableMapOf<Int, Boolean>()

    class NoticeViewHolder(private val binding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NoticeModel, isExpanded: Boolean, onItemClicked: () -> Unit) {
            with(binding) {
                noticeCategory.text = item.category
                noticeTitle.text = item.title
                noticeDescription.text = item.description

                // Glide를 사용하여 이미지 로드
                if (item.imageUrl.isEmpty()) {
                    noticeImageView.visibility = View.GONE // 이미지 URL이 없을 경우 숨김
                } else {
                    noticeImageView.visibility = View.VISIBLE
                    Glide.with(noticeImageView.context)
                        .load(item.imageUrl) // Firestore에서 가져온 이미지 URL
                        .placeholder(R.drawable.placeholder_image) // 로딩 중 보여줄 이미지
                        .error(R.drawable.error_image) // 로드 실패 시 보여줄 이미지
                        .into(noticeImageView)
                }
                Log.d("NoticeModel", "URL: ${item.imageUrl}") // NoticeModel

                // 펼침 상태에 따라 답변 컨테이너 표시/숨김
                noticeContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE
            }

            // 클릭 이벤트로 펼침 상태 토글
            itemView.setOnClickListener {
                onItemClicked()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val binding = ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val item = getItem(position)
        val isExpanded = expandedStates[position] ?: false

        holder.bind(item, isExpanded) {
            expandedStates[position] = !isExpanded
            notifyItemChanged(position)
        }
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<NoticeModel>() {
            override fun areItemsTheSame(oldItem: NoticeModel, newItem: NoticeModel): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: NoticeModel, newItem: NoticeModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}