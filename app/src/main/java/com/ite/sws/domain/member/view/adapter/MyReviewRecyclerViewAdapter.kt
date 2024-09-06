package com.ite.sws.domain.member.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ite.sws.databinding.ItemReviewBinding
import com.ite.sws.domain.member.data.GetMemberReviewRes

class MyReviewRecyclerViewAdapter(
    private val reviews: List<GetMemberReviewRes>
) : RecyclerView.Adapter<MyReviewRecyclerViewAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.bind(review)
    }

    override fun getItemCount(): Int = reviews.size

    inner class ReviewViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(review: GetMemberReviewRes) {

            // 이미지 로드
            Glide.with(binding.root.context)
                .load(review.thumbnailImage)
                .into(binding.imgReview)
        }
    }
}
