package com.ite.sws.domain.review.view.ui

import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ite.sws.R
import com.ite.sws.domain.review.api.repository.ReviewRepository
import com.ite.sws.domain.review.data.GetReviewRes
import com.ite.sws.domain.review.view.adapter.ReviewAdapter

/**
 * 리뷰 프래그먼트
 * @author 정은지
 * @since 2024.08.24
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.24  	정은지       최초 생성
 * </pre>
 */



class ReviewFragment : Fragment() {

    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var reviews: List<GetReviewRes>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_review, container, false)

        recyclerView = view.findViewById(R.id.recyclerView_reviews)
        recyclerView.layoutManager = LinearLayoutManager(context)

        ReviewRepository().getReviews(page = 0, size = 10, onSuccess = { fetchedReviews ->
            reviews = fetchedReviews
            reviewAdapter = ReviewAdapter(reviews) { review ->
                playVideoAutomatically(view.findViewById(R.id.video_view), Uri.parse(review.shortFormUrl))
            }
            recyclerView.adapter = reviewAdapter
        }, onFailure = { throwable ->
            Toast.makeText(requireContext(), "리뷰 업로드 실패: ${throwable.message}", Toast.LENGTH_SHORT).show()
        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                handleAutoPlayVideo()
            }
        })

        return view
    }

    private fun handleAutoPlayVideo() {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

        for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as? ReviewAdapter.ReviewViewHolder
            viewHolder?.let {
                if (isFullyVisible(it.itemView)) {
                    playVideoAutomatically(it.itemView.findViewById(R.id.video_view), Uri.parse(reviews[i].shortFormUrl))
                } else {
                    it.itemView.findViewById<VideoView>(R.id.video_view).stopPlayback()
                }
            }
        }
    }

    private fun playVideoAutomatically(videoView: VideoView, videoUri: Uri) {
        if (videoView.isPlaying) {
            videoView.stopPlayback()
        }

        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.setOnVideoSizeChangedListener { _, width, height ->
                (recyclerView.adapter as ReviewAdapter).resizeVideoView(videoView, width, height)
            }
            mediaPlayer.start()
        }

        videoView.setOnCompletionListener { mediaPlayer -> mediaPlayer.start() }
    }

    private fun isFullyVisible(view: View): Boolean {
        val itemRect = Rect()
        val isVisible = view.getGlobalVisibleRect(itemRect)
        return isVisible && itemRect.height() == view.height
    }
}

