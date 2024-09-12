package com.ite.sws.domain.review.view.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ite.sws.R
import com.ite.sws.domain.review.data.GetReviewRes


class ReviewAdapter(
    private val reviews: List<GetReviewRes>,
    private val onThumbnailClick: (GetReviewRes) -> Unit

) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review_detail, parent, false)

        return ReviewViewHolder(view)

    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.bind(review)
        holder.itemView.findViewById<ImageView>(R.id.thumbnail_image).setOnClickListener {
            onThumbnailClick(review)
        }
    }

    override fun getItemCount(): Int = reviews.size

    fun resizeVideoView(videoView: VideoView, videoWidth: Int, videoHeight: Int): Pair<Int, Int> {
        val aspectRatio = videoWidth.toFloat() / videoHeight.toFloat()
        val screenWidth = videoView.context.resources.displayMetrics.widthPixels

        val toolbarHeight = getToolbarHeight(videoView)
        val bottomNavHeight = getBottomNavHeight(videoView)

        val availableHeight = videoView.context.resources.displayMetrics.heightPixels - toolbarHeight - bottomNavHeight

        val newHeight = (screenWidth / aspectRatio).toInt()
        val finalHeight = if (newHeight > availableHeight) availableHeight else newHeight

        val videoViewParams = videoView.layoutParams
        videoViewParams.width = screenWidth
        videoViewParams.height = finalHeight
        videoView.layoutParams = videoViewParams

        return Pair(screenWidth, finalHeight)
    }

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val videoView = itemView.findViewById<VideoView>(R.id.video_view)
        private val thumbnailImage = itemView.findViewById<ImageView>(R.id.thumbnail_image)

        fun bind(review: GetReviewRes) {
            Glide.with(itemView.context)
                .load(review.thumbnailImage)
                .into(thumbnailImage)

            videoView.setOnPreparedListener { mediaPlayer ->
                val (videoWidth, videoHeight) = (itemView.context as? Fragment)?.let {
                    (it as? ReviewAdapter)?.resizeVideoView(videoView, mediaPlayer.videoWidth, mediaPlayer.videoHeight)
                } ?: Pair(0, 0)

                val thumbnailParams = thumbnailImage.layoutParams
                thumbnailParams.width = videoWidth
                thumbnailParams.height = videoHeight
                thumbnailImage.layoutParams = thumbnailParams

                mediaPlayer.start()
            }
        }
    }

    private fun getToolbarHeight(view: View): Int {
        val toolbar = (view.context as? Activity)?.findViewById<View>(R.id.toolbar)
        return toolbar?.height ?: 0
    }

    private fun getBottomNavHeight(view: View): Int {
        val bottomNav = (view.context as? Activity)?.findViewById<View>(R.id.navigation_main)
        return bottomNav?.height ?: 0
    }
}
