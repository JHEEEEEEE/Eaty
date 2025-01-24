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

class BlogReviewListAdapter :
    ListAdapter<BlogReviewModel, BlogReviewListAdapter.BlogReviewViewHolder>(DiffCallback) {

    class BlogReviewViewHolder(private val binding: ItemBlogReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BlogReviewModel) {
            with(binding){
                title.text = item.title
                contents.text = item.contents
                dateTime.text = item.dateTime

                root.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                    it.context.startActivity(intent)
                }
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
        private val DiffCallback = object : DiffUtil.ItemCallback<BlogReviewModel>() {
            override fun areItemsTheSame(
                oldItem: BlogReviewModel, newItem: BlogReviewModel
            ): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(
                oldItem: BlogReviewModel, newItem: BlogReviewModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}