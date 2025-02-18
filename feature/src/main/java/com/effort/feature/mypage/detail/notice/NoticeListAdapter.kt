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

/**
 * 공지사항 목록을 표시하는 RecyclerView Adapter
 * - 사용자가 공지사항을 클릭하면 세부 내용을 펼치거나 숨긴다.
 * - `expandedStates` 맵을 사용하여 펼침 상태를 관리
 */
class NoticeListAdapter :
    ListAdapter<NoticeModel, NoticeListAdapter.NoticeViewHolder>(diffCallback) {

    private val expandedStates = mutableMapOf<Int, Boolean>() // 공지사항의 펼침 상태 관리

    inner class NoticeViewHolder(private val binding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * 공지사항 데이터를 UI에 바인딩하고 클릭 이벤트를 설정
         * - `isExpanded` 값에 따라 공지 상세 내용을 표시하거나 숨김
         * - 클릭 시 `onItemClicked` 콜백을 실행하여 펼침 상태를 토글
         */
        fun bind(item: NoticeModel, isExpanded: Boolean, onItemClicked: () -> Unit) {
            with(binding) {
                noticeCategory.text = item.category
                noticeTitle.text = item.title
                noticeDescription.text = item.description

                // 이미지 URL이 없으면 숨기고, 있으면 Glide로 로드
                if (item.imageUrl.isEmpty()) {
                    noticeImageView.visibility = View.GONE
                } else {
                    noticeImageView.visibility = View.VISIBLE
                    Glide.with(noticeImageView.context)
                        .load(item.imageUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(noticeImageView)
                }

                Log.d("NoticeModel", "공지사항 이미지 URL: ${item.imageUrl}")

                noticeContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE // 펼침 상태 적용
            }

            itemView.setOnClickListener { onItemClicked() } // 클릭 시 펼침 상태 변경
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val binding = ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
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
         * - 같은 `title` 값을 가진 항목은 동일한 아이템으로 간주
         */
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