package com.effort.feature.home.restaurant.detail.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.databinding.ItemCommentBinding
import com.effort.presentation.model.home.restaurant.detail.comment.CommentModel

/**
 * 댓글 목록을 표시하는 RecyclerView Adapter
 * - 사용자가 입력한 댓글을 리스트로 관리
 */
class CommentAdapter : ListAdapter<CommentModel, CommentAdapter.CommentViewHolder>(DiffCallback) {

    class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * 댓글 데이터를 UI에 바인딩
         */
        fun bind(item: CommentModel) {
            with(binding) {
                userNickname.text = item.userNickname
                commentTimestamp.text = item.timestamp
                commentContent.text = item.content
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        /**
         * RecyclerView의 성능을 최적화하기 위한 DiffUtil 콜백
         * - 같은 userId를 가진 댓글은 동일한 아이템으로 간주
         */
        val DiffCallback = object : DiffUtil.ItemCallback<CommentModel>() {
            override fun areItemsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}