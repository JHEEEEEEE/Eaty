package com.effort.feature.home.restaurant.detail.info

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.effort.feature.databinding.ItemBlogReviewBinding
import com.effort.presentation.model.home.restaurant.detail.blog.BlogReviewModel

/**
 * 블로그 리뷰 목록을 표시하는 RecyclerView Adapter
 * - 사용자가 블로그 리뷰를 클릭하면 해당 블로그 링크로 이동
 * - DiffUtil을 사용하여 변경된 데이터만 갱신
 */
class BlogReviewListAdapter :
    ListAdapter<BlogReviewModel, BlogReviewListAdapter.BlogReviewViewHolder>(DiffCallback) {

    class BlogReviewViewHolder(private val binding: ItemBlogReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * 블로그 리뷰 데이터를 UI에 바인딩하고, 클릭 시 해당 블로그 링크로 이동
         */
        fun bind(item: BlogReviewModel) {
            binding.blogTitle.text = item.title
            binding.blogContents.text = item.contents
            binding.blogDateTime.text = item.dateTime

            binding.root.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                it.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogReviewViewHolder {
        val binding =
            ItemBlogReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlogReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        /**
         * RecyclerView의 성능을 최적화하기 위한 DiffUtil 콜백
         * - 같은 URL을 가진 블로그 리뷰는 동일한 아이템으로 간주
         */
        private val DiffCallback = object : DiffUtil.ItemCallback<BlogReviewModel>() {
            override fun areItemsTheSame(oldItem: BlogReviewModel, newItem: BlogReviewModel): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: BlogReviewModel, newItem: BlogReviewModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}