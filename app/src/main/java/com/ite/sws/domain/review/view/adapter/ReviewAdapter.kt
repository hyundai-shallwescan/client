package com.ite.sws.domain.review.view.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ite.sws.R
import com.ite.sws.domain.review.data.GetReviewRes


class ReviewAdapter(
    private val reviews: List<GetReviewRes>
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review_detail, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.bind(review)
    }

    override fun getItemCount(): Int = reviews.size

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val videoView = itemView.findViewById<VideoView>(R.id.video_view)
        private val thumbnailImage = itemView.findViewById<ImageView>(R.id.thumbnail_image)


        fun bind(review: GetReviewRes) {
            Glide.with(itemView.context)
                .load(review.thumbnailImage)
                .into(thumbnailImage)
            videoView.setVideoURI(Uri.parse(review.shortFormUrl))
        }

        fun playVideo() {
            if (!videoView.isPlaying) {
                videoView.start()
            }
        }

        fun stopVideo() {
            if (videoView.isPlaying) {
                videoView.pause()
            }
        }

    }
}
